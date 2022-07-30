package com.toy.toy.controller.exception_controller;


import com.toy.toy.StaticVariable;
import com.toy.toy.controller.HomeController;
import com.toy.toy.controller.exception_controller.exception.*;

import lombok.extern.slf4j.Slf4j;

import org.hibernate.EntityMode;
import org.springframework.context.MessageSource;

import org.springframework.context.NoSuchMessageException;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.http.converter.HttpMessageNotReadableException;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.toy.toy.StaticVariable.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;


@Slf4j
@RestController
@ControllerAdvice
public class ApiExceptionController extends ResponseEntityExceptionHandler {

    private MessageSource messageSource;

    public ApiExceptionController(MessageSource messageSource){
        this.messageSource = messageSource;
    }

    //로그인 안한 사용자가 로그인이 필요한 서비스에 접근할 떄
    @ExceptionHandler
    public ResponseEntity requiredLoginException(RequiredLoginException exception , WebRequest webRequest){
        Map<String , String> requiredLogin = new HashMap<>();
        requiredLogin.put("info",exception.getMessage());
        requiredLogin.put("requestURI",exception.getRequestURI());


        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                EntityModel.of(requiredLogin)
                        .add(linkTo(HomeController.class).withRel("main-page")));

    }


    //회원 리소스를 찾지 못할 때
    @ExceptionHandler
    public ResponseEntity memberNotFoundHandler(MemberNotFoundException exception , WebRequest request){
        EntityModel memberNotFound = commonNotFoundHandler("MemberNotFound", exception, request);
        memberNotFound.add(Link.of("/docs/index.html#_회원_조회_실패").withRel("profile"));

        return new ResponseEntity<>(memberNotFound,HttpStatus.NOT_FOUND);
    }

    //게시글 리소스를 찾지 못할 때
    @ExceptionHandler
    public ResponseEntity boardNotFoundHandler(BoardNotFoundException exception ,  WebRequest request){

        EntityModel boardNotFound = commonNotFoundHandler("BoardNotFound", exception, request);
        boardNotFound.add(Link.of("/docs/index.html#_회원_조회_실패").withRel("profile"));

        return new ResponseEntity<>(boardNotFound, HttpStatus.NOT_FOUND);
    }

    //댓글 리소스를 찾지 못할 때
    @ExceptionHandler
    public ResponseEntity commentNotFoundHandler(CommentNotFoundException exception ,  WebRequest request){

        EntityModel commentsNotFound = commonNotFoundHandler("CommentsNotFound", exception, request);
        commentsNotFound.add(Link.of("/docs/index.html#_회원_조회_실패").withRel("profile"));

        return new ResponseEntity<>(commentsNotFound , HttpStatus.NOT_FOUND);
    }

    //파일 리소스를 찾지 못할 때
    @ExceptionHandler
    public ResponseEntity filesNotFoundHandler(FilesNotFoundException exception ,  WebRequest request){

        EntityModel filesNotFound = commonNotFoundHandler("FilesNotFound", exception, request);
        filesNotFound.add(Link.of("/docs/index.html#_회원_조회_실패").withRel("profile"));

        return new ResponseEntity<>(filesNotFound , HttpStatus.NOT_FOUND);
    }



    //에러 발생한 시간 반환(format)
    private String occurExceptionTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    //공통부분 처리(템플릿 역할)
    private EntityModel commonNotFoundHandler(String exceptionName , Exception exception , WebRequest request){


        return EntityModel.of(ErrorResponse.builder()
                .timestamp(occurExceptionTime())
                .code(exceptionName)
                .path(request.getDescription(false))
                .message(exception.getMessage())
                .build())
                .add(Link.of("http://www.localhost:8080").withRel("main-page"));
    }


    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return  handleValidationNotFieldMatchedException(new ValidationNotFieldMatchedException(ex.getBindingResult()),request);
    }


/*
    Bean Validation 유효성 검증 통과하지 못하였을 경우.
*/
    @ExceptionHandler
    public ResponseEntity handleValidationNotFieldMatchedException(
            ValidationNotFieldMatchedException ex, WebRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", occurExceptionTime());
        body.put("status",HttpStatus.BAD_REQUEST);
        body.put("path",request.getDescription(false));

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

       ;

        EntityModel<Map<String, Object>> model = EntityModel.of(body)
                .add(linkTo(HomeController.class).withRel(MAIN_PAGE))
                .add(Link.of("/docs/index.html").withRel(PROFILE));

        return new ResponseEntity(model,encodingHeaders(),HttpStatus.BAD_REQUEST);
    }


    //응답 헤더 지정
    private HttpHeaders encodingHeaders(){
        HttpHeaders resHeaders = new HttpHeaders();
        resHeaders.add("Content-Type", "application/hal+json;charset=UTF-8");

        return resHeaders;
    }

   /* private String getProfileLink(String objectName){

        if(objectName.equals("joinMemberDto")){
            return "/docs/index.html#_회원_가입실패";
        }else if(objectName.equals("updateMemberDto")){
            return "/docs/index.html#_회원_수정실패";
        }else if(objectName.equals("loginMemberDto")){
            return "/docs/index.html#_로그인_실패";
        }

        return null;
    }*/




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

        body.put("message" , "올바르지 않은 형식의 값이 Json에 포함되어 있습니다. 알맞은 데이터를 입력해주세요");

        return new ResponseEntity<>(body, HttpStatus.METHOD_NOT_ALLOWED);
    }




    //거절된 값을 얻어온다.
    private String getRejectedValue(FieldError fe) {
        String rejectedValue = null;

        if(fe.getRejectedValue() == null || fe.getRejectedValue().toString().isBlank()){
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



}
