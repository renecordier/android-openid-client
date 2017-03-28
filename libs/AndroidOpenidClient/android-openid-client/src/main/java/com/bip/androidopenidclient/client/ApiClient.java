package com.bip.androidopenidclient.client;

import android.util.Log;

import com.bip.androidopenidclient.entity.ClaimsSet;
import com.bip.androidopenidclient.entity.UserInfo;
import com.bip.androidopenidclient.exceptions.BonnierOpenIdException;
import com.bip.androidopenidclient.response.OAuth2Response;
import com.bip.androidopenidclient.utils.JwsUtil;
import com.google.gson.Gson;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by rene on 06/03/17.
 */

class ApiClient {
    private static final String TAG = "ApiClient";

    //protected WebResource resource;
    private JwsUtil jwsUtil;

    private OAuth2Response oAuth2Response; //Contains access_token, refresh_token, id_token, ...
    private Long expireTime;
    private String clientId; //Must be a public client !

    private String endpoint;

    private OkHttpClient httpClient;

    /**
     * Basic constructor.
     * @param endpoint the oauth2 endpoint to access the API
     */
    public ApiClient(String endpoint) {
        Log.d(TAG, "OAuth2 endpoint " + endpoint);

        clientId = null;
        jwsUtil = new JwsUtil(endpoint + "/keys");
        this.endpoint = endpoint;
        httpClient = new OkHttpClient();
    }

    /**
     * Full constructor
     * @param endpoint the oauth2 endpoint to access the API
     * @param clientId S+ client Id (must be a public client)
     */
    public ApiClient(String endpoint, String clientId) {
        this(endpoint);
        this.clientId = clientId;
    }

    /**
     * Returns client access token if exists and valid or request a new one otherwise
     * @return valid BIP client access token
     */
    protected String accessToken() {
        if (oAuth2Response == null || expireTime <= System.currentTimeMillis()) {
            try {
                expireTime = System.currentTimeMillis();
                oAuth2Response = requestClientAccessToken(clientId, "openid", false);
                expireTime += oAuth2Response.expiresIn * 1000L;
            } catch (Exception e) {
                Log.i(TAG, "Can not create new client access token", e);
            }
        }
        return oAuth2Response.accessToken;
    }

    /**
     * Request a BIP client access token using the client_credentials flow
     * @param clientId S+ client Id
     * @param scope the scope of the access
     * @param longLivedToken if token long lived (30d) or not (8h)
     * @return OAuth2Response object
     */
    public OAuth2Response requestClientAccessToken(String clientId,
                                                   String scope,
                                                   Boolean longLivedToken) {
        return requestAccessToken(clientId, "client_credentials", null, null, scope,
                longLivedToken, null, null, null);
    }

    /**
     * Request an access token using the authorization_code grant type flow with a PKCE (Proof Key for Code Exchange).
     * Used by public clients that cannot store secrets...
     * @param clientId S+ client Id
     * @param code the code received in authorization code flow
     * @param redirectURI the redirect link to handle the response back from the server
     * @param longLivedToken if token long lived (30d) or not (8h)
     * @param codeVerifier used for PKCE concept (in case client cannot store a secret)
     * @return OAuth2Response object
     * @throws BonnierOpenIdException
     */
    public OAuth2Response requestAccessTokenFromCodePKCE(String clientId,
                                                         String code,
                                                         String redirectURI,
                                                         Boolean longLivedToken,
                                                         String codeVerifier) throws BonnierOpenIdException {
        return requestAccessToken(clientId, "authorization_code", code, redirectURI, null,
                longLivedToken, null, null, codeVerifier);
    }

    /**
     * Request an access token using the password grant type flow
     * @param clientId S+ client Id
     * @param scope the scope of the access
     * @param username the email of user account
     * @param password the password of user account
     * @param longLivedToken if token long lived (30d) or not (8h)
     * @return OAuth2Response object
     * @throws BonnierOpenIdException
     */
    public OAuth2Response requestAccessTokenWithPassword(String clientId,
                                                         String scope,
                                                         String username,
                                                         String password,
                                                         Boolean longLivedToken) throws BonnierOpenIdException {
        return requestAccessToken(clientId, "password", null, null, scope,
                longLivedToken, username, password, null);
    }

