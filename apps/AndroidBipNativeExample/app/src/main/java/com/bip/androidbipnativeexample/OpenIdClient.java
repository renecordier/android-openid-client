package com.bip.androidbipnativeexample;

import android.app.Application;

import com.bip.androidopenidclient.client.BipPasswordFlowClient;

/**
 * Created by rene on 10/03/17.
 */

public class OpenIdClient extends Application{
    private final String endpoint = "https://api.qa.newsplus.se/v1/oauth2";
    private final String clientId = "ASK_SERVICE_+";
    private BipPasswordFlowClient ssoClient = new BipPasswordFlowClient(endpoint, clientId);

    public BipPasswordFlowClient getOpenIdClient(){
        return this.ssoClient;
    }

    public String getClientId(){
        return this.clientId;
    }
}
