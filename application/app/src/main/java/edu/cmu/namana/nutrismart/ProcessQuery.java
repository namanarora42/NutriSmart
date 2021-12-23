package edu.cmu.namana.nutrismart;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

/**
 * This class implements methods that handle processing of user's query.
 * It will start an asynchronous task that works in the background on gathering data while the GUI remains response
 */
public class ProcessQuery {
    NutriSmart nutriSmart = null;

    /**
     * Entry point to start a task in background
     * @param query user's search query
     * @param nutriSmart instance of the calling class so the background thread can alert the main thread upon completion
     */
    public void query(String query, NutriSmart nutriSmart) {
        this.nutriSmart = nutriSmart;
        new AsyncProcessing().execute(query); //start asynchronous task and pass query to it
    }

    /**
     * This class implements methods for background task processing
     */
    private class AsyncProcessing extends AsyncTask<String, Void, Responses> {

        /**
         * Operations in this method are done in an asynchronous thread
         * @param strings parameters passed on to the execute method
         * @return Responses from the server
         */
        @Override
        protected Responses doInBackground(String... strings) {
            Responses responses = null;
            try { //data validation
                responses = query(strings[0]);
            } catch (JSONException e) {
                System.out.println("User query invalid");
            }
            catch (MalformedURLException m) { //connection error
                System.out.println("Could not setup connection with remote server");
            }
            return responses;
        }

        /**
         * Once background task is complete, this method is called
         * @param responses responses returned by doInBackground
         */
        protected void onPostExecute(Responses responses) {
            nutriSmart.responsesReady(responses);
        }

        /**
         * If an error occurs, cancel() is called in the background thread.
         * Once the background thread is complete, onCancelled gets called
         * @param responses responses returned by the background thread
         */
        protected void onCancelled(Responses responses) {
            System.out.println("Async Task was cancelled");
            //we passed an error message to the responses object when cancelling, we can retrieve it here and pass it back to the caller
            nutriSmart.error(responses.getErrorMessage());
        }

        /**
         * Utility method to process the query from user
         * @param query String query
         * @return Responses object that encapsulated response from API
         * @throws JSONException when JSON cannot be constructed from the received data
         * @throws MalformedURLException Incorrect URL
         */
        private Responses query(String query) throws JSONException, MalformedURLException {
            Responses responses = new Responses();
            JSONObject apiResponse = new JSONObject();
            try { //data validation for invalid data received from API
                apiResponse = getAPIResponse(query);
            } catch (JSONException e) {
                apiResponse.put("error", "Could not parse output from the API");
                e.printStackTrace();
            }
            if (apiResponse.has("error")) { //If the server has reported an error, we need to cancel this task and return to the main thread
                responses = new Responses(apiResponse.getString("error")); //add error message to responses object
                cancel(true); //cancel the task since an error has occurred
                return responses;
            }
            JSONArray foods = apiResponse.getJSONArray("foods");
            JSONArray images = apiResponse.getJSONArray("images");
            for (int i = 0; i<foods.length(); i++) { //extract all information received from the server
                JSONObject food = (JSONObject) foods.get(i);
                JSONObject image = (JSONObject) images.get(i);
                Bitmap bitmap = getRemoteImage(new URL(image.getString("image")));
                responses.add(food.getString("food"), bitmap); //add the calorie details and respective image to the responses object
            }
            return responses;
        }

        /**
         * Utility method for making a call to the remote server to get results
         * @param userQuery string user query
         * @return JSON Object that encapsulates response received from server
         * @throws JSONException if JSON cannot be formed with the response, will be handled by caller
         */
        private JSONObject getAPIResponse(String userQuery) throws JSONException {
            JSONObject response = new JSONObject();
            try {
                JSONObject query = new JSONObject().put("query", userQuery);
                //Servlet URL
                URL url = new URL("https://enigmatic-citadel-60457.herokuapp.com/nutri-servlet");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                //we will be using timeouts for handling connectivity issues
                //A timeout of 5000ms is set for both obtaining a connection and obtaining an data stream from the connection
                con.setConnectTimeout(6000);
                con.setReadTimeout(6000);
                //We will be using POST since we have to send data in JSON
                con.setRequestMethod("POST");
                con.setDoOutput(true);
                try (OutputStream os = con.getOutputStream()) {
                    byte[] input = query.toString().getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length); //write data to the stream
                }
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder responseString = new StringBuilder();
                    String responseLine;
                    //read data sent by the server
                    while ((responseLine = br.readLine()) != null) {
                        responseString.append(responseLine.trim());
                    }
                    //parse the data sent by the server as a JSON object
                    response = new JSONObject(responseString.toString());
                } catch (Exception e) { //Data validation (user did not enter food items, or the NLP engine does not recognize it)
                    response.put("error", "Your input was not understood. Please rephrase and try again.");
                }
            } catch (SocketTimeoutException e) { //connection timeout
                response.put("error", "Connection timed out.");
            } catch (Exception e) {
                e.printStackTrace();
                response.put("error", "Could not contact remote server.");
            }
            return response;
        }

        /**
         * Constructs a bitmap from a given image URL
         * Ref. Android Lab 8
         * @param url URL of image
         * @return Bitmap from URL
         */
        private Bitmap getRemoteImage(final URL url) {
            try {
                final URLConnection conn = url.openConnection();
                conn.connect();
                BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
                return BitmapFactory.decodeStream(bis);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
