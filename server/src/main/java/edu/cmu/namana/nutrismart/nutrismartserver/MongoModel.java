package edu.cmu.namana.nutrismart.nutrismartserver;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

/**
 * This class acts as a model for handling communication with the MongoDB cluster, pushing data to it and getting data from it
 */
public class MongoModel {
    private static MongoCollection<Document> collection;

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
}
