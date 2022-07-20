package com.toy.toy.interceptor;

import com.toy.toy.StaticVariable;
import com.toy.toy.dto.responseDto.LoginResponse;
import com.toy.toy.entity.MemberGrade;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
/*
public class AdminCheckInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        String requestURI = request.getRequestURI();

        if(session != null) {
            LoginResponse loginResponse = (LoginResponse) session.getAttribute(StaticVariable.LOGIN_MEMBER);
            loginResponse.getMemberGrade().equals(MemberGrade.ADMIN)
        }

        return HandlerInterceptor.super.preHandle(request, response, handler);

    }
}*/
