package com.toy.toy.controller.exception_controller.exception;

public class CommentNotFoundException  extends RuntimeException{
    public CommentNotFoundException() {
        super();
    }

    public CommentNotFoundException(String message) {
        super(message);
    }
}
