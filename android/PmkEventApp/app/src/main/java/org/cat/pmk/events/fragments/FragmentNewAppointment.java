package org.cat.pmk.events.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import org.cat.pmk.events.MainActivity;
import org.cat.pmk.events.R;
import org.cat.pmk.events.sfdc.QueryResponseHandler;
import org.cat.pmk.events.sfdc.SfdcRestApi;

public class FragmentNewAppointment extends Fragment {


    Spinner spinnerAppointments;
    Button newOrderBtnBook,newOrderBtnSave;
    RadioGroup rg;
    Context ctx;


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
        return inflater.inflate(R.layout.fgmt_new_appointment, container, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        ctx = view.getContext();
        rg = view.findViewById(R.id.radiobuttonsAppts);

        // GET APPOINTMENTS FROM THE SERVER
        proposedAppointments = new ArrayList<>();
        SfdcRestApi.getAppointments("a195800000iHjam", new QueryResponseHandler() {
            @Override
            public void handleResponseArray(JSONArray result) {
                System.out.println("I have appointments: " + result);
                spinnerAppointments.setEnabled(true);
                newOrderBtnBook.setEnabled(true);

                final List<String> appList = new ArrayList<>();
                for (int i=0; i< result.length(); i++ ) {
                    Appointment app = new Appointment();
                    try {
                        JSONObject obj = (JSONObject)result.get(i);
                        app.orderID = "a195800000iHjamAAC";
                        app.start = obj.getString("Start");
                        app.resourceFirstName = obj.getString("ResourceFirstName");
                        app.resourceLastName = obj.getString("ResourceLastName");
                        app.messageID = obj.getString("MessageId");
                        app.index = obj.getString("Index");
                    } catch (Exception e) {
                        System.out.println("ERROR: " + e.toString());
                    }


                    proposedAppointments.add(app);
                    appList.add(app.start + " (" + app.resourceLastName+ ")");
                }

                final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(spinnerAppointments.getContext(), R.layout.spinner_item, appList);
                spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
                spinnerAppointments.setAdapter(spinnerArrayAdapter);


                newOrderBtnSave.setEnabled(false);
                newOrderBtnBook.setEnabled(true);


            }
        });

        spinnerAppointments = (Spinner) view.findViewById(R.id.appointmentsSpinner);
        spinnerAppointments.setEnabled(false);


        newOrderBtnBook = (Button) view.findViewById(R.id.newOrderBtnBook);
        newOrderBtnBook.setEnabled(false);


        // SAVE APPOINTENT
        newOrderBtnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String text = spinnerAppointments.getSelectedItem().toString();
                Integer idx = spinnerAppointments.getSelectedItemPosition();

                Appointment apt = proposedAppointments.get(idx);

                System.out.println("SELECTED APPT: " + text);

                FragmentOrders fragment = new FragmentOrders(ctx);
                MainActivity.fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();

                System.out.println("SERVER: Save ");
                SfdcRestApi.saveAppointment(apt.messageID,apt.orderID,apt.index, new QueryResponseHandler() {
                    @Override
                    public void handleResponseArray(JSONArray result) {

                        System.out.println("SAVED");
                    }
                });
            }
        });

    }



}
