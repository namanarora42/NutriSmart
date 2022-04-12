package edu.cmu.namana.nutrismart.nutrismartserver;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * This class implements a Log object.
 * A log object is used to contain information about a single log record, which is later pushed to the MongoDB collection.
 */
public class Log {
    private String timestamp, ip, userAgent, query, error;
    private JSONArray foods, calories;
    int responseTime;

    /**
     * Constructor
     * @param timestamp timestamp for when the request was received
     * @param ip ip name
     * @param userAgent user agent of client
     * @param query query string
     * @param foods array of foods contains food objects (JSON)
     * @param calories array of calorie details for foods in the query
     * @param responseTime time taken to fetch response
     */
    public Log(String timestamp, String ip, String userAgent, String query, JSONArray foods, JSONArray calories, long responseTime) {
        this.timestamp = timestamp;
        this.ip = ip;
        this.userAgent = userAgent;
        this.query = query;
        this.foods = foods;
        this.calories = calories;
        this.responseTime = Math.toIntExact(responseTime);
    }

    /**
     * Constructor for an error log
     * @param timestamp timestamp for when the request was received
     * @param ip ip name
     * @param userAgent user agent of client
     * @param query query string
     * @param error Error string
     * @param responseTime response time
     */
    public Log(String timestamp, String ip, String userAgent, String query, String error, long responseTime) {
        this.timestamp = timestamp;
        this.ip = ip;
        this.userAgent = userAgent;
        this.query = query;
        this.error = error;
        this.responseTime = Math.toIntExact(responseTime);
    }

    /**
     * Construct a log object from a JSON object
     * Usage: Convert documents received from MongoDB to Log objects
     * @param obj JSON Object
     */
    public Log(JSONObject obj) {
        this.timestamp = obj.getString("timestamp");
        this.ip = (obj.has("ip") ? obj.getString("ip") : "");
        this.userAgent = (obj.has("userAgent") ? obj.getString("userAgent") : "");
        this.query = (obj.has("query") ? obj.getString("query") : "");
        this.foods = (obj.has("foods") ? obj.getJSONArray("foods") : null);
        this.calories = (obj.has("calories") ? obj.getJSONArray("calories") : null);
        this.responseTime = (obj.has("responseTime") ? obj.getInt("responseTime") : 0);
        this.error = (obj.has("error") ? obj.getString("error") : "");
    }

    /**
     * Getter for timestamp
     * @return timestamp
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * Getter for IP address of client
     * @return IP address
     */
    public String getIp() {
        return ip;
    }

    /**
     * Getter for User Agent
     * @return UA of client
     */
    public String getUserAgent() {
        return userAgent;
    }

    /**
     * Getter for query
     * @return query sent by client
     */
    public String getQuery() {
        return query;
    }

    /**
     * Getter for foods recognized by API
     * @return array of foods
     */
    public JSONArray getFoods() {
        return (foods != null ? foods : null);
    }

    /**
     * Getter for calorie values returned by API
     * @return calories
     */
    public JSONArray getCalories() {
        return (calories != null ? calories : null);
    }

    /**
     * Getter for response time for the query processing
     * @return response time
     */
    public int getResponseTime() {
        return Math.toIntExact(responseTime);
    }

    /**
     * Getter for error string (if this is an error log)
     * @return error message
     */
    public String getError() {
        return error;
    }

    /**
     * Converts Log object to JSON object for sending to MongoDB
     * @return JSON Object
     */
    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("timestamp", timestamp);
        obj.put("ip", ip);
        obj.put("userAgent", userAgent);
        obj.put("query", query);
        obj.put("foods", foods);
        obj.put("calories", calories);
        obj.put("responseTime", responseTime);
        obj.put("error", error);
        return  obj;
    }
}
