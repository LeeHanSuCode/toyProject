package com.toy.toy.controller.exception_controller;

import lombok.Builder;
import lombok.Getter;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;

@Builder
@Getter
public class ErrorResult{
    private String timestamp;
    private String code;
    private String message;
    private Errors errors;
}