package com.toy.toy.service;
import com.toy.toy.controller.exception_controller.exception.MemberNotFoundException;
import com.toy.toy.dto.validationDto.JoinMemberDto;
import com.toy.toy.dto.validationDto.UpdateMemberDto;
import com.toy.toy.entity.Comment;
import com.toy.toy.entity.Member;
import com.toy.toy.entity.MemberGrade;
import com.toy.toy.repository.CommentRepository;
import com.toy.toy.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import javax.swing.text.html.Option;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;



    private Member.MemberBuilder createMember(Long id){
        return Member.builder()
                .id(id).userId("hslee0710")
                .username("이한수")
                .password("wmf123!@#")
                .email("hslee0710@naver.com")
                .tel("010-1111-1111");
    }

    @Test
    @DisplayName("회원 가입")
    void joinMember(){
        //given
        Member member = createMember(1L)
                .memberGrade(MemberGrade.NORMAL)
                .build();

        when(memberRepository.save(member)).thenReturn(member);

        //when
        Member joinMember = memberService.join(member);

        //then
        assertThat(joinMember.getId()).isEqualTo(member.getId());
        assertThat(joinMember.getMemberGrade()).isEqualTo(MemberGrade.NORMAL);
        assertThat(joinMember.getUserId()).isEqualTo(member.getUserId());

        verify(memberRepository,times(1)).save(member);

    }


    @Test
    @DisplayName("회원 단건 조회 성공")
    void findMember_success(){
        //given
       Member member = createMember(1L).build();

        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));

        //when
        Member findMember = memberService.findById(1L);

        //then
        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUserId()).isEqualTo(member.getUserId());
        assertThat(findMember).isEqualTo(member);

        verify(memberRepository,times(1)).findById(member.getId());

    }

    @Test
    @DisplayName("존재 하지 않는 회원 조회할 경우")
    void findMember_fail(){
        //given
        when(memberRepository.findById(100L)).thenReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> memberService.findById(100L))
                .isInstanceOf(MemberNotFoundException.class)
                .hasMessageContaining("존재하지 않는 회원입니다.");
        //verify
        verify(memberRepository,times(1)).findById(100L);
    }



    @Test
    @DisplayName("회원 수정 성공 케이스")
    void updateMember_success(){
        //given
        Member member = createMember(1L).build()
                ;
        UpdateMemberDto updateMemberDto = UpdateMemberDto.builder()
                .username("삼한수")
                .password("zxcv123!@#")
                .password2("zxcv123!@#")
                .email("dhfl0710@hanmail.net")
                .tel("010-4545-4545")
                .build();
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));

        //when
        memberService.update(updateMemberDto,member.getId());

        //then
        assertThat(updateMemberDto.getUsername()).isEqualTo(member.getUsername());
        assertThat(updateMemberDto.getPassword()).isEqualTo(member.getPassword());
        assertThat(updateMemberDto.getEmail()).isEqualTo(member.getEmail());
        assertThat(updateMemberDto.getTel()).isEqualTo(member.getTel());

        //verify
        verify(memberRepository ,times(1)).findById(member.getId());
    }

    @Test
    @DisplayName("회원 수정 실패 케이스 - 존재하지 않는 회원")
    void updateMember_fail(){
        //given
        when(memberRepository.findById(1L)).thenReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> memberService.findById(1L))
                .isInstanceOf(MemberNotFoundException.class)
                .hasMessageContaining("존재하지 않는 회원입니다.");
        //verify
        verify(memberRepository,times(1)).findById(1L);
    }


    @Test
    @DisplayName("회원 삭제 성공 케이스")
    void deleteMember_success(){
        //given
        //나중에 댓글 부분과 파일부분도 완성한 뒤에 ,
        //여기와서 해당 메소드들이 호출되는 정도만 테스트 해보자.

        Member member = createMember(1L).build();

        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
        doNothing().when(memberRepository).delete(member);

        //when
        memberService.delete(member.getId());

        //then
        verify(memberRepository,times(1)).findById(member.getId());
        verify(memberRepository,times(1)).delete(member);
    }

    @Test
    @DisplayName("회원 삭제 실패 케이스 - 존재하지 않는 회원")
    void deleteMember_fail(){
        //given
        Long id = 100L;
        when(memberRepository.findById(id)).thenReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> memberService.findById(id))
                .isInstanceOf(MemberNotFoundException.class)
                .hasMessageContaining("존재하지 않는 회원입니다.");

        //verify
        verify(memberRepository,times(1)).findById(id);
    }
}