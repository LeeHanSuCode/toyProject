package com.toy.toy.interceptor;

import com.toy.toy.StaticVariable;
import com.toy.toy.controller.exception_controller.exception.RequiredLoginException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.List;
import java.util.Optional;

import static com.toy.toy.StaticVariable.*;
import static org.springframework.http.HttpMethod.*;

@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {

    private String httpMethodNotMembers = GET.toString();
    private String httpMethodMembers = POST.toString();
    private List<String> acceptOperation = List.of("/boards","/comments","/files");



    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String requestMethod = request.getMethod();
        String requestURI = request.getRequestURI();

        if(sessionCheck(request)){
            if(!checkHttpMethod(requestMethod , requestURI)){
                log.info("미인증 사용자 요청 경로 ={}", request.getRequestURI());
                throw new RequiredLoginException("로그인이 필요합니다.", request.getRequestURI());
            }
        }

        return true;
    }

    //세션 체크
    private boolean sessionCheck(HttpServletRequest request){
        HttpSession session = request.getSession();

        return session == null || session.getAttribute(LOGIN_MEMBER) == null;
    }


    //httpMethod중에 접근하지 못하는 메소드 제한
    private boolean checkHttpMethod(String requestMethod,String requestURI){

        if(requestURI.contains("/members")){
            return this.httpMethodMembers.equals(requestMethod);
        }else
            return this.httpMethodNotMembers.equals(requestMethod);
        }



}
