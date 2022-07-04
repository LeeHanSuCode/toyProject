package com.toy.toy.controller.exception_controller;


import com.toy.toy.controller.MemberController;
import com.toy.toy.controller.exception_controller.exception.*;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.core.MethodParameter;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@RestController
@ControllerAdvice
public class ApiExceptionController extends ResponseEntityExceptionHandler {

    private MessageSource messageSource;

    public ApiExceptionController(MessageSource messageSource){
        this.messageSource = messageSource;
    }

    //회원 리소스를 찾지 못할 때
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> memberNotFoundHandler(MemberNotFoundException exception , WebRequest request){
         return commonNotFoundHandler("MemberNotFound" , exception , request);
    }

    //게시글 리소스를 찾지 못할 때
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> boardNotFoundHandler(BoardNotFoundException exception ,  WebRequest request){

        return commonNotFoundHandler("BoardNotFound" , exception , request);
    }

    //댓글 리소스를 찾지 못할 때
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> commentNotFoundHandler(CommentNotFoundException exception ,  WebRequest request){

        return commonNotFoundHandler("CommentsNotFound" , exception , request);
    }

    //파일 리소스를 찾지 못할 때
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> filesNotFoundHandler(FilesNotFoundException exception ,  WebRequest request){

        return commonNotFoundHandler("FilesNotFound" , exception , request);
    }


    //에러 발생한 시간 반환(format)
    private String occurExceptionTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    //공통부분 처리(템플릿 역할)
    private ResponseEntity<ErrorResponse> commonNotFoundHandler(String exceptionName , Exception exception , WebRequest request){


        return new ResponseEntity(ErrorResponse.builder()
                .timestamp(occurExceptionTime())
                .code(exceptionName)
                .path(request.getDescription(false))
                .message(exception.getMessage())
                .build() , HttpStatus.NOT_FOUND);
    }



    /*
    Bean Validation 유효성 검증 통과하지 못하였을 경우.

    에러 스펙
    -> 발생시간
    -> 예외상태코드값
    -> 요청 경로
    -> 필드에러(메세지 , 거절된 값 , 필드이름)
    */

    //

      @ExceptionHandler
    public ResponseEntity<Object> handleValidationNotFieldMatchedException(
            ValidationNotFieldMatchedException ex, WebRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", occurExceptionTime());
        body.put("status",HttpStatus.BAD_REQUEST);
        body.put("path",request.getDescription(false));

          List<ObjectError> allErrors = ex.getBindingResult().getAllErrors();

          Map<String ,ValidationErrorResponse> filedErrorsInfo = new HashMap<>();


          ex.getBindingResult().getFieldErrors()
                  .stream().forEach(fe -> {

                                if(filedErrorsInfo.containsKey(fe.getField())){

                                    filedErrorsInfo.get(fe.getField()).getMessages().add(getMessageSource(fe));

                                }else{
                                    ValidationErrorResponse validationErrorResponse = ValidationErrorResponse.builder()
                                            .fieldName(fe.getField())
                                            .rejectedValue(getRejectedValue(fe))
                                            .messages(new ArrayList<>())
                                            .build();

                                    validationErrorResponse.getMessages().add(getMessageSource(fe));

                                    filedErrorsInfo.put(fe.getField() , validationErrorResponse);
                                }
                          });

        body.put("fieldErrors", filedErrorsInfo);

        return new ResponseEntity<>(body,HttpStatus.BAD_REQUEST);
    }

    //거절된 값을 얻어온다.
    private String getRejectedValue(FieldError fe) {
        String rejectedValue = null;

        if(fe.getRejectedValue() == null){
            rejectedValue = "값이 들어오지 않음";
        }else{
            rejectedValue = fe.getRejectedValue().toString();
        }
        return rejectedValue;
    }

    //error 메세지를 얻어온다.
    private String getMessageSource(FieldError fe) {
        return Arrays.stream(Objects.requireNonNull(fe.getCodes()))
                .map(c -> {
                    try {
                        Object[] argument = fe.getArguments();
                        return messageSource.getMessage(c, argument, null);
                    } catch (NoSuchMessageException e) {
                        return null;
                    }
                }).filter(Objects::nonNull)
                .findFirst()
                .orElse(fe.getDefaultMessage());
    }

