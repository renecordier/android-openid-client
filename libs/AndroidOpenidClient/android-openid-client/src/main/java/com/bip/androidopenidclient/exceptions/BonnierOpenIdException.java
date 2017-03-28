package com.bip.androidopenidclient.exceptions;

/**
 * Created by rene on 06/03/17.
 */

public class BonnierOpenIdException extends RuntimeException{
    public BonnierOpenIdException(Exception e) {
        super(e);
    }
    public BonnierOpenIdException(String message) {
        super(message);
    }
}
