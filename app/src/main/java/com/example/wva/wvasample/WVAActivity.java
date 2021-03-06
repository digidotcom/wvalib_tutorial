/*
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of
 * the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2014 Digi International Inc., All Rights Reserved.
 */

package com.example.wva.wvasample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.digi.wva.WVA;
import com.digi.wva.async.AlarmType;
import com.digi.wva.async.EventFactory;
import com.digi.wva.async.VehicleDataEvent;
import com.digi.wva.async.VehicleDataListener;
import com.digi.wva.async.VehicleDataResponse;
import com.digi.wva.async.WvaCallback;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;


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

        /*
        Add buttons to allow the user to enable/disable the HTTP web server.
         */
        final Button enable_http_button = (Button) findViewById(R.id.enable_http_button);
        final Button disable_http_button = (Button) findViewById(R.id.disable_http_button);

        View.OnClickListener httpClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.enable_http_button:
                        enable_http_clicked(wvaapp);
                        break;
                    case R.id.disable_http_button:
                        disable_http_clicked(wvaapp);
                        break;
                }
            }
        };

        enable_http_button.setOnClickListener(httpClick);
        disable_http_button.setOnClickListener(httpClick);

        /*
         * Add button to allow the user to subscribe to EngineSpeed, and a text view to see
         * its value.
         */
        final Button subscribe_button = (Button) findViewById(R.id.subscribe_button);
        final TextView engine_speed_value = (TextView) findViewById(R.id.engine_speed_value);

        /*
        Add a text view to display the values from EngineSpeed alarms.
         */
        final TextView engine_speed_alarm_value = (TextView) findViewById(R.id.engine_speed_alarm_value);

        /*
         Set the listener that will be called each time a new EngineSpeed value arrives via the
         event channel. This will update the displayed EngineSpeed value.
         */
        wvaapp.getWVA().setVehicleDataListener("EngineSpeed", new VehicleDataListener() {
            @Override
            public void onEvent(VehicleDataEvent event) {
                VehicleDataResponse response = event.getResponse();

                if (event.getType() == EventFactory.Type.SUBSCRIPTION) {
                    Log.i("WVA Tutorial", "EngineSpeed value: " + response.getValue());
                    engine_speed_value.setText(String.format("EngineSpeed = %.3f\n%s", response.getValue(), response.getTime().toString()));
                }
                else if (event.getType() == EventFactory.Type.ALARM) {
                    Log.i("WVA Tutorial", "EngineSpeed alarm value: " + response.getValue());
                    engine_speed_alarm_value.setText(String.format("EngineSpeed (Alarm) = %.3f\n%s", response.getValue(), response.getTime().toString()));
                }
            }
        });

        subscribe_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subscribe_to_enginespeed(wvaapp);
            }
        });

        wvaapp.getWVA().createVehicleDataAlarm("EngineSpeed", AlarmType.ABOVE, 1000, 10, new WvaCallback<Void>() {
            @Override
            public void onResponse(Throwable error, Void response) {
                if (error != null) {
                    error.printStackTrace();
                } else {
                    Toast.makeText(getApplicationContext(), "Created alarm on EngineSpeed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Clear out the EngineSpeed listener, since this activity has been destroyed and we
        // should drop all references to its views.
        ((WVAApplication) getApplication()).getWVA().removeVehicleDataListener("EngineSpeed");
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

    private void enable_http_clicked(WVAApplication wvaapp) {
        WVA wva = wvaapp.getWVA();

        // Build the JSON data to be sent as part of the request.
        JSONObject json = new JSONObject();
        try {
            json.put("enable", "on");
            json.put("port", 80);
        } catch (JSONException e) {
            // Unexpected error
            e.printStackTrace();
            return;
        }

        wva.configure("http", json, new WvaCallback<Void>() {
            @Override
            public void onResponse(Throwable error, Void response) {
                if (error != null) {
                    error.printStackTrace();
                } else {
                    Toast.makeText(getApplicationContext(), "Enabled HTTP server", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void disable_http_clicked(WVAApplication wvaapp) {
        WVA wva = wvaapp.getWVA();

        // Build the JSON data to be sent as part of the request.
        JSONObject json = new JSONObject();
        try {
            json.put("enable", "off");
            json.put("port", 80);
        } catch (JSONException e) {
            // Unexpected error
            e.printStackTrace();
            return;
        }

        wva.configure("http", json, new WvaCallback<Void>() {
            @Override
            public void onResponse(Throwable error, Void response) {
                if (error != null) {
                    error.printStackTrace();
                } else {
                    Toast.makeText(getApplicationContext(), "Disabled HTTP server", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void subscribe_to_enginespeed(WVAApplication wvaapp) {
        WVA wva = wvaapp.getWVA();

        wva.subscribeToVehicleData("EngineSpeed", 15, new WvaCallback<Void>() {
            @Override
            public void onResponse(Throwable error, Void response) {
                if (error != null) {
                    error.printStackTrace();
                } else {
                    Toast.makeText(getApplicationContext(), "Subscribed to EngineSpeed", Toast.LENGTH_SHORT).show();
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
