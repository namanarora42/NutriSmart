package edu.cmu.namana.nutrismart.nutrismartserver;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * This is the class that connects to the third party API and brings in the information that the user has requested.
 */
public class NutriSmart {
    JSONObject request, response, rawResponse;
    long responseTime;

    /**
     * Return the stored response
     * @return response JSON object
     */
    public JSONObject getResponse() {
        return response;
    }

    /**
     * Returns the raw response from the API
     * @return raw API's JSON response
     */
    public JSONObject getRawResponse() {
        return rawResponse;
    }

    /**
     * Return the response time
     * @return time taken between receiving user input to returning output fetched from API
     */
    public long getResponseTime() {
        return responseTime;
    }

    /**
     * Get the request JSON object
     * @return JSON object
     */
    public JSONObject getRequest() {
        return request;
    }

    /**
     * Sets up the class instance for the client's request
     * @param request JSON request from client
     */
    public void setup(JSONObject request) {
        this.request = request;
        this.responseTime = 0;
    }

    /**
     * Calls the API, parses the responses and extracts the useful info.
     * The useful info is marshalled into a new JSON object that can be passed on to the client
     */
    public void performQuery() {
        long startTime = System.currentTimeMillis(); //track time taken to process
        JSONObject apiResponse = callAPI(request); //makes a call to the API with user query
        this.rawResponse = apiResponse;
        if (apiResponse.has("error")) { //if an error was reported by the API
            this.response = apiResponse;
            long endTime = System.currentTimeMillis();
            this.responseTime = endTime - startTime; //track time taken to process
            return; //don't proceed
        }
        JSONObject appResponse = new JSONObject(); //setting up an object to be sent back to the requesting client
        JSONArray calorieInfo = new JSONArray();
        JSONArray imageInfo = new JSONArray();
        JSONArray foods = apiResponse.getJSONArray("foods");
        //constructing the output to be sent to the client
        for (Object foodObj : foods) {
            JSONObject food = (JSONObject) foodObj;

            //preparing the response format to be sent to the client so that the client does not have to do any additional processing
            String responseString = String.format("\n" +
                            "-----------------------------------------------------" + "\n" +
                            "Food Name - %s" + "\n" +
                            "Servings - %.1f %s" + "\n" +
                            "Serving Weight - %d grams" + "\n" +
                            "Calories - %.1f" + "\n",
                    //fetching relevant information from the JSON response sent by the API
                    food.getString("food_name"), food.getDouble("serving_qty"), food.getString("serving_unit"),
                    food.getInt("serving_weight_grams"), food.getDouble("nf_calories"));
            calorieInfo.put(new JSONObject().put("food", responseString));
            imageInfo.put(new JSONObject().put("image",
                    (food.getJSONObject("photo").isNull("highres") ?
                            (food.getJSONObject("photo").isNull("thumb") ? "No Picture available" :
                                    food.getJSONObject("photo").getString("thumb")) :
                            food.getJSONObject("photo").getString("highres"))));
        }
        //adding JSON Arrays to the output JSON object to be sent to client
        appResponse.put("foods", calorieInfo);
        appResponse.put("images", imageInfo);
        this.response = appResponse;
        long endTime = System.currentTimeMillis();
        this.responseTime = endTime - startTime; //track time taken to process
    }

    /**
     * Makes the call to the Nutritionix API and fetches response
     * @param query Query JSON object
     * @return JSON Object returned from the third party API
     */
    private static JSONObject callAPI(JSONObject query)  {
        HttpURLConnection con;
        try {
            URL url = new URL("https://trackapi.nutritionix.com/v2/natural/nutrients");
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST"); //Making a post request
            con.setRequestProperty("Content-Type", "application/json"); //JSON will be transferred
            con.setRequestProperty("x-app-id", "f19ec5a9"); //Part of api key
            con.setRequestProperty("x-app-key", "24b7c3a4570cd8ed3a74bc9b8a971d89"); //part of api key
            con.setRequestProperty("Accept", "application/json"); //accept JSON
            con.setDoOutput(true); //requesting output to be sent
            try (OutputStream os = con.getOutputStream()) { //send data to API as POST request body
                byte[] input = query.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
        }
        catch (Exception e) { //if the API could not be reached
            return new JSONObject().put("error", "Could not connect to the API");
        }
        JSONObject response;
        try (BufferedReader br = new BufferedReader( //read data sent by the API
                new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder responseString = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                responseString.append(responseLine.trim());
            }
            response = new JSONObject(responseString.toString()); //generate JSON object from the data sent by API
        }
        catch (Exception e) { //If the API returns an error
            return new JSONObject().put("error", "The input query could not be understood.");
        }
        return response;

    }

}
