package org.cat.pmk.events.api;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class PmkEventsRestApi {

    private static final String BASE_URL = "http://www.pmk-bielefeld.de";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void getEventsByGeo(float geoLat, float geoLon, int distanceIdx, AsyncHttpResponseHandler responseHandler) {
        System.out.println("Selected distance (index):  " + distanceIdx);


        int distance = 25;
        switch (distanceIdx) {
            case 0:
                distance = 25;
                break;
            case 1:
                distance = 50;
                break;
            case 2:
                distance = 100;
                break;
            case 3:
                distance = 200;
                break;
        }

        System.out.println("GEO lat: " + geoLat);
        System.out.println("GEO lon: " + geoLon);
        System.out.println("GEO dis: " + distance);


        RequestParams params = null;
        String url = getAbsoluteUrl("/pmkEvents/api/getEventsByGeo.php?days=14&lat="+ String.valueOf(geoLat)+"&lon="+String.valueOf(geoLon)+ "&dis="+String.valueOf(distance));

        System.out.println("REST URL: " + url);
        client.get(url, params, responseHandler);
    }



    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    private static String encode(String query) {
        return query.replace(" ", "+");
        //.replace(")","\\)").replace("(","\\(");
    }


}
