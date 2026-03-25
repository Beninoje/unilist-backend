package com.unilist.campora.exceptions;

public class OtpRequiredException extends RuntimeException{
    public OtpRequiredException(String message){
        super(message);
    }
}
