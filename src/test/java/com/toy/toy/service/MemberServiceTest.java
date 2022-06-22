package com.toy.toy.service;

import com.toy.toy.dto.JoinMemberDto;
import com.toy.toy.entity.Member;
import com.toy.toy.repository.BoardRepository;
import com.toy.toy.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)     //Junit과 통합하여 사용하기 위한 어노테이션(Mock
@Slf4j
@Rollback
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private BoardService boardService;
    @Mock
    private BoardRepository boardRepository;
    @Mock
    private MemberRepository memberRepository;

    @Test
    @DisplayName("회원가입 테스트")
    void join(){
        //given
        JoinMemberDto joinMember = joinMemberDto();

         doReturn(Member.builder()
                .username(joinMember.getUsername())
                .userId(joinMember.getUserId())
                .password(joinMember.getPassword())
                .ssn(joinMember.getSsn())
                .email(joinMember.getEmail())
                .tel(joinMember.getTel())
                .build())
                .when(memberRepository).save(any(Member.class));

     /*   log.info("saveMember.class={}" , saveMember.getClass());
        log.info("saveId={}" , saveMember.getId());
        log.info("username={}" , saveMember.getUsername());

        //when
        Long saveId = memberService.join(joinMember);

        //then
        assertThat(saveMember.getId()).isEqualTo(saveId);*/

    }

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