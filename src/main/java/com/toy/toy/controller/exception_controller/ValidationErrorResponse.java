package com.toy.toy.controller.exception_controller;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
public class ValidationErrorResponse {

    private List<String> messages;
    private String fieldName;
    private String rejectedValue;
}
