package com.bip.androidbipwebviewexample;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String accessToken = preferences.getString("access_token", null);
        if(accessToken != null) {
            Intent intent = new Intent(this, MainLoggedInActivity.class);
            startActivity(intent);
        }
    }

    public void login(View view) {
        Intent intent = new Intent(this, WebviewActivity.class);
        intent.putExtra("LOADING_URL", "loginUrl");
        startActivity(intent);
    }
}
