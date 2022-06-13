package com.toy.toy.controller;


import com.toy.toy.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

   /* //회원 가입 화면 이동
    @GetMapping
    public String joinForm(){

    }


    //회원 가입 로직처리
    @PostMapping
    public String join(@Valid JoinMemberDto joinMemberDto){

    }


    //회원 정보 조회
    @GetMapping("/{id}")
    public String info(@PathVariable Long id , Model model){

    }


    //회원 정보 수정
    @PatchMapping("/{id}")
    public*/
}
