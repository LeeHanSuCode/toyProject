package com.toy.toy.controller.exception_controller.exception;

public class MemberNotFoundException extends RuntimeException {

    public MemberNotFoundException() {
        super();
    }

    public MemberNotFoundException(String message) {
        super(message);
    }
}
