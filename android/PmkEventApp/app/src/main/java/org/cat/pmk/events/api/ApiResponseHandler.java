package org.cat.pmk.events.api;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ApiResponseHandler extends com.loopj.android.http.JsonHttpResponseHandler {

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        // If the response is JSONObject instead of expected JSONArray
        System.out.println("RAW RESPONSE: " + response.toString());
        handleResponse(response);
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
        try {
            System.out.println("RAW RESPONSE (array): " + response.toString());
            handleResponseArray(response);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
        System.out.println("FAILURE " + throwable.toString());
        System.out.println("OUTPUT RAW: " + errorResponse);
    }


    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
        String tweetText = "ERROR API: ";
        JSONObject response = null;
        try {

            System.out.println("OUTPUT RAW: " + throwable.toString());
            tweetText += errorResponse.toString(3);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // Do something with the response
        System.out.println(tweetText);
    }

    public void handleResponse(JSONObject result) {
        //Toast.makeText(MainActivity.this, "ITEM: " + resultItem, Toast.LENGTH_SHORT).show();
        //override above
        return;
    }

    public void handleResponseArray(JSONArray result) {
        //Toast.makeText(MainActivity.this, "ITEM: " + resultItem, Toast.LENGTH_SHORT).show();
        //override above
        return;
    }


}
