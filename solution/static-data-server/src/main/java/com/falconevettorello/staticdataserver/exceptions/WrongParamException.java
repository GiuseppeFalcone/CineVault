package com.falconevettorello.staticdataserver.exceptions;

public class WrongParamException extends RuntimeException {
    public WrongParamException(String message) {
        super(message);
    }
}
