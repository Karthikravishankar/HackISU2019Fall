package com.example.driveshare;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;


import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

public class DriveShareMain extends AppCompatActivity {
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;
    private NavigationView navigation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drivesharemain);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);

        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        linkActivity();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }
    private void linkActivity() {
        navigation = findViewById(R.id.nav_view);
        navigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.analytics:
//                        Intent analytics = new Intent(DriveShareMain.this, ManagerRequstListPage.class);
//                        startActivity(analytics);
                        break;
                    case R.id.preferences:
                        Intent preferences = new Intent(DriveShareMain.this, Preferences.class);
                        startActivity(preferences);
                        break;
                    case R.id.settings:
                        Intent settings = new Intent(DriveShareMain.this, Settings.class);
                        startActivity(settings);
                        break;
                }
                return false;
            }
        });

    }
}
