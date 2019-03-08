package org.cat.pmk.events.sfdc;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

import org.json.JSONObject;

import cz.msebera.android.httpclient.entity.StringEntity;

public class SfdcRestApi {

    public static String sessionId = "tbd";

    private static final String BASE_URL = "https://service1demo.my.salesforce.com";

    private static AsyncHttpClient client = new AsyncHttpClient();


    public static void refreshSessionId(Context ctx, AsyncHttpResponseHandler responseHandler) {
        String grant_type = "password";
        String client_id = "3MVG98_Psg5cppyZ77hzuEPMNpdW79s70_NsbJQZTf8IlBRO_Ud0APfa8jsRBQasaWrfnzfiZT1o4CyccfHF_";
        String client_secret = "1820053330651826959";
        String username = "mdynia@gms-online.de-demo";
        String password = "EAI$-kg,BKNalDH01--DIDAznRiH31iFLZXnScUmTS8LD9skz";


        RequestParams params = new RequestParams();
        params.put("grant_type", grant_type);
        params.put("client_id", client_id);
        params.put("client_secret", client_secret);
        params.put("username", username);
        params.put("password", password);
        client.post("https://login.salesforce.com/services/oauth2/token", params, responseHandler);
    }


    public static void soqlQuery(String query, AsyncHttpResponseHandler responseHandler) {
        System.out.println("QUERY: " + query);
        client.addHeader("Authorization", "Bearer " + sessionId);
        RequestParams params = null;
        String url = getAbsoluteUrl("/services/data/v26.0/query/?q=" + encode(query));

        System.out.println("REST URL: " + url);
        client.get(url, params, responseHandler);
    }


    public static void createOrder(String installedBaseID, String service, ResponseHandlerInterface responseHandler) {
        String url = getAbsoluteUrl("/services/apexrest/WSSimpleOrder/" + service + "/" + installedBaseID);

        client.addHeader("Authorization", "Bearer " + sessionId);

        RequestParams params = new RequestParams();


        System.out.println("REST URL: " + url);
        client.post(url, params, responseHandler);

        // -d '{  "name" : "001D000000IqhSLIAZ",  "phone" : "ss",  "website" : true}'
    }

    public static void getAppointments(String orderID, AsyncHttpResponseHandler responseHandler) {
        client.addHeader("Authorization", "Bearer " + sessionId);
        RequestParams params = null;
        String url = getAbsoluteUrl("/services/apexrest/WSSimpleAppointment/" + orderID);


        System.out.println("REST URL: " + url);
        client.get(url, params, responseHandler);
    }


    public static void saveAppointment(String MessageID, String orderID, String index, AsyncHttpResponseHandler responseHandler) {
        client.addHeader("Authorization", "Bearer " + sessionId);
        client.addHeader("Content-Type", "application/json");
        client.addHeader("Accept", "*/*");

        String url = getAbsoluteUrl("/services/apexrest/WSSimpleAppointment/");
        System.out.println("REST URL: " + url);

        RequestParams params = new RequestParams();
        params.put("MessageId", MessageID);
        params.put("mode", "mode");
        params.put("OrderId", orderID);
        params.put("Index", index);
        client.post(url, params, responseHandler);
    }


    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.addHeader("Authorization", "Bearer " + sessionId);
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.addHeader("Authorization", "Bearer " + sessionId);
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    private static String encode(String query) {
        return query.replace(" ", "+");
        //.replace(")","\\)").replace("(","\\(");
    }


}
