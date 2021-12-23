package edu.cmu.namana.nutrismart;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

/**
 * This is the main class that handles drawing app elements and listens for events
 */
public class NutriSmart extends AppCompatActivity {

    /**
     * This method is the entry point into the application
     * @param savedInstanceState Bundle to use when app returns back to focus
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Using a spinner to show loading process
        ProgressBar spinner;
        spinner = (ProgressBar)findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);

        final NutriSmart nutriSmart = this; //saving an instance of this class to be passed on the async task handler

        Button submitButton = (Button)findViewById(R.id.submitButton); //submit button
        TextView textView = findViewById(R.id.results); //this is where results are displayed in the GUI
        EditText userInput = findViewById(R.id.userInput); //the user enters a text query here
        Button clearButton = (Button) findViewById(R.id.clear); //clear button


        /*
         * Even handler when submit button is pressed
         */
        submitButton.setOnClickListener(v -> {
            hideSoftKeyboard(this); //hide the keyboard when user is done typing
            spinner.setVisibility(View.VISIBLE); //start spinning
            textView.setText(R.string.fetching); //alert user about processing
            String query = userInput.getText().toString(); //get user's search query
            System.out.println("Received search query " + query);
            if (query.equals("")) { //checking for invalid mobile app input from user
                textView.setText(R.string.invalidInput); //alert user about invalid input
                spinner.setVisibility(View.GONE); //stop spinner
                hideAllImages(); //if any images were set, remove them.
                return;
            }
            ProcessQuery processQuery = new ProcessQuery();
            processQuery.query(query, nutriSmart); //gets response from server asynchronously
        });

        /*
         * Event handler when clear button is pressed
         */
        clearButton.setOnClickListener(v -> {
            hideSoftKeyboard(this); //hide keyboard
            spinner.setVisibility(View.GONE); //stop spinner
            textView.setText(""); //clear output text view
            userInput.setText(""); //clear input text field
            hideAllImages(); //clear all images
        });

    }

    /**
     * Utility method to hide the keyboard upon button press
     * https://stackoverflow.com/questions/4165414/how-to-hide-soft-keyboard-on-android-after-clicking-outside-edittext
     * @param activity activity
     */
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if(inputMethodManager.isAcceptingText()){
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(),
                    0
            );
        }
    }

    /**
     * This method is called by the process query object when the responses are ready
     * Now we can parse the responses and display them to the user
     * @param responses Responses object
     */
    public void responsesReady(Responses responses) {
        ArrayList<String> text = responses.getResponses(); //list of all calorie info received for each food item recognized
        ArrayList<Bitmap> images = responses.getImages(); //images of all food items recognized
        TextView textView = findViewById(R.id.results);
        textView.setText("");
        textView.setMovementMethod(new ScrollingMovementMethod()); //enable scrolling in the text view
        StringBuilder sb = new StringBuilder();
        for (String each : text) { //concatenate responses to display
            sb.append(each);
        }
        textView.setText(sb.toString());
        ImageView[] imageViews = new ImageView[] { //getting all the image views into an array
                findViewById(R.id.image1), //first image
                findViewById(R.id.image2), //second image
                findViewById(R.id.image3), //third image
                findViewById(R.id.image4) //fourth image
        };
        hideAllImages();
        for (int i = 0; i < images.size(); i++) { //displaying a max of four images
            Bitmap currentBitmap = images.get(i);
            ImageView imageView = imageViews[i];
            if (currentBitmap != null) {
                imageView.setImageBitmap(currentBitmap); //set image to be displayed
                System.out.println("Picture " + (i + 1) + " set.");
                imageView.setVisibility(View.VISIBLE); //un hide image
            }
            else {
                imageView.setVisibility(View.INVISIBLE); //if image could not be read, make the placeholder invisible
                System.out.println("Picture " + (i + 1) + " NOT set.");
            }
        }
        LinearLayout imagesLayout = (LinearLayout) findViewById(R.id.images);
        imagesLayout.setBackgroundColor(getResources().getColor(R.color.white)); //setting background color of container to white for consistency
        //Stop the progress bar spinner
        ProgressBar spinner = (ProgressBar)findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);
    }

    /**
     * This method is called by the background async task if an error has occurred
     * @param errorMessage description of error to be sent to user
     */
    public void error(String errorMessage) {
        hideAllImages(); //hide all images from view
        TextView textView = findViewById(R.id.results);
        textView.setText(errorMessage); //display error message
        //Stop the progress bar spinner
        ProgressBar spinner = (ProgressBar)findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);
    }

    /**
     * Utility method to hide all images from the placeholders
     */
    private void hideAllImages() {
        ImageView[] imageViews = new ImageView[] { //getting all the image views into an array
                findViewById(R.id.image1),
                findViewById(R.id.image2),
                findViewById(R.id.image3),
                findViewById(R.id.image4)
        };
        for (ImageView imageView : imageViews) {
            imageView.setVisibility(View.INVISIBLE);
        }
        LinearLayout imagesLayout = (LinearLayout) findViewById(R.id.images);
        imagesLayout.setBackgroundColor(getResources().getColor(R.color.black));
    }

}