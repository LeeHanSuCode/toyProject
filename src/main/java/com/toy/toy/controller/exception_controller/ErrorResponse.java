package com.toy.toy.controller.exception_controller;

import lombok.Builder;
import lombok.Getter;
import org.springframework.validation.Errors;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@Builder
@Getter
public class ErrorResponse{
    private String timestamp;
    private String code;
    private String message;
    private String path;
}