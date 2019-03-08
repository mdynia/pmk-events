package org.cat.pmk.events.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.cat.pmk.events.MainActivity;
import org.cat.pmk.events.R;
import org.cat.pmk.events.sfdc.QueryResponseHandler;
import org.cat.pmk.events.sfdc.SfdcRestApi;

public class FragmentNewOrder extends Fragment {


    Spinner spinnerAppointments;
    Button newOrderBtnBook,newOrderBtnSave;


    private class Appointment {
        public String messageID;
        public String orderID;
        public String index;
        public String start;
        public String resourceFirstName;
        public String resourceLastName;
    }

    ArrayList<Appointment> proposedAppointments;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fgmt_new_order, container, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here

        proposedAppointments = new ArrayList<>();

        newOrderBtnSave = (Button) view.findViewById(R.id.newOrderBtnSave);
        newOrderBtnSave.setEnabled(true);

        android.support.design.widget.TextInputEditText startFrom = (android.support.design.widget.TextInputEditText) view.findViewById(R.id.startFrom);
        SimpleDateFormat dtFormat = new SimpleDateFormat("yyyy-MM-dd");
        startFrom.setText(dtFormat.format(new Date()));


        // SPINNER
        final Spinner spinnerAddress = (Spinner) view.findViewById(R.id.addressSpinner1);
        String[] plants = new String[]{
                "Hanseatenstraße 55, 71640 Ludwigsburg",
                "Rotebühlstraße 120, 70197 Stuttgart"
        };
        final List<String> plantsList = new ArrayList<>(Arrays.asList(plants));
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(view.getContext(), R.layout.spinner_item, plantsList);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerAddress.setAdapter(spinnerArrayAdapter);


        newOrderBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentNewAppointment fragment = new FragmentNewAppointment();
                MainActivity.fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();

                //move

            }
        });



    }



}
