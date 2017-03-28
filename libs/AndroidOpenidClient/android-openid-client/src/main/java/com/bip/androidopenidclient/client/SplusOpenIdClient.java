package com.bip.androidopenidclient.client;

import android.util.Log;

import com.bip.androidopenidclient.entity.ClaimsSet;
import com.bip.androidopenidclient.entity.DiscoveryData;
import com.bip.androidopenidclient.entity.UserInfo;
import com.bip.androidopenidclient.exceptions.BonnierOpenIdException;
import com.google.gson.Gson;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by rene on 06/03/17.
 */

public abstract class SplusOpenIdClient {
    private static final String TAG = "SplusOpenIdClient";

    protected static ApiClient apiClient;

    /**
     * Basic constructor.
     * @param endpoint the oauth2 endpoint to access the API
     */
    public SplusOpenIdClient(String endpoint) {
        apiClient = new ApiClient(endpoint);
    }

    /**
     * Full constructor
     * @param endpoint the oauth2 endpoint to access the API
     * @param clientId S+ client Id
     */
    public SplusOpenIdClient(String endpoint, String clientId) {
        apiClient = new ApiClient(endpoint, clientId);
    }

    /**
     * Verify signature of ID token and extract information (init with clientId)
     * @param idToken ID token got in the same time with access token
     * @return ClaimsSet containing data of user
     */
    public ClaimsSet verifyIdToken(String idToken) {
        return verifyIdToken(idToken, null);
    }

    /**
     * Verify signature of ID token and extract information (init without clientId)
     * @param idToken ID token got in the same time with access token
     * @param clientId S+ client Id
     * @return ClaimsSet containing data of user
     */
    public ClaimsSet verifyIdToken(String idToken, String clientId) {
        return apiClient.verifyIdToken(idToken, clientId);
    }

    /**
     * Check if access token is valid or not
     * @param token access token to validate
     * @return true if token still valid, false otherwise
     * @throws BonnierOpenIdException
     */
    public boolean validateAccessToken(String token) throws BonnierOpenIdException {
        if (token != null) {
            return apiClient.validateToken(token);
        } else {
            return false;
        }
    }

    /**
     * Get user information from oauth2 endpoint
     * @param accessToken
     * @return JSON object with info of user
     */
    public UserInfo getUserInfo(String accessToken) {
        Log.d(TAG, "UserInfo with accessToken:" + accessToken);
        UserInfo userInfo =  apiClient.getUserInfo(accessToken);

        return userInfo;
    }

    /**
     * Get Open ID configuration from the discovery endpoint
     * @param discoveryUrl url of the discovery endpoint of OpenID
     * @return JSON object with info of user
     */
    public static DiscoveryData getOpenIdConfiguration(String discoveryUrl) {
        DiscoveryData discoveryData = new DiscoveryData();

        OkHttpClient httpClient = new OkHttpClient();
        try {
            Request request = new Request.Builder().url(discoveryUrl).get().build();
            Response response = httpClient.newCall(request).execute();
            discoveryData = new Gson().fromJson(response.body().string(), DiscoveryData.class);
        } catch (Exception e) {
            Log.d(TAG, "Failed to catch configuration at the discovery endpoint : " + discoveryUrl);
        }

        return discoveryData;
    }
}
