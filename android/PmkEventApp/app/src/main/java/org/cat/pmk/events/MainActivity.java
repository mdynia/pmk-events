package org.cat.pmk.events;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.cat.pmk.events.fragments.FragmentEventsByDistance;
import org.cat.pmk.events.fragments.FragmentMainWindow;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String PREFERENCES_GEO = "geo-location-user";


    public static FragmentManager fragmentManager;

    private FusedLocationProviderClient fusedLocationClient;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int res = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fragmentManager = getSupportFragmentManager();

        // enable a fragment
        FragmentMainWindow fragment = new FragmentMainWindow();
        fragment.setActivity(this);

        MainActivity.fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_main) {
            MainActivity.this.setTitle(getString(R.string.nav_main));

            // enable a fragment
            FragmentMainWindow fragment = new FragmentMainWindow();
            fragment.setActivity(this);
            MainActivity.fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
        } else if (id == R.id.nav_nabozenstwa) {
            MainActivity.this.setTitle(getString(R.string.nav_nabozenstwa));

            // enable a fragment
            FragmentEventsByDistance fragment = new FragmentEventsByDistance();
            fragment.setContext(getApplicationContext());
            fragment.setEventTypeFilter(false);
            fragment.setBackground(R.drawable.kosciol_wnetrze);
            MainActivity.fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
        } else if (id == R.id.nav_spowiedz) {
            MainActivity.this.setTitle(getString(R.string.nav_spowiedz));

            // enable a fragment
            FragmentEventsByDistance fragment = new FragmentEventsByDistance();
            fragment.setContext(getApplicationContext());
            fragment.setEventTypeFilter(true);
            fragment.setBackground(R.drawable.konfesjonal);
            MainActivity.fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
        } else if (id == R.id.nav_terminy) {

        } else if (id == R.id.nav_rekolekcje) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
