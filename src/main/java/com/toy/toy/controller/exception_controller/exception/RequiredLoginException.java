package com.toy.toy.controller.exception_controller.exception;

public class RequiredLoginException extends RuntimeException{

    private String requestURI;

    public RequiredLoginException(String message , String requestURI) {
        super(message);
        this.requestURI = requestURI;
    }

    public String getRequestURI() {
        return requestURI;
    }
}
