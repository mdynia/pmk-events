package org.cat.pmk.events.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import org.cat.pmk.events.MainActivity;
import org.cat.pmk.events.R;

public class FragmentMainWindow extends Fragment {
    LocationManager locationManager;

    View fragmentView;

    Activity mainActivity;


    public void setActivity(Activity mainActivity) {
        this.mainActivity = mainActivity;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fgmt_main_window, container, false);
        return fragmentView;
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here

        locationManager =  (LocationManager) view.getContext().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        Button btn = (Button) view.findViewById(R.id.btnRefreshPosition);
        btn.setOnClickListener(new View.OnClickListener() {

            // get gps position
            private float[] getGPS(View view) {
                Criteria criteria = new Criteria();

                float[] gps = new float[2];
                gps[0] = -1.0f;
                gps[1] = -1.0f;

                String bestProvider = locationManager.getBestProvider(criteria, true);
                Log.d("location", "bestProvider = " +bestProvider);

                if (ContextCompat.checkSelfPermission(view.getContext().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Location location = locationManager.getLastKnownLocation(bestProvider);
                    if (location!=null) {
                        gps[0] = (float) location.getLatitude();
                        gps[1] = (float) location.getLongitude();
                    } else {
                        Log.e("location", "location = null");
                    }
                } else {
                    Log.d("location", "permissions missing for ACCESS_FINE_LOCATION");
                }

                return gps;
            }

            @Override
            public void onClick(View view) {
                Log.d("button", "onClick: ");

                // check permissions
                if (ContextCompat.checkSelfPermission(view.getContext().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Log.d("button", "permission missing: ACCESS_FINE_LOCATION ");

                    // Permission is not granted
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                    } else {
                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions(mainActivity,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                11213);

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                }

                Log.d("button", "providers: " + locationManager.getProviders(false));

                float[] geo = getGPS(view);

                if (geo[0]>0 &&geo[1]>0) {
                    Toast.makeText(mainActivity, "Nowa pozycja GPS ustawiona (" +geo[0] + ", " + geo[1] + ")", Toast.LENGTH_SHORT).show();
                    // valid coordinates
                    SharedPreferences.Editor editor = mainActivity.getSharedPreferences(MainActivity.PREFERENCES_GEO, Context.MODE_PRIVATE).edit();
                    editor.putFloat("lat", geo[0]);
                    editor.putFloat("lon", geo[1]);
                    editor.apply();

                    Log.i("tag", "store location: lat=" + geo[0] + ", lon=" + geo[1]);
                } else {
                    Toast.makeText(mainActivity, "Nie udało się określić pozycji GPS", Toast.LENGTH_SHORT).show();
                }

            }
        });



    }
}
