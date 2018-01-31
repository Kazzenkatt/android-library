package com.github.axet.androidlibrary.widgets;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;

public abstract class AppCompatSettingsThemeActivity extends AppCompatThemeActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setAppTheme(getAppTheme());
        super.onCreate(savedInstanceState);
        final SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(this);
        shared.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        final SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(this);
        shared.unregisterOnSharedPreferenceChangeListener(this);
    }

    public abstract String getAppThemeKey();

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getAppThemeKey())) {
            restartActivity();
        }
    }

}
