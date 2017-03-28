package com.bip.androidopenidclient.client;

import com.bip.androidopenidclient.exceptions.BonnierOpenIdException;
import com.bip.androidopenidclient.exceptions.InvalidDataException;
import com.bip.androidopenidclient.response.OAuth2Response;

/**
 * Created by rene on 06/03/17.
 */

public class BipAuthorizationCodeFlowClient extends SplusOpenIdClient {
    /**
     * Constructor
     * @param endpoint the oauth2 endpoint to access the API
     */
    public BipAuthorizationCodeFlowClient(String endpoint) {
        super(endpoint);
    }

    /**
     * Full mobile Constructor
     * @param endpoint the oauth2 endpoint to access the API
     * @param clientId S+ client Id (must be a public client)
     */
    public BipAuthorizationCodeFlowClient(String endpoint, String clientId) {
        super(endpoint, clientId);
    }

    /**
     * Builds the authorize url with all the parameters needed
     * @param authorizationRequestUri the authorization endpoint url
     * @param clientId S+ client Id
     * @param redirectUri the redirect link to handle the response back from the server
     * @param scope the scope of the access
     * @param state random value to maintain the state between request and response
     * @param nonce unique value generated to prevent from replay attacks
     * @param display full display or iframe display (if user not logged in)
     * @param lc locale
     * @param loginHint To preffil the email on the login form (if user not logged in)
     * @param cancelUri Cancel callback url
     * @param codeChallenge A one time random generated string, result of the encryption method used
     * @param codeChallengeMethod The encryption method used for the code challenge
     * @return thhe built authorize url
     */
    public String getAuthorizeUrl(String authorizationRequestUri, String clientId, String redirectUri, String scope, String state,
                                  String nonce, String display, String lc, String loginHint, String cancelUri, String codeChallenge, String codeChallengeMethod) {
        if(authorizationRequestUri == null || clientId == null || redirectUri == null || scope == null || codeChallenge == null || codeChallengeMethod == null){
            throw new InvalidDataException("Some mandatory parameters are missing ! [authorizationRequestUri,clientId,redirectUri,scope,codeChallenge,codeChallengeMethod]");
        }
        String url = authorizationRequestUri + "?response_type=code";
        url += "&client_id=" + clientId;
        url += "&redirect_uri=" + redirectUri;
        url += "&scope=" + scope;
        url += state != null ? "&state=" + state : "";
        url += nonce != null ? "&nonce=" + nonce : "";
        url += display != null ? "&display=" + display : "";
        url += lc != null ? "&lc=" + lc : "";
        url += loginHint != null ? "&login_hint=" + loginHint : "";
        url += cancelUri != null ? "&cancel_uri=" + cancelUri : "";
        url += "&code_challenge=" + codeChallenge;
        url += "&code_challenge_method=" + codeChallengeMethod;

        return url;
    }

    /**
     * Builds the logout url with all the parameters needed
     * @param authorizationRequestUri the logout endpoint url
     * @param appId the application id of the client website
     * @param postLogoutRedirectUri the redirect link to handle the response back from the server
     * @param state random value to maintain the state between request and response
     * @return the build logout url
     */
    public String getLogoutUrl(String authorizationRequestUri, String appId, String postLogoutRedirectUri, String state) {
        if(authorizationRequestUri == null || appId == null || postLogoutRedirectUri == null){
            throw new InvalidDataException("Some mandatory parameters are missing ! [authorizationRequestUri,appId,postLogoutRedirectUri]");
        }
        String url = authorizationRequestUri;
        url += "?post_logout_redirect_uri=" + postLogoutRedirectUri;
        url += "&appId=" + appId;
        url += state != null ? "&state=" + state : "";

        return url;
    }

    /**
     * Requests access token from Bonnier Identity Provider using authorization code and PKCE (init with clientId)
     * @param code the code received in authorization code flow
     * @param redirectURI the redirect uri registered
     * @param longLivedToken if token is long lived (30d) or not (8h)
     * @param codeVerifier used for PKCE concept (in case client cannot store a secret)
     * @return OAuth2Response object
     * @throws BonnierOpenIdException
     */
    public OAuth2Response requestAccessTokenWithPKCE(String code,
                                                     String redirectURI,
                                                     Boolean longLivedToken,
                                                     String codeVerifier) throws BonnierOpenIdException {
        return requestAccessTokenWithPKCE(null, code, redirectURI, longLivedToken, codeVerifier);
    }

    /**
     * Requests access token from Bonnier Identity Provider using authorization code and PKCE (init without clientId)
     * @param clientId S+ client Id
     * @param code the code received in authorization code flow
     * @param redirectURI the redirect uri registered
     * @param longLivedToken if token is long lived (30d) or not (8h)
     * @param codeVerifier used for PKCE concept (in case client cannot store a secret)
     * @return OAuth2Response object
     * @throws BonnierOpenIdException
     */
    public OAuth2Response requestAccessTokenWithPKCE(String clientId,
                                                     String code,
                                                     String redirectURI,
                                                     Boolean longLivedToken,
                                                     String codeVerifier) throws BonnierOpenIdException {
        return apiClient.requestAccessTokenFromCodePKCE(clientId, code, redirectURI, longLivedToken, codeVerifier);
    }

    /**
     * Refresh access token (init with clientId)
     * @param refreshToken the token needed to refresh the access token
     * @param scope if want to restrict the scope more than the initial one
     * @return OAuth2Response object
     * @throws BonnierOpenIdException
     */
    public OAuth2Response refreshAccessToken(String refreshToken, String scope) throws BonnierOpenIdException {
        return refreshAccessToken(refreshToken, scope, null);
    }

    /**
     * Refresh access token (init without clientId)
     * @param refreshToken the token needed to refresh the access token
     * @param scope if want to restrict the scope more than the initial one
     * @param clientId S+ client Id
     * @return OAuth2Response object
     * @throws BonnierOpenIdException
     */
    public OAuth2Response refreshAccessToken(String refreshToken, String scope, String clientId) throws BonnierOpenIdException {
        return apiClient.refreshAccessToken(refreshToken, scope, clientId);
    }

    /**
     * Revoke access token (init with clientId)
     * @param token access token to revoke
     */
    public void revokeAccessToken(String token) {
        revokeAccessToken(token, null);
    }

    /**
     * Revoke access token (init without clientId)
     * @param token access token to revoke
     * @param clientId S+ client Id
     */
    public void revokeAccessToken(String token, String clientId) {
        apiClient.revokeToken(token, clientId);
    }
}
