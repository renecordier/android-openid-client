package com.bip.androidopenidclient.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by rene on 06/03/17.
 */

public class DiscoveryData {
    @SerializedName("issuer")
    private String issuer;
    @SerializedName("authorization_endpoint")
    private String authorizationEndpoint;
    @SerializedName("token_endpoint")
    private String tokenEndpoint;
    @SerializedName("userinfo_endpoint")
    private String userinfoEndpoint;
    @SerializedName("revocation_endpoint")
    private String revocationEndpoint;
    @SerializedName("check_session_iframe")
    private String checkSessionIframe;
    @SerializedName("end_session_endpoint")
    private String endSessionEndpoint;
    @SerializedName("jwks_uri")
    private String jwksUri;

    @SerializedName("response_types_supported")
    private List<String> responseTypesSupported;
    @SerializedName("subject_types_supported")
    private List<String> subjectTypesSupported;
    @SerializedName("id_token_signing_alg_values_supported")
    private List<String> idTokenSigningAlgValuesSupported;
    @SerializedName("scopes_supported")
    private List<String> scopesSupported;
    @SerializedName("token_endpoint_auth_methods_supported")
    private List<String> tokenEndpointAuthMethodsSupported;
    @SerializedName("claims_supported")
    private List<String> claimsSupported;

    @SerializedName("frontchannel_logout_supported")
    private boolean frontchannelLogoutSupported;
    @SerializedName("frontchannel_logout_session_supported")
    private boolean frontchannelLogoutSessionSupported;
    @SerializedName("backchannel_logout_supported")
    private boolean backchannelLogoutSupported;
    @SerializedName("backchannel_logout_session_supported")
    private boolean backchannelLogoutSessionSupported;

    //private List<String> response_types_supported;
    //private List<String> subject_types_supported;

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getAuthorizationEndpoint() {
        return authorizationEndpoint;
    }

    public void setAuthorizationEndpoint(String authorizationEndpoint) {
        this.authorizationEndpoint = authorizationEndpoint;
    }

    public String getTokenEndpoint() {
        return tokenEndpoint;
    }

    public void setTokenEndpoint(String tokenEndpoint) {
        this.tokenEndpoint = tokenEndpoint;
    }

    public String getUserinfoEndpoint() {
        return userinfoEndpoint;
    }

    public void setUserinfoEndpoint(String userinfoEndpoint) {
        this.userinfoEndpoint = userinfoEndpoint;
    }

    public String getRevocationEndpoint() {
        return revocationEndpoint;
    }

    public void setRevocationEndpoint(String revocationEndpoint) {
        this.revocationEndpoint = revocationEndpoint;
    }

    public String getCheckSessionIframe() {
        return checkSessionIframe;
    }

    public void setCheckSessionIframe(String checkSessionIframe) {
        this.checkSessionIframe = checkSessionIframe;
    }

    public String getEndSessionEndpoint() {
        return endSessionEndpoint;
    }

    public void setEndSessionEndpoint(String endSessionEndpoint) {
        this.endSessionEndpoint = endSessionEndpoint;
    }

    public String getJwksUri() {
        return jwksUri;
    }

    public void setJwksUri(String jwksUri) {
        this.jwksUri = jwksUri;
    }
}
