package com.bip.androidopenidclient.utils;


import com.bip.androidopenidclient.entity.ClaimsSet;
import com.bip.androidopenidclient.entity.IdTokenClaimsSet;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

/**
 * Created by rene on 06/03/17.
 */

public class TokenUtil {
    public static ClaimsSet parseClaimsSet(String jsonIdToken) {
        try {
            return new IdTokenClaimsSet(new JSONObject(jsonIdToken));
        } catch (ParseException e) {
            return null;
        } catch (JSONException e) {
            return null;
        }
    }
}
