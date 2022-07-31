package com.toy.toy.controller;


import com.toy.toy.controller.exception_controller.exception.ValidationNotFieldMatchedException;
import com.toy.toy.dto.validationDto.LoginMemberDto;
import com.toy.toy.dto.responseDto.LoginResponse;
import com.toy.toy.entity.Member;
import com.toy.toy.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

import static com.toy.toy.StaticVariable.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/login")
public class LoginController {

    private final MemberRepository memberRepository;



    @PostMapping
    public ResponseEntity login(@RequestBody @Validated LoginMemberDto loginMemberDto , BindingResult bindingResult,
                                HttpServletRequest request){


        if(bindingResult.hasErrors()){
            throw new ValidationNotFieldMatchedException(bindingResult);
        }

        //아이디가 없는 경우
        Optional<Member> optionalFindMember = memberRepository.findByUserId(loginMemberDto.getUserId());

        if(!optionalFindMember.isPresent()){
            bindingResult.rejectValue("userId" , "NotExist" , "존재하지 않는 회원 입니다.");
            throw new ValidationNotFieldMatchedException(bindingResult);
        }

        Member findMember = optionalFindMember.get();

        //비밀번호가 일치하지 않는 경우
        if(!findMember.getPassword().equals(loginMemberDto.getPassword())){
            bindingResult.rejectValue("password" , "NotEquals" , "비밀번호가 일치하지 않습니다.");
            throw new ValidationNotFieldMatchedException(bindingResult);
        }

        //세션 생성
        LoginResponse loginResponse = LoginResponse.builder()
                .userId(findMember.getUserId())
                .id(findMember.getId())
                .memberGrade(findMember.getMemberGrade())
                .build();

        request.getSession().setAttribute(LOGIN_MEMBER,loginResponse);

        return ResponseEntity.ok(
                new RepresentationModel<>()
                        .add(linkTo(HomeController.class).withRel("main-page"))
                        .add(linkTo(BoardController.class).withRel("board-list"))
                        .add(linkTo(MemberController.class).slash(findMember.getId()).withRel("member-info"))
                        .add(Link.of("/docs/index.html#_로그인_성공").withRel(PROFILE))
        );
    }


    @GetMapping("/logout")
    public ResponseEntity logout(HttpServletRequest request){
        HttpSession session = request.getSession(false);

        if(session != null) {
            session.invalidate();
        }

        return ResponseEntity.ok().body(new RepresentationModel<>()
                .add(linkTo(HomeController.class).withRel("main-page"))
                .add(Link.of("/docs/index.html#_로그_아웃").withRel(PROFILE))
        );
    }
}
