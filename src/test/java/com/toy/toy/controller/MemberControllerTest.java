package com.toy.toy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toy.toy.dto.validationDto.JoinMemberDto;
import com.toy.toy.dto.validationDto.UpdateMemberDto;
import com.toy.toy.entity.Member;
import com.toy.toy.entity.MemberGrade;
import com.toy.toy.repository.MemberRepository;
import com.toy.toy.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberRepository memberRepository;

    @PersistenceContext
    private EntityManager em;

    private final static String JOIN_EXCEPTION_STATUS = "BAD_REQUEST";
    private final static String JOIN_EXCEPTION_PATH = "uri=/members";


    private Member beforeMember(){
        return Member.builder()
                .username("이한수")
                .userId("hslee0710")
                .password("asd123!@#")
                .email("dhfl0710@naver.com")
                .tel("010-1111-1111")
                .memberGrade(MemberGrade.NORMAL)
                .build();
    }

    @Test
    @DisplayName("회원 가입 성공 케이스")
    void joinMember_success() throws Exception{
        //given
        JoinMemberDto joinMemberDto = JoinMemberDto.builder()
                .username("이한수")
                .userId("dlsdn857758")
                .password("asd123@@")
                .password2("asd123@@")
                .email("dlsdn857758@naver.com")
                .tel("010-1111-1111")
                .isIdCheck(true)
                .build();

        //expected
        mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(joinMemberDto))
                )
                .andDo(print())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE,MediaTypes.HAL_JSON_VALUE))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value(joinMemberDto.getUsername()))
                .andExpect(jsonPath("$.userId").value(joinMemberDto.getUserId()))
                .andExpect(jsonPath("$.email").value(joinMemberDto.getEmail()))
                .andExpect(jsonPath("$.tel").value(joinMemberDto.getTel()))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.update-member.href").exists())
                .andExpect(jsonPath("$._links.delete-member.href").exists())

        ;
    }


    //실패케이스는 예외별로 구성하자. -> 타입변환실패 , 유효성 검증 실패
    @Test
    @DisplayName("회원 가입 실패 케이스 - 모두 null 데이터를 넘길 경우.")
    void joinMember_fail_beanValidation_null() throws Exception{
        //given
        JoinMemberDto joinMemberDto = JoinMemberDto.builder()
                .username(null)
                .userId(null)
                .password(null)
                .password2(null)
                .email(null)
                .tel(null)
                .isIdCheck(null)
                .build();

        //expected
        mockMvc.perform(post("/members")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(joinMemberDto))
        )
                .andDo(print())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(JOIN_EXCEPTION_STATUS))
                .andExpect(jsonPath("$.path").value(JOIN_EXCEPTION_PATH))
                .andExpect(jsonPath("$.fieldErrors.password.messages[0]").value("password은 필수값 입니다."))
                .andExpect(jsonPath("$.fieldErrors.userId.messages[0]").value("userId은 필수값 입니다."))
                .andExpect(jsonPath("$.fieldErrors.email.messages[0]").value("email은 필수값 입니다."))
                .andExpect(jsonPath("$.fieldErrors.tel.messages[0]").value("tel은 필수값 입니다."))
                .andExpect(jsonPath("$.fieldErrors.username.messages[0]").value("username은 필수값 입니다."))
                .andExpect(jsonPath("$.fieldErrors.isIdCheck.messages[0]").value("아이디는 중복확인이 필수 입니다."))
                .andExpect(jsonPath("$.fieldErrors.password.messages.length()").value(1))
                .andExpect(jsonPath("$.fieldErrors.userId.messages.length()").value(1))
                .andExpect(jsonPath("$.fieldErrors.email.messages.length()").value(1))
                .andExpect(jsonPath("$.fieldErrors.tel.messages.length()").value(1))
                .andExpect(jsonPath("$.fieldErrors.username.messages.length()").value(1))
                .andExpect(jsonPath("$.fieldErrors.isIdCheck.messages.length()").value(1))

        ;

    }
    @Test
    @DisplayName("회원 가입 실패 케이스 - 비어있는 데이터를 넘겼을 경우")
    void joinMember_fail_beanValidation_blankData() throws Exception{
        //given
        JoinMemberDto joinMemberDto = JoinMemberDto.builder()
                .username("")
                .userId("")
                .password("")
                .password2("")
                .email("")
                .tel("")
                .build();

        //expected
        mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(joinMemberDto))
                )
                .andDo(print())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(JOIN_EXCEPTION_STATUS))
                .andExpect(jsonPath("$.path").value(JOIN_EXCEPTION_PATH))
                .andExpect(jsonPath("$.fieldErrors.password.messages.length()").value(3))
                .andExpect(jsonPath("$.fieldErrors.userId.messages.length()").value(2))
                .andExpect(jsonPath("$.fieldErrors.email.messages.length()").value(2))
                .andExpect(jsonPath("$.fieldErrors.tel.messages.length()").value(2))
                .andExpect(jsonPath("$.fieldErrors.username.messages.length()").value(2))
                .andExpect(jsonPath("$.fieldErrors.isIdCheck.messages.length()").value(1))


        ;

    }

    @Test
    @DisplayName("회원 가입 실패 케이스 - 잘못된 데이터를 넘겼을 경우")
    void joinMember_fail_beanValidation_wrongData() throws Exception{
        //given
        JoinMemberDto joinMemberDto = JoinMemberDto.builder()
                .username("이")
                .userId("hslee")
                .password("abc4842")
                .password2("abc4842")
                .email("abc4842")
                .tel("abc4842")
                .isIdCheck(false)
                .build();

        //expected
        mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(joinMemberDto))
                )
                .andDo(print())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(JOIN_EXCEPTION_STATUS))
                .andExpect(jsonPath("$.path").value(JOIN_EXCEPTION_PATH))
                .andExpect(jsonPath("$.fieldErrors.userId.messages[0]").value("userId은 8 ~ 20글자 사이로 입력해 주세요."))
                .andExpect(jsonPath("$.fieldErrors.email.messages[0]").value("이메일 형식이 올바르지 않습니다."))
                .andExpect(jsonPath("$.fieldErrors.tel.messages[0]").value("전화번호가 올바르지 않습니다."))
                .andExpect(jsonPath("$.fieldErrors.username.messages[0]").value("username은 2 ~ 4글자 사이로 입력해 주세요."))
                .andExpect(jsonPath("$.fieldErrors.password.messages.length()").value(2))
                .andExpect(jsonPath("$.fieldErrors.userId.messages.length()").value(1))
                .andExpect(jsonPath("$.fieldErrors.email.messages.length()").value(1))
                .andExpect(jsonPath("$.fieldErrors.tel.messages.length()").value(1))
                .andExpect(jsonPath("$.fieldErrors.username.messages.length()").value(1))
                .andExpect(jsonPath("$.fieldErrors.isIdCheck.messages.length()").value(1))


        ;

    }


    @Test
    @DisplayName("글 1개 조회")
    void findMember_success() throws Exception{
        //given
        Member member = beforeMember();
        memberRepository.save(member);


        //expected
        mockMvc.perform(get("/members/{id}",member.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(member.getId()))
                .andExpect(jsonPath("$.userId").value(member.getUserId()))
                .andExpect(jsonPath("$.username").value(member.getUsername()))
                .andExpect(jsonPath("$.email").value(member.getEmail()))
                .andExpect(jsonPath("$.tel").value(member.getTel()))
                .andExpect(jsonPath("$._links.self").exists())
                .andExpect(jsonPath("$._links.update-member").exists())
                .andExpect(jsonPath("$._links.delete-member").exists())
        ;

    }

/*

    @Test
    @DisplayName("회원 수정 성공 케이스 - 전부 변경한 경우")
    void updateMember_success() throws Exception{
        //given
        Member member = beforeMember();
        memberRepository.save(member);

        UpdateMemberDto updateMemberDto = UpdateMemberDto.builder()
                .username("한수2")
                .password("wmf12312@@")
                .password2("wmf12312@@")
                .email("dlsdn857758@gmail.com")
                .tel("010-2222-2222")
                .build();

        Member findMember = memberRepository.findById(member.getId()).get();

        //expected
        mockMvc.perform(patch("/members/{id}",member.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(updateMemberDto))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(updateMemberDto.getUsername()))



        ;


    }

*/



    @Test
    @DisplayName("회원 삭제")
    void deleteMember() throws Exception{
        //given
        Member member = beforeMember();
        memberRepository.save(member);

        //expected
        mockMvc.perform(delete("/members/{id}",member.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.greeting").value("Thank you for using it so far"))
                .andExpect(jsonPath("$._links.main-page").exists());

    }
}