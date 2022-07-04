package com.toy.toy.controller.exception_controller.exception;

import org.springframework.validation.BindingResult;

public class ValidationNotFieldMatchedException extends RuntimeException{

    private BindingResult bindingResult;

    public ValidationNotFieldMatchedException(BindingResult bindingResult){
        this.bindingResult = bindingResult;
    }

    public BindingResult getBindingResult() {
        return bindingResult;
    }
}
