package com.toy.toy.controller.exception_controller;

import com.toy.toy.controller.exception_controller.exception.BoardNotFoundException;
import com.toy.toy.controller.exception_controller.exception.CommentNotFoundException;
import com.toy.toy.controller.exception_controller.exception.FilesNotFoundException;
import com.toy.toy.controller.exception_controller.exception.MemberNotFoundException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ApiExceptionController {

    @ExceptionHandler
    public ResponseEntity<ErrorResult> memberNotFoundHandler(MemberNotFoundException e){
        errorLog(e);
        return new ResponseEntity(new ErrorResult("MemberNotFound" , e.getMessage()) , HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResult> boardNotFoundHandler(BoardNotFoundException e){
        errorLog(e);
        return new ResponseEntity(new ErrorResult("BoardNotFound" , e.getMessage()) , HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResult> commentNotFoundHandler(CommentNotFoundException e){
        errorLog(e);
        return new ResponseEntity(new ErrorResult("CommentNotFound" , e.getMessage()) , HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResult> filesNotFoundHandler(FilesNotFoundException e){
        errorLog(e);
        return new ResponseEntity(new ErrorResult("FilesNotFound" , e.getMessage()) , HttpStatus.NOT_FOUND);
    }

    private void errorLog(RuntimeException e){
        log.error("[exceptionHandler] ex" , e);
    }

    @Getter
     class ErrorResult{
        private String code;
        private String message;

        public ErrorResult(String code, String message) {
            this.code = code;
            this.message = message;
        }
    }
}
