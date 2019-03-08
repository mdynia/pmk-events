package org.cat.pmk.events.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.cat.pmk.events.MainActivity;
import org.cat.pmk.events.R;

public class FragmentMainWindow extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fgmt_main_window, container, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);

        Button newOrderBtn = (Button) view.findViewById(R.id.newOrderBtn);

        newOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // enable a fragment
                FragmentNewOrder fragment = new FragmentNewOrder();
                MainActivity.fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
            }
        });


    }
}
