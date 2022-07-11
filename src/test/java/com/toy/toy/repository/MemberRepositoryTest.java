package com.toy.toy.repository;

import com.toy.toy.controller.exception_controller.exception.MemberNotFoundException;
import com.toy.toy.entity.Member;
import com.toy.toy.entity.MemberGrade;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)

@DataJpaTest            //@Transactional 을 포함하고 있다.
@Slf4j
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @PersistenceContext
    private EntityManager em;

    private Long findMemberId;


    @BeforeEach
    void memberSetting(){

            Member member1 = Member.builder()
                    .username("이한수1" )
                    .userId("dlsdn8577581")
                    .password("please123@@1")
                    .email("dlsdn857751@gmail.com")
                    .tel("01073633381")
                    .memberGrade(MemberGrade.NORMAL)
                    .build();

        Member member2 = Member.builder()
                .username("이한수2" )
                .userId("dlsdn8577582")
                .password("please123@@2")
                .email("dlsdn857752@gmail.com")
                .tel("01073633382")
                .memberGrade(MemberGrade.NORMAL)
                .build();

            memberRepository.save(member1);
            memberRepository.save(member2);

            //조회 , 수정 , 삭제등에서 조회할 회원 id 하나 지정.
            findMemberId = member1.getId();

        em.flush();
        em.clear();
    }

    @DisplayName("회원가입 성공 후 조회하여 필드 비교")
    @Test
    void memberJoinTest(){
        //given
        Member joinMember = member();

        //when
        memberRepository.save(joinMember);

        em.flush();
        em.clear();

        Member saveMember = memberRepository.findById(joinMember.getId()).get();
        //then
        assertThat(saveMember.getId()).isEqualTo(joinMember.getId());
        assertThat(saveMember.getUserId()).isEqualTo(joinMember.getUserId());
        assertThat(saveMember.getUsername()).isEqualTo(joinMember.getUsername());
        assertThat(saveMember.getPassword()).isEqualTo(joinMember.getPassword());
        assertThat(saveMember.getEmail()).isEqualTo(joinMember.getEmail());
        assertThat(saveMember.getTel()).isEqualTo(joinMember.getTel());
        assertThat(saveMember.getMemberGrade()).isEqualTo(joinMember.getMemberGrade());

    }


    private Member member() {
       return Member.builder()
                .username("이한수")
                .userId("dlsdn857758")
                .password("please123@@")
                .email("dlsdn857758@gmail.com")
                .tel("01073633380")
                .memberGrade(MemberGrade.NORMAL)
                .build();
    }


    @DisplayName("회원가입 시에 Auditing 날짜 확인")
    @Test
    void memberSaveDateAuditing(){
        //given
        Member member = member();

        //when
        Member saveMember = memberRepository.save(member);

        //then
        assertThat(saveMember.getCreatedDate()).isNotNull();
        assertThat(saveMember.getUpdatedDate()).isNotNull();
    }


    @DisplayName("회원 전체 조회 ")
    @Test
    void findAll_memberCount(){

        //when
        List<Member> findMemberList = memberRepository.findAll();

        //then
        assertThat(findMemberList.size()).isEqualTo(2);
    }



    @DisplayName("회원 조회 - 페이지")
    @Test
    void findAll_memberCount_perPage(){

    }


    @DisplayName("회원 조회 - 단건 조회 없음(예외)")
    @Test
    void findMemberById_fail(){
        //when
        long findId = 100L;
        Optional<Member> findMember = memberRepository.findById(findId);

        //then
        assertThrows(NoSuchElementException.class , ()->findMember.get());
    }


    @DisplayName("회원 수정(이름,비밀번호,이메일,전화번호)  -> 비밀번호만 변경 x")
    @Test
    void updateMember(){
        //given
        Member findMember = memberRepository.findById(findMemberId).get();
        String existPw = findMember.getPassword();

        //when

        String updateUsername = "김종국";
        String updateEmail = "hslee0000@hanmail.net";
        String updateTel = "01084456513";

        findMember.updateMember(updateUsername,existPw,updateEmail,updateTel);

        em.flush();
        em.clear();

        Member reFindMember = memberRepository.findById(findMember.getId()).get();

        //then
        assertThat(reFindMember.getUsername()).isEqualTo(updateUsername);
        assertThat(reFindMember.getPassword()).isEqualTo(existPw);
        assertThat(reFindMember.getEmail()).isEqualTo(updateEmail);
        assertThat(reFindMember.getTel()).isEqualTo(updateTel);
    }


    @DisplayName("회원 삭제")
    @Test
    void deleteMember(){
        //given
        List<Member> findMembers = memberRepository.findAll();
        int existSize = findMembers.size();

        //when
        memberRepository.delete(findMembers.get(0));
        em.flush();
        em.clear();

        //then
       assertThat(memberRepository.findAll().size())
               .isNotSameAs(existSize);
    }
}