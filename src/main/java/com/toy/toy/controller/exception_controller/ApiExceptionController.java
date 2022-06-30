package com.toy.toy.controller.exception_controller;


import com.toy.toy.controller.exception_controller.exception.BoardNotFoundException;
import com.toy.toy.controller.exception_controller.exception.CommentNotFoundException;
import com.toy.toy.controller.exception_controller.exception.FilesNotFoundException;
import com.toy.toy.controller.exception_controller.exception.MemberNotFoundException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Slf4j
@ControllerAdvice
public class ApiExceptionController extends ResponseEntityExceptionHandler {


    @ExceptionHandler
    public ResponseEntity<ErrorResult> memberNotFoundHandler(MemberNotFoundException exception){
        errorLog(exception);
        System.out.println("호출은 되나?");

        return new ResponseEntity(ErrorResult.builder()
                .timestamp(occurExceptionTime())
                .code("MemberNotFound")
                .message(exception.getMessage())
                .build()
                , HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResult> boardNotFoundHandler(BoardNotFoundException exception){
        errorLog(exception);
        return new ResponseEntity(ErrorResult.builder()
                .timestamp(occurExceptionTime())
                .code("BoardNotFound")
                .message(exception.getMessage())
                .build()
                , HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResult> commentNotFoundHandler(CommentNotFoundException exception){
        errorLog(exception);
        return new ResponseEntity(ErrorResult.builder()
                .timestamp(occurExceptionTime())
                .code("CommentsNotFound")
                .message(exception.getMessage())
                .build()
                , HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResult> filesNotFoundHandler(FilesNotFoundException exception){
        errorLog(exception);
        return new ResponseEntity(ErrorResult.builder()
                .timestamp(occurExceptionTime())
                .code("FilesNotFound")
                .message(exception.getMessage())
                .build()
                , HttpStatus.NOT_FOUND);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,WebRequest request) {
        log.info("호출되라!");

        ErrorResult errorResult = ErrorResult.builder()
                .timestamp(occurExceptionTime())
                .code("Not Valid")
                .message(ex.getMessage())
                .errors(ex.getBindingResult())
                .build();

        return new ResponseEntity(errorResult,HttpStatus.BAD_REQUEST);
    }


    //에러 log남겨주기
    private void errorLog(RuntimeException e){
        log.error("[exceptionHandler] ex={}" , e);
    }

    //에러 발생한 시간 반환(format)
    private String occurExceptionTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }


}
