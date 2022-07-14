package com.toy.toy.controller;

import com.toy.toy.StaticVariable;
import com.toy.toy.argumentResolver.Login;
import com.toy.toy.controller.exception_controller.exception.LoginInfoNotMatchedException;
import com.toy.toy.controller.exception_controller.exception.MemberNotFoundException;
import com.toy.toy.dto.LoginMemberDto;
import com.toy.toy.entity.Member;
import com.toy.toy.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.URI;
import java.util.Optional;

import static com.toy.toy.StaticVariable.*;

@Controller
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final MemberRepository memberRepository;

    /*@GetMapping
    public ResponseEntity home(@Login LoginMemberDto loginMemberDto){
        if(loginMemberDto == null){
            return
        }


    }

*/

    @PostMapping ("/login")
    public ResponseEntity login(@RequestBody LoginMemberDto loginMemberDto , HttpServletRequest request){
        log.info("userId={}" , loginMemberDto.getUserId());

        Member findMember = memberRepository.findByUserId(loginMemberDto.getUserId())
                .orElseThrow(() -> new LoginInfoNotMatchedException("회원 정보가 일치하지 않습니다."));

        if(!findMember.getPassword().equals(loginMemberDto.getPassword())){
            throw new LoginInfoNotMatchedException("회원 정보가 일치하지 않습니다.");
        }

        loginMemberDto.setId(findMember.getId());

        request.getSession().setAttribute(LOGIN_MEMBER,loginMemberDto);

        return ResponseEntity.ok().body(new CustomEntityModel()
                .add(WebMvcLinkBuilder.linkTo(LoginController.class).withRel("main-page")));
    }


    @GetMapping("/logout")
    public ResponseEntity logout(HttpServletRequest request){
        HttpSession session = request.getSession(false);

        if(session != null) {
            session.invalidate();
        }

        return ResponseEntity.ok().body(new CustomEntityModel()
                .add(WebMvcLinkBuilder.linkTo(LoginController.class).withRel("main-page")));
    }
}
