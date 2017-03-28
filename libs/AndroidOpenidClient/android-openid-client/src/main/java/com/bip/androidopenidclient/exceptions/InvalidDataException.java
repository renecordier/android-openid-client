package com.bip.androidopenidclient.exceptions;

/**
 * Created by rene on 06/03/17.
 */

public class InvalidDataException extends RuntimeException {
    public InvalidDataException(String message) {
        super(message);
    }

    public InvalidDataException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
