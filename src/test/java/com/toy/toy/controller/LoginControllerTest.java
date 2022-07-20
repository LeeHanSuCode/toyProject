package com.toy.toy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toy.toy.StaticVariable;
import com.toy.toy.dto.LoginMemberDto;
import com.toy.toy.dto.responseDto.LoginResponse;
import com.toy.toy.entity.Member;
import com.toy.toy.entity.MemberGrade;
import com.toy.toy.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.toy.toy.StaticVariable.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
@Transactional
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberRepository memberRepository;

    private MockHttpSession session = new MockHttpSession();

    private Long memberId;


    @BeforeEach
    void setMember(){
        Member joinMember = Member.builder()
                .username("이한수")
                .userId("dhfl0718")
                .memberGrade(MemberGrade.NORMAL)
                .password("asd123!@#")
                .email("dhfl0710@gmail.com")
                .tel("010-1111-1111")
                .build();

        Member save = memberRepository.save(joinMember);
        this.memberId = save.getId();
    }

    @Test
    @DisplayName("로그인 성공")
    void login_success() throws Exception{
        //given
        String userId = "dhfl0718";
        String password = "asd123!@#";

        LoginMemberDto loginMemberDto = LoginMemberDto.builder()
                .userId(userId)
                .password(password)
                .build();

        //expected
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(loginMemberDto))
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.main-page.href").exists())
                .andExpect(jsonPath("$._links.board-list.href").exists())
                .andExpect(jsonPath("$._links.member-info.href").exists())
        ;

    }


    @Test
    @DisplayName("로그인 실패 - Beanvalidation 위반")
    void login_fail_BeanValidation() throws Exception{
        //given

        LoginMemberDto loginMemberDto = LoginMemberDto.builder()
                .build();


        //expected
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(loginMemberDto))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.path").value("uri=/login"))
                .andExpect(jsonPath("$._links.main-page.href").exists())
                .andExpect(jsonPath("$.fieldErrors.length()").value(2))
                .andExpect(jsonPath("$.fieldErrors.password.fieldName").value("password"))
                .andExpect(jsonPath("$.fieldErrors.userId.fieldName").value("userId"))
                .andExpect(jsonPath("$.fieldErrors.password.rejectedValue").value("값이 들어오지 않음"))
                .andExpect(jsonPath("$.fieldErrors.userId.rejectedValue").value("값이 들어오지 않음"))
                .andExpect(jsonPath("$.fieldErrors.password.messages[0]").value("password은 필수값 입니다."))
                .andExpect(jsonPath("$.fieldErrors.userId.messages[0]").value("userId은 필수값 입니다."))

        ;

    }

    @Test
    @DisplayName("로그인 실패 - 데이터는 보냈으나 , 아이디가 존재하지 않을 경우 ")
    void login_fail_notExistUserId() throws Exception{
        //given
        String userId = "dhfl58589";
        String password = "asd123!@#";

        LoginMemberDto loginMemberDto = LoginMemberDto.builder()
                .userId(userId)
                .password(password)
                .build();


        //expected
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(loginMemberDto))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.path").value("uri=/login"))
                .andExpect(jsonPath("$._links.main-page.href").exists())
                .andExpect(jsonPath("$.fieldErrors.length()").value(1))
                .andExpect(jsonPath("$.fieldErrors.userId.messages[0]").value("존재하지 않는 회원 입니다."))
                .andExpect(jsonPath("$.fieldErrors.userId.fieldName").value("userId"))
                .andExpect(jsonPath("$.fieldErrors.userId.rejectedValue").value(userId))




        ;

    }

    @Test
    @DisplayName("로그인 실패 - 데이터는 보냈으나 , 아이디는 존재하나 비밀번호가 틀렸을 경우 ")
    void login_fail_notEqualsPassword() throws Exception{
        //given
        //given
        String userId = "dhfl0718";
        String password = "asd123!@#@@";

        LoginMemberDto loginMemberDto = LoginMemberDto.builder()
                .userId(userId)
                .password(password)
                .build();


        //expected
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(loginMemberDto))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.path").value("uri=/login"))
                .andExpect(jsonPath("$._links.main-page.href").exists())
                .andExpect(jsonPath("$.fieldErrors.length()").value(1))
                .andExpect(jsonPath("$.fieldErrors.password.messages[0]").value("비밀번호가 일치하지 않습니다."))
                .andExpect(jsonPath("$.fieldErrors.password.fieldName").value("password"))
                .andExpect(jsonPath("$.fieldErrors.password.rejectedValue").value(password))

        ;
    }


    @Test
    @DisplayName("로그 아웃")
    void logout_success() throws Exception{
        //given
        String userId = "dhfl0718";
        String password = "asd123!@#";

        LoginResponse loginResponse = LoginResponse.builder()
                .userId(userId)
                .id(memberId)
                .build();

        session.setAttribute(LOGIN_MEMBER,loginResponse);
        LoginResponse loginResponseSession = (LoginResponse)session.getAttribute(LOGIN_MEMBER);

        //expected
        mockMvc.perform(get("/login/logout")
                        .session(session)
                        .accept(MediaTypes.HAL_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.main-page.href").exists())
        ;
    }
}