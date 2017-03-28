package com.bip.androidbipnativeexample;

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

    private UserLogoutTask mLogoutTask = null;
    private UserRefreshTokenTask mRefreshTask = null;

    private View mProgressView;
    private View mLoggedInView;

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

        mLoggedInView = findViewById(R.id.logged_in_view);
        mProgressView = findViewById(R.id.logout_progress);
    }

    private void refresh_token() {
        if(mRefreshTask != null){
            return;
        }

        showProgress(true);
        mRefreshTask = new UserRefreshTokenTask();
        mRefreshTask.execute((Void) null);
    }

    private void user_info() {
        Intent intent = new Intent(this, UserInfoActivity.class);
        startActivity(intent);
    }

    private void logout() {
        if (mLogoutTask != null) {
            return;
        }

        showProgress(true);
        mLogoutTask = new UserLogoutTask();
        mLogoutTask.execute((Void) null);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoggedInView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoggedInView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoggedInView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoggedInView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
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
                response = openIdClient.getOpenIdClient().refreshPublicAccessToken(refreshToken, null);

                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("access_token", response.accessToken);
                editor.apply();
            } catch(BonnierOpenIdException e) {
                Log.e(TAG, "Error Bonnier OpenId exception : " + e.getMessage());
                throw new BonnierOpenIdException(e.getMessage());
            } catch(Exception e) {
                e.printStackTrace();
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mRefreshTask = null;
            showProgress(false);

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

    public class UserLogoutTask extends AsyncTask<Void, Void, Boolean> {
        UserLogoutTask() {

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Log.d(TAG, "Trying to logout...");

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainLoggedInActivity.this);
            String accessToken = preferences.getString("access_token", "");

            OpenIdClient openIdClient = (OpenIdClient)getApplication();
            openIdClient.getOpenIdClient().revokeAccessToken(accessToken);

            SharedPreferences.Editor editor = preferences.edit();
            editor.remove("access_token");
            editor.remove("refresh_token");
            editor.remove("name");
            editor.apply();

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mLogoutTask = null;
            showProgress(false);

            if (success) {
                Intent intent = new Intent(MainLoggedInActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }
    }
}
