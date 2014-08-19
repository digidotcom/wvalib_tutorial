/*
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of
 * the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2014 Digi International Inc., All Rights Reserved.
 */

package com.example.wva.wvasample;

import android.app.Application;
import android.widget.Toast;

import com.digi.wva.async.EventChannelStateListener;
import com.digi.wva.WVA;

import java.io.IOException;

public class WVAApplication extends Application {

    private WVA wva;
    // Replace with the IP address of your WVA.
    private String wva_ip = "10.10.32.209";

    public WVA getWVA() {
        return wva;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        wva = new WVA(wva_ip);
        // Replace the username and password to match that of your WVA.
        wva.useBasicAuth("admin", "admin").useSecureHttp(true);

        // Set the listener used to report the state of the event channel connection.
        wva.setEventChannelStateListener(new EventChannelStateListener() {
            @Override
            public void onConnected(WVA device) {
                Toast.makeText(getApplicationContext(), "Event channel connected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(WVA device, IOException error) {
                error.printStackTrace();
                device.disconnectEventChannel(true);
            }

            @Override
            public void onRemoteClose(WVA device, int port) {
                Toast.makeText(getApplicationContext(), "Event channel closed on remote side. Reconnecting...", Toast.LENGTH_SHORT).show();

                reconnectAfter(device, 15000, port);
            }

            @Override
            public void onFailedConnection(WVA device, int port) {
                Toast.makeText(getApplicationContext(), "Could not connect to event channel", Toast.LENGTH_SHORT).show();
            }
        });

        wva.connectEventChannel(5000);
    }
}
