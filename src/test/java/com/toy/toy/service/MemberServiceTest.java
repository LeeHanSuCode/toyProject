package com.toy.toy.service;
import com.toy.toy.dto.JoinMemberDto;
import com.toy.toy.entity.Member;
import com.toy.toy.entity.MemberGrade;
import com.toy.toy.repository.CommentRepository;
import com.toy.toy.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import static org.mockito.Mockito.*;

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