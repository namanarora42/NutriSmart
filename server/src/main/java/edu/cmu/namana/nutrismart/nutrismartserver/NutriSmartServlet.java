package edu.cmu.namana.nutrismart.nutrismartserver;

import java.io.*;

import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import org.json.JSONObject;

/**
 * This class models an HTTPServlet that allows a client to request data from a third party API.
 * It implements a doPost method that can be used to send a query to the servlet which is further processed to generate the output and send it back to the user
 */
@WebServlet(name = "nutriServlet", value = "/nutri-servlet")
public class NutriSmartServlet extends HttpServlet {
    private NutriSmart nutriSmart;

    public void init() {
        nutriSmart = new NutriSmart();
    }

    /**
     * Handling POST request sent to the servlet
     * @param req Request object
     * @param resp Response object
     * @throws IOException if the connection cannot be setup
     */
    public void doPost(HttpServletRequest req,
                       HttpServletResponse resp) throws IOException {
        init(); //setup a NutriSmart object to handle data and communication with API
        System.out.println("doPost request was received and is being processed.\n\n");
        BufferedReader requestBuffer = req.getReader();
        String in;
        StringBuilder sb = new StringBuilder();
        while ((in = requestBuffer.readLine()) != null) { //get body from post request
            sb.append(in);
        }
        PrintWriter out = resp.getWriter();
        if (sb.toString().equals("")) { //error checking for invalid input
            out.println(new JSONObject().put("error", "Please provide a valid input"));
        }
        JSONObject requestJSON = new JSONObject(sb.toString()); //create JSON object from the stream received
        nutriSmart.setup(requestJSON); //set up an instance of NutriSmart with the JSON received from client
        nutriSmart.performQuery(); //perform query on the instance
        out.println(nutriSmart.getResponse()); //retrieve the response sent by the API, ready for consumption by the client
        out.flush();
        out.close();
    }

    /**
     * Used for freeing up resources and shutting down
     */
    public void destroy() {
        nutriSmart = null;
    }
}