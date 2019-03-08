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
import org.cat.pmk.events.sfdc.SfdcRestApi;
import org.cat.pmk.events.sfdc.QueryResponseHandler;

public class FragmentInstalledBaseManager extends Fragment {

    private static InstalledBaseAdapter ibAdapter;
    private static final ArrayList<InstalledBase> installedBases = new ArrayList<>();

    private final Context ctx;

    ListView listView;

    public FragmentInstalledBaseManager(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fgmt_installed_base_manager, container, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);

        listView = view.findViewById(R.id.list_ib);

        String query = "SELECT " +
                "id, Name, SER__SerialNo__c, SER__ProductNameCalc__c, SER__ShortText__c, LastModifiedDate " +
                "FROM SER__SCInstalledBase__c " +
                "WHERE SER__InstalledBaseLocation__c IN (SELECT SER__InstalledBaseLocation__c from SER__SCInstalledBaseRole__c WHERE SER__Account__r.SER__ID2__c ='"+ MainActivity.accountID2+"') " +
                "LIMIT 10";
        SfdcRestApi.soqlQuery(query, new QueryResponseHandler() {
            @Override
            public void handleResponse(JSONObject response) {
                populateInstalledBaseList(ctx, listView, response);
            }
        });
    }

    private static void populateInstalledBaseList(Context cntx, ListView listView, JSONObject response) {
        installedBases.clear();

        // parse results

        try {
            if (response != null) {
                JSONArray records = response.getJSONArray("records");
                for (int i = 0; i < records.length(); i++) {
                    JSONObject record = (JSONObject) records.get(i);
                    installedBases.add(new InstalledBase(record));
                }
            }
        } catch (Exception e) {
            System.err.println("ERROR: " + e);
        }

        ibAdapter = new InstalledBaseAdapter(installedBases, cntx);
        listView.setAdapter(ibAdapter);
    }
}
