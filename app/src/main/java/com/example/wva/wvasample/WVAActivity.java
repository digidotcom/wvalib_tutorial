/*
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of
 * the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2014 Digi International Inc., All Rights Reserved.
 */

package com.example.wva.wvasample;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.digi.wva.async.VehicleDataResponse;
import com.digi.wva.async.WvaCallback;
import com.digi.wva.WVA;

import org.joda.time.DateTime;


public class WVAActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wva);

        final WVAApplication wvaapp = (WVAApplication) getApplication();
        final TextView t = (TextView) findViewById(R.id.connection_status_text);

        /*
        Check that we are talking to a WVA and update
        the text view in the app to the current connection status.
        */
        wvaapp.getWVA().isWVA(new WvaCallback<Boolean>() {
            @Override
            public void onResponse(Throwable error, Boolean success) {
                if(error != null) {
                    error.printStackTrace();
                }
                else {
                    if (success) {
                        t.setText(R.string.wva_connect_ok);
                    }
                    else {
                        t.setText(R.string.wva_connect_error);
                    }
                }
            }
        });

        /*
        Add button to allow the user to set time on the WVA
         */
        final Button time_button = (Button) findViewById(R.id.set_time_button);
        time_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_button_clicked(wvaapp);
            }
        });

        /*
        Add button to allow the user to fetch a vehicle data value from the WVA
         */
        final Button data_button = (Button) findViewById(R.id.fetch_data_button);
        data_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                data_button_clicked(wvaapp);
            }
        });
    }

    private void time_button_clicked(WVAApplication wvaapp) {
        WVA wva = wvaapp.getWVA();
        DateTime now = new DateTime();

        wva.setTime(now, new WvaCallback<DateTime>() {
            @Override
            public void onResponse(Throwable error, DateTime dateTime) {
                if(error != null) {
                    error.printStackTrace();
                }
            }
        });
    }

    private void data_button_clicked(WVAApplication wvaapp) {
        WVA wva = wvaapp.getWVA();
        String endpoint = "EngineSpeed";

        final TextView value_view = (TextView) findViewById(R.id.vehicle_data_value);

        wva.fetchVehicleData(endpoint, new WvaCallback<VehicleDataResponse>() {
            @Override
            public void onResponse(Throwable error, VehicleDataResponse response) {
                if (error != null) {
                    error.printStackTrace();
                } else {
                    value_view.setText(Double.toString(response.getValue()));
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.wva, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
