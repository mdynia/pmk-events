package org.cat.pmk.events.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import org.cat.pmk.events.MainActivity;
import org.cat.pmk.events.R;
import org.cat.pmk.events.api.ApiResponseHandler;
import org.cat.pmk.events.api.PmkEventsRestApi;
import org.cat.pmk.events.datamodel.AdapterEvent;
import org.cat.pmk.events.datamodel.Event;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentEventsByDistance extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final ArrayList<Event> EVENTS = new ArrayList<>();

    ListView listView;

    View fragmentView;

    int backgroundImageResourceId = R.drawable.kosciol_jasne_wnetrze;

    public Context ctx;

    private static AdapterEvent ibAdapter;

    public static String filterEventType;

    private float geoLat = 51.755828f;
    private float geoLon = 8.777529f;

    private int distanceIndex = 0;

    public void setContext(Context context) {
        this.ctx = context;
    }

    public void setEventTypeFilter(String filter) {
        this.filterEventType = filter;
    }

    public void setBackground(int backgroundImageResourceId) {
        this.backgroundImageResourceId = backgroundImageResourceId;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView =  inflater.inflate(R.layout.fgmt_events_bydistance, container, false);

        return fragmentView;
    }


    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here

        // set background image
        ImageView imageViewBackground = (ImageView) view.findViewById(R.id.imageViewBackground);
        imageViewBackground.setImageResource(backgroundImageResourceId);

        listView = view.findViewById(R.id.list_ib);

        //populate spinner
        Spinner spinner = (Spinner) view.findViewById(R.id.distance_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ctx,
                R.array.distance_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        // set spinner to selection stores in shared prefs
        SharedPreferences prefs = view.getContext().getSharedPreferences(getString(R.string.slectedDistanceRestriction), Context.MODE_PRIVATE);
        if (prefs.contains(getString(R.string.slectedDistanceRestriction))) {
            distanceIndex  = prefs.getInt(getString(R.string.slectedDistanceRestriction),0);
            spinner.setSelection(distanceIndex);
        }

        // fetch events from server again
        SharedPreferences prefsGeo = ctx.getSharedPreferences(MainActivity.PREFERENCES_GEO, Context.MODE_PRIVATE);
        geoLat = prefsGeo.getFloat("lat", 51.75f);
        geoLon = prefsGeo.getFloat("lon", 8.77f);
        Log.d("onViewCreated", "read location : lat=" + geoLat + ", lon=" + geoLon);

    }


    private static void populateEventList(Context cntx, ListView listView, JSONObject response) {
        EVENTS.clear();

        // parse results
        try {
            if (response != null) {
                JSONArray records = response.getJSONArray("records");
                for (int i = 0; i < records.length(); i++) {
                    JSONObject record = (JSONObject) records.get(i);
                    Event e = new Event(record);
                    Log.i("event","Checking: " + e);
                    if (filterEventType.equals(e.getType())) {
                       EVENTS.add(e);
                    }
                }
            }
        } catch (Exception e) {
            Log.e("event","ERROR: " + e);
        }

        ibAdapter = new AdapterEvent(EVENTS, cntx);
        listView.setAdapter(ibAdapter);
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {

        // store spinner selected position in share
        SharedPreferences.Editor editor = view.getContext().getSharedPreferences(getString(R.string.slectedDistanceRestriction), Context.MODE_PRIVATE).edit();
        editor.putInt(getString(R.string.slectedDistanceRestriction), pos);
        editor.apply();

        distanceIndex  = pos;

        // fetch events from server again
        SharedPreferences prefsGeo = ctx.getSharedPreferences(MainActivity.PREFERENCES_GEO, Context.MODE_PRIVATE);
        geoLat = prefsGeo.getFloat("lat", 51.75f);
        geoLon = prefsGeo.getFloat("lon", 8.77f);
        Log.d("tag", "read location: lat=" + geoLat + ", lon=" + geoLon);

        PmkEventsRestApi.getEventsByGeo(geoLat, geoLon, distanceIndex, new ApiResponseHandler() {
            @Override
            public void handleResponse(JSONObject response) {
                populateEventList(ctx, listView, response);
            }
        });
    }

    public void onNothingSelected(AdapterView<?> parent) {

        // Another interface callback
    }

}
