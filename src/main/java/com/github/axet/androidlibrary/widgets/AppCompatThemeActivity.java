package com.github.axet.androidlibrary.widgets;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public abstract class AppCompatThemeActivity extends AppCompatActivity {
    public int themeId;

    public void setAppTheme(int id) {
        super.setTheme(id);
        themeId = id;
    }

    public abstract int getAppTheme();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setAppTheme(getAppTheme());
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (themeId != getAppTheme()) {
            restartActivity();
        }
    }

    public void restartActivity() {
        finish();
        startActivity(new Intent(this, getClass()));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
