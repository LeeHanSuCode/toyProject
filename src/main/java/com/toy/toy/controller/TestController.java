package com.toy.toy.controller;


import com.toy.toy.controller.exception_controller.exception.BoardNotFoundException;
import com.toy.toy.controller.exception_controller.exception.CommentNotFoundException;
import com.toy.toy.controller.exception_controller.exception.FilesNotFoundException;
import com.toy.toy.controller.exception_controller.exception.MemberNotFoundException;
import com.toy.toy.dto.JoinMemberDto;
import com.toy.toy.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
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
    public String memberJoin(){
        JoinMemberDto joinMemberDto = JoinMemberDto.builder()
                .userId("dlsdn857758")
                .username("이한수")
                .ssn("001101-1195121")
                .password("please123@@")
                .password2("please123@@")
                .email("dlsdn857758@gmail.com")
                .tel("01073633380")
                .build();


       return String.valueOf(memberService.join(joinMemberDto));
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