    /**
     * Requests access token from Bonnier Identity Provider
     * @param clientId S+ client Id
     * @param grantType the grant type of the flow
     * @param code the code received in authorization code flow
     * @param redirectURI the redirect link to handle the response back from the server
     * @param scope the scope of the access
     * @param longLivedToken if token is long lived (30d) or not (8h)
     * @param username the email of user account
     * @param password the password of user account
     * @param codeVerifier used for PKCE concept (in case client cannot store a secret)
     * @return OAuth2Response object
     * @throws BonnierOpenIdException
     */
    public OAuth2Response requestAccessToken(String clientId,
                                             String grantType,
                                             String code,
                                             String redirectURI,
                                             String scope,
                                             Boolean longLivedToken,
                                             String username,
                                             String password,
                                             String codeVerifier) throws BonnierOpenIdException {
        OAuth2Response oauthResponse = null;

        try {
            Request.Builder requestBuilder = new Request.Builder().url(endpoint + "/token");
            FormBody.Builder form = new FormBody.Builder().add("grant_type", grantType);
            if(redirectURI != null) {
                form.add("redirect_uri", redirectURI);
            }
            if(code != null) {
                form.add("code", code);
            }
            if(scope != null) {
                form.add("scope", scope);
            }
            if(longLivedToken != null) {
                form.add("long_lived_token",longLivedToken.toString());
            }
            if(username != null) {
                form.add("username", username);
            }
            if(password != null) {
                form.add("password", password);
            }
            if(codeVerifier != null) {
                form.add("code_verifier", codeVerifier);
            }
            if(clientId != null) {
                form.add("client_id", clientId);
            } else if (this.clientId != null){
                form.add("client_id", this.clientId);
            } else {
                requestBuilder.header("Authorization", "Bearer " + accessToken());
            }

            Request request = requestBuilder.post(form.build()).build();
            Response response = httpClient.newCall(request).execute();
            oauthResponse = new Gson().fromJson(response.body().string(), OAuth2Response.class);
            Log.d(TAG, "Success! Access token:" + oauthResponse.accessToken);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return oauthResponse;
    }

    /**
     * Request a new access token from a refresh token
     * @param refreshToken the refresh token used to ask for a new access token
     * @param scope the scope of the access
     * @param clientId S+ client Id
     * @return OAuth2Response object
     * @throws BonnierOpenIdException
     */
    public OAuth2Response refreshAccessToken(String refreshToken, String scope, String clientId) throws BonnierOpenIdException {
        OAuth2Response oauthResponse = null;

        try {
            Request.Builder requestBuilder = new Request.Builder().url(endpoint + "/token");
            FormBody.Builder form = new FormBody.Builder();
            form.add("grant_type", "refresh_token");
            form.add("refresh_token", refreshToken);
            if (scope != null) {
                form.add("scope", scope);
            }
            if(clientId != null) {
                form.add("client_id", clientId);
            } else if (this.clientId != null){
                form.add("client_id", this.clientId);
            } else {
                requestBuilder.header("Authorization", "Bearer " + accessToken());
            }
            Request request = requestBuilder.post(form.build()).build();
            Response response = httpClient.newCall(request).execute();
            oauthResponse = new Gson().fromJson(response.body().string(), OAuth2Response.class);
            Log.d(TAG, "Success! Access token:" + oauthResponse.accessToken);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return oauthResponse;
    }

    /**
     * Check if access token is valid or not
     * @param token access token to validate
     * @return true if token still valid, false otherwise
     * @throws BonnierOpenIdException
     */
    public boolean validateToken(String token) throws BonnierOpenIdException {
        boolean valid = false;
        try {
            HttpUrl url =  HttpUrl.parse(endpoint + "/validate").newBuilder().addQueryParameter("access_token", token).build();
            Request request = new Request.Builder()
                    .url(url.toString())
                    .get()
                    .build();

            Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                valid = true;
            }
        } catch (Exception ex) {
            throw new BonnierOpenIdException(ex);
        }
        return valid;
    }

    /**
     * Revoke the use of a token (access or refresh token)
     * @param token access or refresh token
     * @param clientId S+ client Id
     */
    public void revokeToken(String token, String clientId) {
        try {
            Request.Builder requestBuilder = new Request.Builder().url(endpoint + "/revoke");
            FormBody.Builder form = new FormBody.Builder();
            form.add("token", token);
            form.add("token_type_hint", "access_token");

            if(clientId != null) {
                form.add("client_id", clientId);
            } else if (this.clientId != null){
                form.add("client_id", this.clientId);
            } else {
                requestBuilder.header("Authorization", "Bearer " + accessToken());
            }

            Request request = requestBuilder.post(form.build()).build();
            Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                Log.d(TAG,"Success revoke token : " + token);
            } else {
                Log.d(TAG,"Error revoke token : " + token);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Verify signature of ID token and extract information
     * @param idToken ID token got in the same time with access token
     * @param clientId S+ client Id
     * @return ClaimsSet containing data of user
     */
    public ClaimsSet verifyIdToken(String idToken, String clientId) {
        String client = clientId;
        if(client == null) {
            client = this.clientId;
        }
        return jwsUtil.verifyContent(idToken, client, endpoint + "/keys");
    }

    /**
     * Get user information from oauth2 endpoint
     * @param accessToken
     * @return JSON object with info of user
     */
    public UserInfo getUserInfo(String accessToken) {
        UserInfo userInfo = null;
        try {
            Request request = new Request.Builder()
                    .url(endpoint + "/userinfo")
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-Type", "application/json")
                    .build();
            Log.d(TAG, "Request: " + request.toString());
            Response response = httpClient.newCall(request).execute();
            String responseBody = response.body().string();
            Log.d(TAG, "Response: " + responseBody);

            userInfo = new Gson().fromJson(responseBody, UserInfo.class);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return userInfo;
    }

}