  /* v3
  //여기까지 field의 map화 성공.
  //근데 group
   @ExceptionHandler
    public ResponseEntity<Object> handleValidationNotFieldMatchedException(
            ValidationNotFieldMatchedException ex, WebRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", occurExceptionTime());
        body.put("status",HttpStatus.BAD_REQUEST);
        body.put("path",request.getDescription(false));

        //구조 FieldErrors(List) -> 필드이름 , 거절된 값 , 메세지 목록 -> 아씨 어떻게 해야하는거야

        *//*Map<String,ValidationErrorResponse> fieldErrors = ex.getBindingResult().getFieldErrors()
                .stream().map(
                        fe ->{

                        }
                ).collect(Collectors.);*//*


        //-> timestamp:값
        //-> fieldError : 값
        //-> fieldName : ValidationErrorResponse (Map)
        //->


        //예외
        List<ValidationErrorResponse> fieldErrorsInfoList = ex.getBindingResult().getFieldErrors()
                .stream().map(
                        fe -> {

                            String message = Arrays.stream(Objects.requireNonNull(fe.getCodes()))
                                    .map(c -> {
                                        try {
                                            Object[] argument = fe.getArguments();
                                            return messageSource.getMessage(c, argument, null);
                                        } catch (NoSuchMessageException e) {
                                            return null;
                                        }
                                    }).filter(Objects::nonNull)
                                    .findFirst()
                                    .orElse(fe.getDefaultMessage());

                            String rejectedValue = null;

                            if(fe.getRejectedValue() == null){
                                 rejectedValue = "값이 들어오지 않음";
                            }else{
                                rejectedValue = fe.getRejectedValue().toString();
                            }

                            ValidationErrorResponse validationErrorResponse = ValidationErrorResponse.builder()
                                    .fieldName(fe.getField())
                                    .rejectedValue(rejectedValue)
                                    .messages(message)
                                    .build();

                            return validationErrorResponse;
                        }
                ).collect(Collectors.toList());

        Map<String, List<ValidationErrorResponse>> collect = fieldErrorsInfoList.stream().collect(Collectors.groupingBy(ValidationErrorResponse::getFieldName));


        body.put("fieldErrors", collect);

        return new ResponseEntity<>(body,HttpStatus.BAD_REQUEST);
    }*/



    //여기까기의 문제
    //메세지 커스텀화 성공.
    //-메세지가 username 대신 사용자 정의 이름이었으면 좋겠다.
    //-메세지로 인한 중복 아직.
    //-글로벌 에러러
/*
    @Ovrride
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", occurExceptionTime());
        body.put("status", status.value());
        body.put("path",request.getDescription(false));

        //구조 FieldErrors(List) -> 필드이름 , 거절된 값 , 메세지 목록 -> 아씨 어떻게 해야하는거야

        Map<String,ValidationErrorResponse> fieldErrors = ex.getBindingResult().getFieldErrors()
                .stream().map(
                        fe ->{

                        }
                ).collect(Collectors.);



        //예외
        List<Map> fieldErrors = ex.getBindingResult().getFieldErrors()
                .stream().map(
                        fe ->{
                            Map errorInfo = new HashMap();
                            errorInfo.put("rejectedValue" , fe.getRejectedValue());
                            errorInfo.put("fieldName" , fe.getField());

                            String message = Arrays.stream(Objects.requireNonNull(fe.getCodes()))
                                    .map(c -> {
                                        try {
                                            Object[] argument = fe.getArguments();
                                            return messageSource.getMessage(c, argument, null);
                                        }catch (NoSuchMessageException e){
                                            return null;
                                        }
                                    }).filter(Objects::nonNull)
                                    .findFirst()
                                    .orElse(fe.getDefaultMessage());

                            errorInfo.put("message",message);

                            return errorInfo;
                        }
                ).collect(Collectors.toList());


        body.put("fieldErrors", fieldErrors);

        return new ResponseEntity<>(body,status);
    }*/





    //여기서의 문제 , 글로벌 오류 처리를 해줄수가 없다.
    //예외 메세지를 message.properties에서 불러오지 않는다.
    //예외 메세지 떄문에 예외 필드가 중복 된다.

    //블로그에 사용할것.
 /*   @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", occurExceptionTime());
        body.put("status", status.value());
        body.put("path",request.getDescription(false));

        List<LinkedHashMap> fieldErrors = ex.getBindingResult().getFieldErrors()
                .stream().map(
                        fe ->{
                            LinkedHashMap linkedHashMap = new LinkedHashMap();
                            linkedHashMap.put("rejectedValue" , fe.getRejectedValue());
                            linkedHashMap.put("fieldName" , fe.getField());
                            linkedHashMap.put("message" , fe.getDefaultMessage());
                            //v2 메세지 리소스 처리.
                            // linkedHashMap.put("message" , messageSource.getMessage(fe.getCodes()[0] , null , null));
                            return linkedHashMap;
                        }
                ).collect(Collectors.toList());


        body.put("fieldErrors", fieldErrors);

        return new ResponseEntity<>(body,status);
    }

*/


    /*
     Request Json 데이터와 @RequestBody가 붙은 객체의 바인딩 과정에서
     타입이 매치되지 않아 Controller 조차 호출되지 못한 경우 발생하는 예외 처리.
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers,
                                                                  HttpStatus status, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", occurExceptionTime());
        body.put("status", status.value());
        body.put("path",request.getDescription(false));




        return new ResponseEntity<>(body, HttpStatus.METHOD_NOT_ALLOWED);
    }




}
