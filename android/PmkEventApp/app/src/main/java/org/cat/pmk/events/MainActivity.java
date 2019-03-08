package org.cat.pmk.events;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONObject;

import java.util.ArrayList;

import org.cat.pmk.events.datamodel.Order;
import org.cat.pmk.events.datamodel.OrderAdapter;
import org.cat.pmk.events.fragments.FragmentInstalledBaseManager;
import org.cat.pmk.events.fragments.FragmentMainWindow;
import org.cat.pmk.events.fragments.FragmentNewOrder;
import org.cat.pmk.events.fragments.FragmentOrders;
import org.cat.pmk.events.sfdc.SfdcRestApi;
import org.cat.pmk.events.sfdc.QueryResponseHandler;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    // New tags for referring to fragments
    public static final String FRAGMENT_TAG_NEW_ORDER = "tag_new_order";
    public static final String FRAGMENT_TAG_IB_LIST = "tag_ib_list";

    public static String accountID2 = "ACC-MIRO-123";

    private static OrderAdapter orderAdapter;

    public static FragmentManager fragmentManager;

    private static final ArrayList<Order> orders = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        MainActivity.fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();


        //refresh session ID
        SfdcRestApi.refreshSessionId(this.getApplicationContext(), new QueryResponseHandler() {
            @Override
            public void handleResponse(JSONObject response) {
                System.out.println("RAW: " + response);

                try {
                    SfdcRestApi.sessionId = response.getString("access_token");
                } catch (Exception e) {
                    System.err.println("ERROR: " + e.getLocalizedMessage());
                }
            }
        });
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

        if (id == R.id.nav_installed_base) {
            MainActivity.this.setTitle(getString(R.string.nav_spowiedz));

            // enable a fragment
            FragmentInstalledBaseManager fragment = new FragmentInstalledBaseManager(getApplicationContext());
            MainActivity.fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();


        } else if (id == R.id.nav_new_order) {
            // set title
            MainActivity.this.setTitle(getString(R.string.nav_nabozenstwa));

            // enable a fragment
            FragmentNewOrder fragment = new FragmentNewOrder();
            MainActivity.fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
        } else if (id == R.id.nav_orders) {
            MainActivity.this.setTitle(getString(R.string.nav_nabozenstwa));

            // enable a fragment
            FragmentOrders fragment = new FragmentOrders(getApplicationContext());
            MainActivity.fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
        } else if (id == R.id.nav_share) {
            // refresh session ID
            SfdcRestApi.refreshSessionId(this.getApplicationContext(), new QueryResponseHandler() {
                @Override
                public void handleResponse(JSONObject response) {
                    System.out.println("RAW: " + response);

                    try {
                        SfdcRestApi.sessionId = response.getString("access_token");
                    } catch (Exception e) {
                        System.err.println("ERROR: " + e.getLocalizedMessage());
                    }
                }
            });
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
