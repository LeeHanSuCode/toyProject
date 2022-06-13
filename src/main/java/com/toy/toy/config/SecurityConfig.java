package com.toy.toy.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity      //기본적인 웹보안을 활성화 시키겠다는 어노테이션
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    //추가적인 설정을 위해서 WebSecurityConfigurer 인터페이스를 이용하거나,
    //WebSecurityConfigurerAdapter 를 extends하는 방법이 있다.

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http
                .authorizeRequests()                                 //HttpServletRequest를 사용하는 요청들에 대한 접근제한을 하겠다라는 의미
                .antMatchers("/api/hello").permitAll()    //해당 경로에 대한 것은 인증없이 접근 허용한다는 의미
                .anyRequest().authenticated();                       //나머지는 인증을 해야 한다는 의미
    }



    //h2-console 하위 모든 요청들과 파비콘 관련 요청은 spring security 로직을 수행하지 않도록 한다.
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(
                "/h2-console/**"
                ,"/favicon.ico"
        );
    }
}
