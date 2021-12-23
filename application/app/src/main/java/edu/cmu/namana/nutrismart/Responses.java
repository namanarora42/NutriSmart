package edu.cmu.namana.nutrismart;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * This is a utility class that is used to encapsulate responses received from the server.
 * Each individual response is stored two array lists. One would contain the text to display.
 * Second list contains the bitmaps of images to display
 */
public class Responses {
    private ArrayList<String> responses;
    private ArrayList<Bitmap> images;
    private String errorMessage;

    /**
     * Constructor to initialize the object
     */
    public Responses() {
        responses = new ArrayList<>();
        images = new ArrayList<>();
    }

    /**
     * Setup responses object with an error initialized
     * @param errorMessage error message
     */
    public Responses(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Return the error message
     * @return error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Add a response and corresponding image
     * @param response response string
     * @param image bitmap of image
     */
    public void add(String response, Bitmap image) {
        responses.add(response);
        images.add(image);
    }

    /**
     * Getter for responses
     * @return list of responses
     */
    public ArrayList<String> getResponses() {
        return new ArrayList<>(responses);
    }

    /**
     * getter for images
     * @return list of bitmaps
     */
    public ArrayList<Bitmap> getImages() {
        return new ArrayList<>(images);
    }
}
