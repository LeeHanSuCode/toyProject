package com.toy.toy.controller;


import com.toy.toy.controller.exception_controller.exception.*;
import com.toy.toy.dto.validationDto.JoinMemberDto;
import com.toy.toy.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
@Slf4j
public class TestController {

    private final MemberService memberService;

    @GetMapping("/member/{itemId}")
    public String member(@PathVariable String itemId){
        if(itemId.equals("ex")){
            throw new MemberNotFoundException("존재하지 않는 회원입니다.");
        }
        return "ok";
    }

    @PostMapping("/member/join")
    public void memberJoin(){
        JoinMemberDto joinMemberDto = JoinMemberDto.builder()
                .userId("dlsdn857758")
                .username("이한수")
                .password("please123@@")
                .password2("please123@@")
                .email("dlsdn857758@gmail.com")
                .tel("01073633380")
                .build();


       //return String.valueOf(memberService.join(joinMemberDto));
    }

    @PostMapping("/member/join2")
    public ResponseEntity memberJoin2(@Valid @RequestBody JoinMemberDto joinMemberDto , BindingResult bindingResult){

    /*    if(!joinMemberDto.getPassword().equals(joinMemberDto.getPassword2())){
                bindingResult.rejectValue("password","NotEquals","비밀번호가 일치하지 않습니다");
        }

        if(bindingResult.hasErrors()){
            throw new ValidationNotFieldMatchedException(bindingResult);
        }*/

        log.info("호출되??333");
        return ResponseEntity.ok().build();
    }

    @GetMapping("/board/{itemId}")
    public String board(@PathVariable String itemId){
        if(itemId.equals("ex")) {
            throw new BoardNotFoundException("존재하지 않는 게시글 입니다.");
        }
        return "ok";
    }

    @GetMapping("/files/{itemId}")
    public String files(@PathVariable String itemId){
        if(itemId.equals("ex")) {
            throw new FilesNotFoundException("파이링 존재하지 않습니다.");
        }
        return "ok";
    }

    @GetMapping("/comment/{itemId}")
    public String comment(@PathVariable String itemId){
        if(itemId.equals("ex")) {
            throw new CommentNotFoundException("댓글이 존재하지 않습니다.");
        }

        return "ok";
    }
}
