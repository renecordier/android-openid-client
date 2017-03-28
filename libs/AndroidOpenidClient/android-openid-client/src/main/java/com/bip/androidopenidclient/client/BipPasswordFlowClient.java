package com.bip.androidopenidclient.client;

import com.bip.androidopenidclient.exceptions.BonnierOpenIdException;
import com.bip.androidopenidclient.response.OAuth2Response;

/**
 * Created by rene on 06/03/17.
 */

public class BipPasswordFlowClient extends SplusOpenIdClient {
    /**
     * Constructor
     * @param endpoint the oauth2 endpoint to access the API
     */
    public BipPasswordFlowClient(String endpoint) {
        super(endpoint);
    }

    /**
     * Full mobile Constructor
     * @param endpoint the oauth2 endpoint to access the API
     * @param clientId S+ client Id (must be a public client)
     */
    public BipPasswordFlowClient(String endpoint, String clientId) {
        super(endpoint, clientId);
    }

    /**
     * Requests access token from Bonnier Identity Provider using password grant type flow (init with ClientId)
     * @param scope OpenId scope
     * @param username user name credentials
     * @param password password credentials
     * @param longLivedToken long lived token or not
     * @return OAuth2Response object
     * @throws BonnierOpenIdException
     */
    public OAuth2Response requestAccessToken(String scope,
                                             String username,
                                             String password,
                                             Boolean longLivedToken) throws BonnierOpenIdException {
        return requestAccessToken(null, scope, username, password, longLivedToken);
    }

    /**
     * Requests access token from Bonnier Identity Provider using password grant type flow (init without ClientId)
     * @param clientId S+ client Id
     * @param scope OpenId scope
     * @param username user name credentials
     * @param password password credentials
     * @param longLivedToken long lived token or not
     * @return OAuth2Response object
     * @throws BonnierOpenIdException
     */
    public OAuth2Response requestAccessToken(String clientId,
                                             String scope,
                                             String username,
                                             String password,
                                             Boolean longLivedToken) throws BonnierOpenIdException {
        return apiClient.requestAccessTokenWithPassword(clientId, scope, username, password, longLivedToken);
    }

    /**
     * Refresh access token (init with ClientId)
     * @param refreshToken the token needed to refresh the access token
     * @param scope if want to restrict the scope more than the initial one
     * @return OAuth2Response object
     * @throws BonnierOpenIdException
     */
    public OAuth2Response refreshPublicAccessToken(String refreshToken, String scope) throws BonnierOpenIdException {
        return refreshPublicAccessToken(refreshToken, scope, null);
    }

    /**
     * Refresh access token (init without ClientId)
     * @param refreshToken the token needed to refresh the access token
     * @param scope if want to restrict the scope more than the initial one
     * @param clientId S+ client Id
     * @return OAuth2Response object
     * @throws BonnierOpenIdException
     */
    public OAuth2Response refreshPublicAccessToken(String refreshToken, String scope, String clientId) throws BonnierOpenIdException {
        return apiClient.refreshAccessToken(refreshToken, scope, clientId);
    }

    /**
     * Revoke access token (init with ClientId)
     * @param token access token to revoke
     */
    public void revokeAccessToken(String token) {
        revokeAccessToken(token, null);
    }

    /**
     * Revoke access token (init without ClientId)
     * @param token access token to revoke
     * @param clientId S+ client Id
     */
    public void revokeAccessToken(String token, String clientId) {
        apiClient.revokeToken(token, clientId);
    }
}
