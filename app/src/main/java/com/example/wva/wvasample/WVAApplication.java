/*
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of
 * the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2014 Digi International Inc., All Rights Reserved.
 */

package com.example.wva.wvasample;

import android.app.Application;

import com.digi.wva.WVA;

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
    }

}
