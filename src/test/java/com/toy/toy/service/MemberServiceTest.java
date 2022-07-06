package com.toy.toy.service;
import com.toy.toy.dto.validationDto.JoinMemberDto;
import com.toy.toy.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
class MemberServiceTest {

    private final BoardService boardService;
    private final CommentRepository commentRepository;
    private final MemberService memberService;




    private JoinMemberDto joinMemberDto(){
        return JoinMemberDto.builder()
                .username("이한수")
                .userId("dlsdn857758")
                .ssn("001101-1195121")
                .password("please123@@")
                .password2("please123@@")
                .email("dlsdn857758@gmail.com")
                .tel("01073633380")
                .build();
    }
}