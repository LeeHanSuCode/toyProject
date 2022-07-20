package com.toy.toy.interceptor;

import com.toy.toy.StaticVariable;
import com.toy.toy.dto.responseDto.LoginResponse;
import com.toy.toy.entity.Member;
import com.toy.toy.entity.MemberGrade;
import com.toy.toy.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.lang.Nullable;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

import static com.toy.toy.StaticVariable.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LoginCheckInterceptorTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MockMvc mockMvc;

    private MockHttpSession mockHttpSession = new MockHttpSession();

    //세션이 있을 경우는 정상응답들이 되므로 , 세션이 있는 경우는 상태코드만 테스트
    //세션이 없는 경우는 상태코드와 응답 필드 검증


    @BeforeEach
    void setSession(){
        Member joinMember = Member.builder()
                .username("이한수")
                .userId("dhfl0718")
                .memberGrade(MemberGrade.NORMAL)
                .password("asd123!@#")
                .email("dhfl0710@gmail.com")
                .tel("010-1111-1111")
                .build();

        Member saveMember = memberRepository.save(joinMember);

        LoginResponse loginResponse = LoginResponse.builder()
                .userId(saveMember.getUserId())
                .id(saveMember.getId())
                .build();
        mockHttpSession.setAttribute(LOGIN_MEMBER ,loginResponse);
    }


    //memberController 세션이 없을 경우
     private void memberTest_template_noSession(MockHttpServletRequestBuilder method) throws Exception{

        mockMvc.perform(method
                .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$._links.main-page.href").exists())
                .andExpect(jsonPath("$.info").value( "로그인이 필요합니다."))
                .andExpect(jsonPath("$.requestURI").exists())
        ;
    }


    //세션 아이디값 얻어오기
    private Long getSession_memberId(){
        LoginResponse loginResponse = (LoginResponse) mockHttpSession.getAttribute(LOGIN_MEMBER);
        return loginResponse.getId();
    }


    @Test
    @DisplayName("회원 단건 조회 - 세션이 없을 경우")
    void findMember_fail_noSession() throws Exception{
        memberTest_template_noSession(get("/members/{id}",getSession_memberId()));

    }

    @Test
    @DisplayName("회원 수정 - 세션이 없을 경우")
    void updateMember_fail_noSession() throws Exception{
        memberTest_template_noSession(patch("/members/{id}",getSession_memberId()));

    }


    @Test
    @DisplayName("회원 삭제 - 세션이 없을 경우")
    void deleteMember_fail_noSession() throws Exception{
        memberTest_template_noSession(delete("/members/{id}",getSession_memberId()));

    }

}