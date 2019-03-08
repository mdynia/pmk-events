package org.cat.pmk.events.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import org.cat.pmk.events.MainActivity;
import org.cat.pmk.events.R;
import org.cat.pmk.events.datamodel.InstalledBase;
import org.cat.pmk.events.datamodel.InstalledBaseAdapter;
import org.cat.pmk.events.datamodel.Order;
import org.cat.pmk.events.datamodel.OrderAdapter;
import org.cat.pmk.events.sfdc.QueryResponseHandler;
import org.cat.pmk.events.sfdc.SfdcRestApi;

public class FragmentOrders extends Fragment {

    private static OrderAdapter ibAdapter;
    private static final ArrayList<Order> installedBases = new ArrayList<>();

    private final Context ctx;

    ListView listView;

    public FragmentOrders(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fgmt_orders, container, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);

        listView = view.findViewById(R.id.list_orders);

        String query = "SELECT " +
                "id, Name, toLabel(SER__Type__c), SER__roleSR__c, toLabel(SER__Activity__c), " +
                "(SELECT id, Name,Serial_Number__c, SER__ProductNameCalc__c from SER__OrderItem__r), " +
                "(SELECT id, SER__Article__r.Name, SER__ArticleName__c, SER__Price__c from SER__OrderLine__r), " +
                "(SELECT id, SER__Start__c, SER__Duration__c from SER__Appointments__r WHERE SER__AssignmentStatus__c <> '5507' ORDER BY SER__Start__c ASC) " +
                "FROM SER__SCOrder__c " +
                "WHERE SER__roleSR__r.SER__Account__r.SER__ID2__c = '" + MainActivity.accountID2 + "' AND SER__Status__c IN ('5501', '5502', '5509', '5510', '5503') " +
                "ORDER BY CreatedDate " +
                "DESC LIMIT 10";
        SfdcRestApi.soqlQuery(query, new QueryResponseHandler() {
            @Override
            public void handleResponse(JSONObject response) {
                populateOrderList(ctx, listView, response);
            }
        });
    }

    private static void populateOrderList(Context cntx, ListView listView, JSONObject response) {
        installedBases.clear();

        // parse results

        try {
            if (response != null) {
                JSONArray records = response.getJSONArray("records");
                for (int i = 0; i < records.length(); i++) {
                    JSONObject record = (JSONObject) records.get(i);
                    installedBases.add(new Order(record));
                }
            }
        } catch (Exception e) {
            System.err.println("ERROR: " + e);
        }

        ibAdapter = new OrderAdapter(installedBases, cntx);
        listView.setAdapter(ibAdapter);
    }
}
