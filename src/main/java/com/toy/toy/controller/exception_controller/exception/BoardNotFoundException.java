package com.toy.toy.controller.exception_controller.exception;

public class BoardNotFoundException  extends RuntimeException{

    public BoardNotFoundException() {
        super();
    }

    public BoardNotFoundException(String message) {
        super(message);
    }
}
