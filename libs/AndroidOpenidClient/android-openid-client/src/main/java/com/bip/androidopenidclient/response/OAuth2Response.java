package com.bip.androidopenidclient.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;


/**
 * Created by rene on 06/03/17.
 */
public class OAuth2Response {
    @SerializedName("access_token")
    public String accessToken;
    @SerializedName("token_type")
    public String tokenType;
    @SerializedName("expires_in")
    public Integer expiresIn;
    @SerializedName("refresh_token")
    public String refreshToken;
    // Optional
    @SerializedName("search_token")
    public List<String> searchToken;
    @SerializedName("id_token")
    public String idToken;
    public String scope;
    @SerializedName("@httpResponseCode")
    public Integer httpResponseCode;
    @SerializedName("@requestId")
    public String requestId;
    public String code;
    public String state;

    // Error fields
    public String error;
    @SerializedName("error_uri")
    public String errorUri;
    @SerializedName("@errorCode")
    public ApiErrorCode errorCode;
    public String errorMsg;
}
