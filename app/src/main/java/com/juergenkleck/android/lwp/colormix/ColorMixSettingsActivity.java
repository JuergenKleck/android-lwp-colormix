package com.juergenkleck.android.lwp.colormix;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Android App - ColorMix LWP
 *
 * Copyright 2022 by Juergen Kleck <develop@juergenkleck.com>
 */
public class ColorMixSettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_container, new ColorMixSettingsFragment())
                .commit();
    }
}
