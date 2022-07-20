package com.toy.toy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toy.toy.StaticVariable;
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
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Slf4j
class HomeControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;
    private MockHttpSession mockHttpSession = new MockHttpSession();

    @BeforeEach
    void setMemberWithSession(){
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

        mockHttpSession.setAttribute(StaticVariable.LOGIN_MEMBER,loginResponse);
    }

    @Test
    @DisplayName("main_page 화면으로 이동 - session이 있을 경우")
    void home_with_session() throws Exception{

        //expected
        mockMvc.perform(get("/")
                .session(mockHttpSession)
                .accept(MediaTypes.HAL_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.board-list.href").exists())
                .andExpect(jsonPath("$._links.member-info.href").exists())
        ;
    }


    @Test
    @DisplayName("main_page 화면으로 이동 - session이 없을 경우")
    void home_without_session() throws Exception{

        //expected
        mockMvc.perform(get("/")
                        .accept(MediaTypes.HAL_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.board-list.href").exists());
    }

}