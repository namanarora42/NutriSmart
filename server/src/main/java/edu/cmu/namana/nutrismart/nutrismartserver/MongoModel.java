package edu.cmu.namana.nutrismart.nutrismartserver;

import com.mongodb.client.*;
import static com.mongodb.client.model.Accumulators.*;
import static com.mongodb.client.model.Aggregates.*;

import com.mongodb.client.model.Sorts;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bson.Document;
import org.json.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.print.Doc;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class acts as a model for handling communication with the MongoDB cluster, pushing data to it and getting data from it
 */
public class MongoModel {
    private static MongoCollection<Document> collection;
    private ArrayList<Log> logs;
    private ArrayList<String> topFoods;
    private ArrayList<Integer> topFoodsCount;
    private double averageCalorie;
    private int totalQueries;
    private double averageResponseTime;

    /**
     * Initialize mongoDB instance
     * Gets the collection "logs" from database "nutrismart"
     */
    private static void init() {
        String uri = "mongodb+srv://admin:uknJnbOfhIpt66vN@cluster0.fpnl6.mongodb.net/myFirstDatabase?retryWrites=true&w=majority";
        MongoClient mongoClient = MongoClients.create(uri);
        MongoDatabase database = mongoClient.getDatabase("nutrismart");
        collection = database.getCollection("logs");
    }

    /**
     * Instantiates the class, calls initializer
     */
    public MongoModel() {
        init();
    }

    /**
     * Getter for top foods
     * @return list of top foods searched
     */
    public ArrayList<String> getTopFoods() {
        return topFoods;
    }

    /**
     * getter for top foods counts
     * @return number of times top foods were searched
     */
    public ArrayList<Integer> getTopFoodsCount() {
        return topFoodsCount;
    }

    /**
     * Getter for average calories
     * @return average calories per meal
     */
    public double getAverageCalorie() {
        return averageCalorie;
    }

    /**
     * Getter for total number of queries served
     * @return total queries served
     */
    public int getTotalQueries() {
        return totalQueries;
    }

    /**
     * Getter for list of logs
     * @return logs
     */
    public ArrayList<Log> getLogs() {
        return new ArrayList<>(logs);
    }

    /**
     * Getter for average response time
     * @return average response time
     */
    public double getAverageResponseTime() {
        return averageResponseTime;
    }

    /**
     * Method used to store a document in the mongo collection
     *
     * @param request    the servlet request object that called doPost
     * @param nutriSmart a reference to the model class, will be used to fetch request and response details
     */
    public void store(HttpServletRequest request, NutriSmart nutriSmart) {
        JSONObject payload = nutriSmart.getRequest(); //the JSON object containing the user's query
        if (nutriSmart.getResponse().has("error")) { //this is an error log
            //Creating a log object
            Log log = new Log(
                    new Timestamp(System.currentTimeMillis()).toString(), //current time
                    request.getRemoteAddr(), //address of the client
                    request.getHeader("user-agent"), //user agent of the client making the request
                    payload.getString("query"), //string query extracted from user's JSON request object
                    nutriSmart.getResponse().getString("error"), //error message
                    nutriSmart.getResponseTime() //time taken by the servlet to get the response from API
            );
            //writing a single log to MongoDB
            collection.insertOne(new Document(log.toJSON().toMap()));
            return;
        }
        JSONObject rawResponse = nutriSmart.getRawResponse(); //the JSON object returned by the API call
        JSONArray rawFoods = rawResponse.getJSONArray("foods"); //calorie details returned by API
        JSONArray foods = new JSONArray();
        JSONArray calories = new JSONArray();
        for (Object obj : rawFoods) {
            JSONObject food = (JSONObject) obj;
            foods.put(new JSONObject().put("food", food.getString("food_name")));
            calories.put(new JSONObject().put("calories", food.getDouble("nf_calories")));
        }
        //Creating a log object
        Log log = new Log(
                new Timestamp(System.currentTimeMillis()).toString(), //current time
                request.getRemoteAddr(), //address of the client
                request.getHeader("user-agent"), //user agent of the client making the request
                payload.getString("query"), //string query extracted from user's JSON request object
                foods, //array of JSON objects, each containing the details of a food item recognized
                calories, //array of JSON objects, each containing the calories in each food
                nutriSmart.getResponseTime() //time taken by the servlet to get the response from API
        );
        //writing a single log to MongoDB
        collection.insertOne(new Document(log.toJSON().toMap()));
    }

    /**
     * Prepares the data to be displayed on the analytics dashboard
     */
    public void prepareStats() {
        topFoods = new ArrayList<>();
        topFoodsCount = new ArrayList<>();
        AggregateIterable<Document> aggDocs;
        // Getting the top 5 most queries food items, and the number of times they were queried
        aggDocs = collection.aggregate(
                Arrays.asList(
                        unwind("$foods"),
                        group("$foods", sum("count", 1)),
                        sort(Sorts.descending("count")),
                        limit(5)
                )
        );
        for (Document docObj : aggDocs) {
            JSONObject doc = new JSONObject(docObj.toJson());
            topFoods.add(doc.getJSONObject("_id").getString("food"));
            topFoodsCount.add(doc.getInt("count"));
        }

        //Average calories per meal
        aggDocs = collection.aggregate(
                Arrays.asList(
                        unwind("$calories"),
                        group("$_id", sum("totalCalories", "$calories.calories")) ,//total calories in each meal
                        group(null, avg("average", "$totalCalories")) //average calories in each meal
                )
        );
        this.averageCalorie = aggDocs.first().getDouble("average");

        //setting number of total queries served
        this.totalQueries = Math.toIntExact(collection.countDocuments());

        //Calculating Average response time from logs
        aggDocs = collection.aggregate(
                Arrays.asList(
                        group(null, avg("average", "$responseTime")) //average calories in each meal
                )
        );
        this.averageResponseTime = aggDocs.first().getDouble("average");
    }

    /**
     * Prepares the logs to displayed on webpage
     */
    public void prepareLogs() {
        this.logs = new ArrayList<>();
        FindIterable<Document> docs = collection.find(); //get all documents stored in the mongoDB collection
        for (Document doc : docs) {
            logs.add(new Log(new JSONObject(doc.toJson())));
        }
    }
}
