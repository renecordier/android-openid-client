package com.bip.androidbipwebviewexample;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.bip.androidopenidclient.entity.ClaimsSet;
import com.bip.androidopenidclient.exceptions.BonnierOpenIdException;
import com.bip.androidopenidclient.response.OAuth2Response;
import com.bip.androidopenidclient.utils.SplusUtil;

import org.jose4j.base64url.Base64Url;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WebviewActivity extends AppCompatActivity {
    private static final String TAG = "WebviewActivity";
    private static final String REMEMBER = "remember";

    private WebView myWebView;
    private UserRequestAccessTokenTask mAuthTask = null;

    private static final String authorizeUrl = "https://account.stage.bonnier.news/bip/authorize";
    private static final String logoutUrl = "https://account.stage.bonnier.news/bip/logout";
    private static final String scope = "openid email profile appId:di.se";
    private static final String codeChallengeMethod = "S256";

    private static String state;
    private static String codeChallenge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        myWebView = (WebView) findViewById(R.id.webviewLogin);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        myWebView.setWebViewClient(new BipWebViewClient());

        OpenIdClient openIdClient = (OpenIdClient)getApplication();
        String loadingUrl;
        state = SplusUtil.createOpenIdState();
        if(getIntent().getStringExtra("LOADING_URL").equals("loginUrl")) {
            codeChallenge = UUID.randomUUID().toString();
            loadingUrl = openIdClient.getOpenIdClient().getAuthorizeUrl(authorizeUrl, openIdClient.getClientId(), "http://bip.com/oauth/callback",
                    scope, state, null, null, "sv", null, "http://bip.com/cancel", computeCodeChallenge(), codeChallengeMethod);
        } else {
            loadingUrl = openIdClient.getOpenIdClient().getLogoutUrl(logoutUrl, "di.se", "http://bip.com/logout", state);
        }

        myWebView.loadUrl(loadingUrl);
    }

    private String computeCodeChallenge(){
        MessageDigest md;

        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e.getMessage());
        }

        byte[] hash = md.digest(codeChallenge.getBytes());
        return Base64Url.encode(hash);
    }

    private class BipWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Uri uri = Uri.parse(url);
            Log.d(TAG, "URL: " + url);
            Log.d(TAG, "path: " + uri.getPath());

            if (uri.getHost().equals("bip.com")) {
                switch (uri.getPath()) {
                    case "/cancel":
                        Log.d(TAG, "BIP OAuth cancel");
                        Intent intent = new Intent(WebviewActivity.this, MainActivity.class);
                        startActivity(intent);
                        break;
                    case "/oauth/callback":
                        Log.d(TAG, "BIP OAuth callback");
                        bipHandler(uri);
                        break;
                    case "/logout":
                        Log.d(TAG, "BIP OAuth logout");
                        logoutHandler(uri);
                }
                return true;
            } else {
                // Load the page via the webview
                view.loadUrl(url);
            }
            return false;
        }

        private void bipHandler(Uri uri) {
            if (mAuthTask != null) {
                return;
            }

            String returnState = uri.getQueryParameter("state");
            if (returnState == null || !returnState.equals(state)) {
                Log.e(TAG, "Error : the state returned by the server: " + returnState + " is different than the one sent: " + state);
            } else {
                state = null;
                String code = uri.getQueryParameter("code");
                if (code != null) {
                    mAuthTask = new UserRequestAccessTokenTask(code, uri.getQueryParameter("access_type"));
                    mAuthTask.execute((Void) null);
                } else {
                    Log.e(TAG, "Error : " + uri.getQueryParameter("error_description"));
                }
            }
        }

        private void logoutHandler(Uri uri) {
            String returnState = uri.getQueryParameter("state");
            if (returnState == null || !returnState.equals(state)) {
                Log.e(TAG, "Error : the state returned by the server: " + returnState + " is different than the one sent: " + state);
            } else {
                state = null;
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(WebviewActivity.this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.remove("access_token");
                editor.remove("refresh_token");
                editor.remove("name");
                editor.apply();

                Intent intent = new Intent(WebviewActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }
    }

    public class UserRequestAccessTokenTask extends AsyncTask<Void, Void, Boolean> {
        private final String mCode;
        private final String mAccessType;

        UserRequestAccessTokenTask(String code, String accessType) {
            mCode = code;
            mAccessType = accessType;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Log.d(TAG, "Trying to login...");

            OpenIdClient openIdClient = (OpenIdClient)getApplication();
            OAuth2Response response = null;
            try{
                boolean longLivedToken = false;
                if(mAccessType != null && mAccessType.equals(REMEMBER)){
                    longLivedToken = true;
                }

                response = openIdClient.getOpenIdClient().requestAccessTokenWithPKCE(openIdClient.getClientId(),
                        mCode,
                        "http://bip.com/oauth/callback",
                        longLivedToken,
                        codeChallenge);

                if(response.httpResponseCode == 200) {
                    codeChallenge = null;
                    Log.d(TAG, "Success !");
                    Log.d(TAG, "Access token : " + response.accessToken);
                    Log.d(TAG, "Token type : " + response.tokenType);
                    Log.d(TAG, "Expires in : " + response.expiresIn);
                    Log.d(TAG, "Refresh token : " + response.refreshToken);
                    Log.d(TAG, "Scope : " + response.scope);

                    ClaimsSet claimsSet = openIdClient.getOpenIdClient().verifyIdToken(response.idToken, openIdClient.getClientId());
                    String firstName = claimsSet.getClaim("given_name").toString();
                    String lastName = claimsSet.getClaim("family_name").toString();
                    Log.d(TAG, "ID token : {firstName:" + firstName + ",lastName:" + lastName + "}");

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(WebviewActivity.this);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("access_token", response.accessToken);
                    editor.putString("refresh_token", response.refreshToken);
                    editor.putString("name", firstName + " " + lastName);
                    editor.apply();
                } else {
                    Log.d(TAG, "Error " + response.httpResponseCode + " : " + response.errorMsg);
                    return false;
                }
            } catch (Exception e) {
                Log.d(TAG, "Internal Error !");
                e.printStackTrace();
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;

            if (success) {
                Intent intent = new Intent(WebviewActivity.this, MainLoggedInActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(WebviewActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }
}
