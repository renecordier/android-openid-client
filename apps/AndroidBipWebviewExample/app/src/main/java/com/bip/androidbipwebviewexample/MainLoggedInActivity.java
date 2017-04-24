package com.bip.androidbipwebviewexample;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bip.androidopenidclient.exceptions.BonnierOpenIdException;
import com.bip.androidopenidclient.response.OAuth2Response;

public class MainLoggedInActivity extends AppCompatActivity {
    private static final String TAG = "MainLoggedInActivity";

    private UserRefreshTokenTask mRefreshTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_logged_in);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String name = preferences.getString("name", "to BIP !!!");

        TextView textView = (TextView) findViewById(R.id.welcome_user_text);
        textView.setText("Welcome " + name);

        Button mRefreshButton = (Button) findViewById(R.id.button_refresh);
        mRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refresh_token();
            }
        });

        Button mLogoutButton = (Button) findViewById(R.id.button_logout);
        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        Button mInfoUserButton = (Button) findViewById(R.id.button_user_info);
        mInfoUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user_info();
            }
        });
    }

    private void refresh_token() {
        if(mRefreshTask != null){
            return;
        }

        mRefreshTask = new UserRefreshTokenTask();
        mRefreshTask.execute((Void) null);
    }

    private void user_info() {
        Intent intent = new Intent(this, UserInfoActivity.class);
        startActivity(intent);
    }

    private void logout() {
        Intent intent = new Intent(this, WebviewActivity.class);
        intent.putExtra("LOADING_URL", "logoutUrl");
        startActivity(intent);
    }

    public class UserRefreshTokenTask extends AsyncTask<Void, Void, Boolean> {
        UserRefreshTokenTask() {

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Log.d(TAG, "Trying to refresh token...");

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainLoggedInActivity.this);
            String refreshToken = preferences.getString("refresh_token", "");

            OAuth2Response response = null;
            try {
                OpenIdClient openIdClient = (OpenIdClient) getApplication();
                response = openIdClient.getOpenIdClient().refreshAccessToken(refreshToken, null, openIdClient.getClientId());

                if(response.httpResponseCode == 200) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("access_token", response.accessToken);
                    editor.apply();
                } else {
                    Log.d(TAG, "Error " + response.httpResponseCode + " : " + response.errorMsg);
                    return false;
                }
            } catch(Exception e) {
                Log.d(TAG, "Internal Error !");
                e.printStackTrace();
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mRefreshTask = null;

            Toast toast = null;

            if (success) {
                toast = Toast.makeText(getApplicationContext(), "Access token was refreshed successfully !", Toast.LENGTH_LONG);
            } else {
                toast = Toast.makeText(getApplicationContext(), "Error! A problem occured...", Toast.LENGTH_LONG);
            }
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.show();
        }
    }
}
