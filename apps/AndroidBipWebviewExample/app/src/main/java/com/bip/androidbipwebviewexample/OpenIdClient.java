package com.bip.androidbipwebviewexample;

import android.app.Application;

import com.bip.androidopenidclient.client.BipAuthorizationCodeFlowClient;

/**
 * Created by rene on 18/04/17.
 */

public class OpenIdClient extends Application {
    private final String endpoint = "https://api.qa.newsplus.se/v1/oauth2";
    private final String clientId = "ASK_S+"; //ask serviceplus for a proper client ID
    private BipAuthorizationCodeFlowClient ssoClient = new BipAuthorizationCodeFlowClient(endpoint, clientId);

    public BipAuthorizationCodeFlowClient getOpenIdClient(){
        return this.ssoClient;
    }

    public String getClientId(){
        return this.clientId;
    }
}
