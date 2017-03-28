package com.bip.androidopenidclient.utils;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Created by rene on 06/03/17.
 */

public class SplusUtil {
    public static String createOpenIdState() {
        String state = new BigInteger(130, new SecureRandom()).toString(32);
        return state;
    }
}
