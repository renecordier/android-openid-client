package com.bip.androidbipnativeexample;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bip.androidopenidclient.entity.UserInfo;

public class UserInfoActivity extends AppCompatActivity {
    private static final String TAG = "MainLoggedInActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String accessToken = preferences.getString("access_token", "");

        OpenIdClient openIdClient = (OpenIdClient)getApplication();
        UserInfo userInfo = openIdClient.getOpenIdClient().getUserInfo(accessToken);

        TextView textView = (TextView) findViewById(R.id.bip_id_text);
        textView.setText(userInfo.getBipAccountId());

        textView = (TextView) findViewById(R.id.email_text);
        textView.setText(userInfo.getEmail());

        textView = (TextView) findViewById(R.id.email_verified_text);
        textView.setText(String.valueOf(userInfo.isEmailVerified()));

        textView = (TextView) findViewById(R.id.name_text);
        textView.setText(userInfo.getFullName());
    }

    public void returnToMain(View view) {
        Intent intent = new Intent(this, MainLoggedInActivity.class);
        startActivity(intent);
    }
}
