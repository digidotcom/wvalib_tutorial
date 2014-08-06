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
import android.widget.TextView;

import com.digi.wva.async.WvaCallback;


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
