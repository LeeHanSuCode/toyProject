package com.toy.toy.controller.exception_controller.exception;

public class FilesNotFoundException extends RuntimeException{

    public FilesNotFoundException() {
        super();
    }

    public FilesNotFoundException(String message) {
        super(message);
    }
}
