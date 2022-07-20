package com.toy.toy.argumentResolver;

import com.toy.toy.StaticVariable;
import com.toy.toy.dto.LoginMemberDto;
import com.toy.toy.dto.responseDto.LoginResponse;
import com.toy.toy.entity.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Objects;

@Slf4j
public class LoginArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        log.info("supportsParameter 실행");

        //1.Login어노테이션을 파라미터가 가지고 있는가?
        //2.파라미터의 타입이 Member가 맞는가?

        boolean hasLoginAnnotation = parameter.hasParameterAnnotation(Login.class);
        boolean hasMemberType = LoginResponse.class.isAssignableFrom(parameter.getParameterType());
        return hasLoginAnnotation && hasMemberType;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        log.info("resolveArgument 실행");

        HttpServletRequest request = (HttpServletRequest)webRequest.getNativeRequest();
        HttpSession session = request.getSession(false);
        log.info("session={}" , Objects.isNull(session));
        LoginResponse loginResponse = (LoginResponse) request.getSession().getAttribute(StaticVariable.LOGIN_MEMBER);

        if(session == null){
            return null;
        }

        return session.getAttribute(StaticVariable.LOGIN_MEMBER);
    }
}
