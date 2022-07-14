package com.toy.toy.controller.exception_controller.exception;

public class LoginInfoNotMatchedException extends RuntimeException{

    public LoginInfoNotMatchedException() {
        super();
    }
    public LoginInfoNotMatchedException(String message) {
        super(message);
    }
}
