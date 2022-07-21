package com.toy.toy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toy.toy.dto.validationDto.LoginMemberDto;
import com.toy.toy.dto.responseDto.LoginResponse;
import com.toy.toy.entity.Member;
import com.toy.toy.entity.MemberGrade;
import com.toy.toy.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static com.toy.toy.StaticVariable.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriScheme = "https",uriHost = "api.login.com" , uriPort = 443)
@ExtendWith(RestDocumentationExtension.class)
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
                .andDo(document("login-success",
                        requestFields(
                                fieldWithPath("userId").description("회원 아이디"),
                                fieldWithPath("password").description("비밀번호")
                        ),
                        responseFields(
                            fieldWithPath("_links.main-page.href").description("메인 페이지 링크"),
                            fieldWithPath("_links.board-list.href").description("게시글 목록 링크"),
                            fieldWithPath("_links.member-info.href").description("회원 정보 링크"),
                            fieldWithPath("_links.profile.href").description("profile")
                        ),
                    links(
                        linkWithRel(BOARD_LIST).description("게시글 목록 링크"),
                        linkWithRel(MAIN_PAGE).description("메인 페이지 링크"),
                        linkWithRel(MEMBER_INFO).description("회원 정보 링크"),
                            linkWithRel(PROFILE).description("link to profile")
                    )))
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
                .andDo(document("login-fail",
                        requestFields(
                                fieldWithPath("userId").description("회원 아이디"),
                                fieldWithPath("password").description("비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("timestamp").description("발생한 시간"),
                                fieldWithPath("status").description("예외 상태"),
                                fieldWithPath("path").description("요청 uri"),
                                fieldWithPath("fieldErrors.*.messages").description("예외 메세지"),
                                fieldWithPath("fieldErrors.*.fieldName").description("예외 발생 필드"),
                                fieldWithPath("fieldErrors.*.rejectedValue").description("거절된 값"),
                                fieldWithPath("_links.main-page.href").description("메인 페이지 링크"),
                                fieldWithPath("_links.profile.href").description("profile")
                        ),
                        links(
                                linkWithRel(MAIN_PAGE).description("메인 페이지 링크"),
                                linkWithRel(PROFILE).description("link to profile")
                        )
                ))




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
                .andDo(document("logout" ,
                            responseFields(
                                    fieldWithPath("_links.main-page.href").description("메인 페이지 릴ㅇ크")
                            ),
                        links(
                                linkWithRel(MAIN_PAGE).description("메인 페이지 링크")
                        )
                        ))
        ;
    }
}