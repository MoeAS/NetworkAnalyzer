package com.example.project;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TabHost;

@SuppressWarnings("deprecation")
public class MainTabs extends TabActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        TabHost host = getTabHost();
        //Phone Status Activity
        TabHost.TabSpec statusspec = host.newTabSpec("Phone Info");
        statusspec.setIndicator("Phone Info");
        Intent phoneStatusIntent = new Intent(this, MainActivity.class);
        statusspec.setContent(phoneStatusIntent);


        //Database Activity
        TabHost.TabSpec dataspec = host.newTabSpec("Data");
        dataspec.setIndicator("Data");
        Intent DataIntent = new Intent(this, Database.class);
        dataspec.setContent(DataIntent);


        //Compare Activity
        TabHost.TabSpec compspec = host.newTabSpec("Compare Data");
        compspec.setIndicator("Compare Data");
        Intent CompIntent = new Intent(this, Compare.class);
        compspec.setContent(CompIntent);


        // Adding all TabSpec to TabHost
        host.addTab(statusspec); //Default tab
        host.addTab(dataspec);
        host.addTab(compspec);
    }

    /*
    * Create a menu with the tabs provided above
    * */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
