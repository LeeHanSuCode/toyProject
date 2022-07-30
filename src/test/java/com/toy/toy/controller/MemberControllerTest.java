package com.toy.toy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toy.toy.ControllerTestAnnotation;
import com.toy.toy.StaticVariable;
import com.toy.toy.dto.responseDto.LoginResponse;
import com.toy.toy.dto.validationDto.JoinMemberDto;
import com.toy.toy.dto.validationDto.UpdateMemberDto;
import com.toy.toy.entity.Member;
import com.toy.toy.entity.MemberGrade;
import com.toy.toy.repository.MemberRepository;
import com.toy.toy.service.MemberService;
import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static com.toy.toy.StaticVariable.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@AutoConfigureRestDocs(uriScheme = "https",uriHost = "api.member.com" , uriPort = 443)
@ControllerTestAnnotation
@Slf4j
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberRepository memberRepository;

    private MockHttpSession httpSession = new MockHttpSession();


    private final static String JOIN_EXCEPTION_STATUS = "BAD_REQUEST";
    private final static String JOIN_EXCEPTION_PATH = "uri=/members";


    private Member beforeMember(String userId){
        Member member = Member.builder()
                .username("이한수")
                .userId(userId)
                .password("asd123!@#")
                .email("dhfl0710@naver.com")
                .tel("010-1111-1111")
                .memberGrade(MemberGrade.NORMAL)
                .build();

        return memberRepository.save(member);

    }
     @BeforeEach
     void setLoginResponse_forSession(){
        LoginResponse loginResponse = LoginResponse.builder()
                .id(1L)
                .userId("hslee0000")
                .build();
        httpSession.setAttribute(LOGIN_MEMBER,loginResponse);
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
                .andExpect(jsonPath("$._links.member-update.href").exists())
                .andExpect(jsonPath("$._links.member-delete.href").exists())
                .andExpect(jsonPath("$._links.profile.href").exists())
                .andDo(document("join-member",
                            requestFields(
                                    fieldWithPath("username").description("회원 이름"),
                                    fieldWithPath("userId").description("회원 아이디"),
                                    fieldWithPath("password").description("비밀번호"),
                                    fieldWithPath("password2").description("비밀번호 확인"),
                                    fieldWithPath("email").description("이메일"),
                                    fieldWithPath("tel").description("휴대폰 번호"),
                                    fieldWithPath("isIdCheck").description("아이디 중복 확인 여부")
                            ),
                        responseFields(
                                fieldWithPath("id").description("식별자"),
                                fieldWithPath("userId").description("회원 아이디"),
                                fieldWithPath("email").description("이메일"),
                                fieldWithPath("tel").description("휴대폰 번호"),
                                fieldWithPath("username").description("회원 이름"),
                                fieldWithPath("_links.self.href").description("my self 링크"),
                                fieldWithPath("_links.member-update.href").description("회원 수정 링크"),
                                fieldWithPath("_links.member-delete.href").description("회원 삭제 링크"),
                                fieldWithPath("_links.profile.href").description("profile")
                        ),
                        links(
                            linkWithRel("self").description("link to self"),
                            linkWithRel("member-update").description("회원 수정 링크"),
                            linkWithRel("member-delete").description("회원 삭제 링크"),
                            linkWithRel("profile").description("link to profile")

                        )
                        ))

        ;
    }


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
                .andDo(document("join-member-fail",
                        responseFields(
                                fieldWithPath("timestamp").description("발생한 시간"),
                                fieldWithPath("status").description("예외 상태"),
                                fieldWithPath("path").description("요청 uri"),
                                fieldWithPath("fieldErrors").description("발생한 에외 목록"),
                                fieldWithPath("fieldErrors.*.messages").description("예외 메세지"),
                                fieldWithPath("fieldErrors.*.fieldName").description("예외 발생 필드"),
                                fieldWithPath("fieldErrors.*.rejectedValue").description("거절된 값"),
                                fieldWithPath("_links.main-page.href").description("메인 페이지 링크"),
                                fieldWithPath("_links.profile.href").description("profile")
                        ),
                        links(linkWithRel(MAIN_PAGE).description("link to mainPage"),
                                linkWithRel(PROFILE).description("link to profile"))
                        )
                )


        ;

    }


    @Test
    @DisplayName("회원 한명 조회")
    void findMember_success() throws Exception{
        //given
        Member member = beforeMember("hslee0000");


        //expected
        mockMvc.perform(RestDocumentationRequestBuilders.get("/members/{id}",member.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                                .session(httpSession)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(member.getId()))
                .andExpect(jsonPath("$.userId").value(member.getUserId()))
                .andExpect(jsonPath("$.username").value(member.getUsername()))
                .andExpect(jsonPath("$.email").value(member.getEmail()))
                .andExpect(jsonPath("$.tel").value(member.getTel()))
                .andExpect(jsonPath("$._links.self").exists())
                .andExpect(jsonPath("$._links.member-update").exists())
                .andExpect(jsonPath("$._links.member-delete").exists())
                .andDo(document("find-member",
                        pathParameters(
                                parameterWithName("id").description("회원 식별자")
                        ),
                        responseFields(
                                fieldWithPath("id").description("식별자"),
                                fieldWithPath("userId").description("회원 아이디"),
                                fieldWithPath("email").description("이메일"),
                                fieldWithPath("tel").description("휴대폰 번호"),
                                fieldWithPath("username").description("회원 이름"),
                                fieldWithPath("_links.self.href").description("my self 링크"),
                                fieldWithPath("_links.member-update.href").description("회원 수정 링크"),
                                fieldWithPath("_links.member-delete.href").description("회원 삭제 링크"),
                                fieldWithPath("_links.profile.href").description("profile")
                        ),
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("member-update").description("회원 수정 링크"),
                                linkWithRel("member-delete").description("회원 삭제 링크"),
                                linkWithRel("profile").description("link to profile")

                        )
                ))
        ;

    }

    @Test
    @DisplayName("회원 한명 조회 실패 - 존재하지 않는 식별자 이용")
    void findMember_fail_notValid_id() throws Exception{
        //given
        Long id = 100L;


        //expected
        mockMvc.perform(RestDocumentationRequestBuilders.get("/members/{id}",id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .session(httpSession)
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.code").value("MemberNotFound"))
                .andExpect(jsonPath("$.path").value("uri=/members/100"))
                .andExpect(jsonPath("$.message").value("존재하지 않는 회원입니다."))
                .andExpect(jsonPath("$._links.main-page.href").value("http://www.localhost:8080"))
                .andDo(document( "find-resource-fail-notExist",
                        pathParameters(
                                parameterWithName("id").description("회원 식별자")
                        ),
                        responseFields(
                                fieldWithPath("timestamp").description("발생한 시간"),
                                fieldWithPath("code").description("예외 상태"),
                                fieldWithPath("path").description("요청 uri"),
                                fieldWithPath("message").description("예외 메세지"),
                                fieldWithPath("_links.main-page.href").description("메인 페이지 링크"),
                                fieldWithPath("_links.profile.href").description("profile")
                        ),
                        links(linkWithRel("main-page").description("link to mainPage"),
                                linkWithRel("profile").description("link to profile"))
                        ))
                ;
    }


    @Test
    @DisplayName("회원 수정 성공 케이스")
    void updateMember_success() throws Exception{
        //given
        Member member = beforeMember("hslee0000");

        UpdateMemberDto updateMemberDto = UpdateMemberDto.builder()
                .username("한수2")
                .password("wmf12312@@")
                .password2("wmf12312@@")
                .email("dlsdn857758@gmail.com")
                .tel("010-2222-2222")
                .build();

        //expected
        mockMvc.perform(RestDocumentationRequestBuilders.patch("/members/{id}",member.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(updateMemberDto))
                        .session(httpSession)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(member.getId()))
                .andExpect(jsonPath("$.userId").value(member.getUserId()))
                .andExpect(jsonPath("$.username").value(updateMemberDto.getUsername()))
                .andExpect(jsonPath("$.email").value(updateMemberDto.getEmail()))
                .andExpect(jsonPath("$.tel").value(updateMemberDto.getTel()))
                .andExpect(jsonPath("$._links.member-info").exists())
                .andDo(document("update-member",
                        pathParameters(
                                parameterWithName("id").description("회원 식별자")
                        ),
                        requestFields(
                                fieldWithPath("username").description("회원 이름"),
                                fieldWithPath("password").description("비밀번호"),
                                fieldWithPath("password2").description("비밀번호 확인"),
                                fieldWithPath("email").description("이메일"),
                                fieldWithPath("tel").description("휴대폰 번호")
                        ),
                        responseFields(
                                fieldWithPath("id").description("식별자"),
                                fieldWithPath("userId").description("회원 아이디"),
                                fieldWithPath("email").description("이메일"),
                                fieldWithPath("tel").description("휴대폰 번호"),
                                fieldWithPath("username").description("회원 이름"),
                                fieldWithPath("_links.member-info.href").description("회원 상세 보기 링크"),
                                fieldWithPath("_links.main-page.href").description("메인 페이지 링크"),
                                fieldWithPath("_links.profile.href").description("profile")
                        ),
                        links(
                                linkWithRel("member-info").description("회원 상세 보기 링크"),
                                linkWithRel("main-page").description("link to mainPage"),
                                linkWithRel("profile").description("link to profile")
                        )
                        ))
        ;


    }

    @Test
    @DisplayName("회원 수정 성공 케이스 - null을 넘기고 아무것도 변경되지 않은 경우.")
    void updateMember_nullData_success() throws Exception{
        //given
        Member member = beforeMember("hslee0000");

        UpdateMemberDto updateMemberDto = UpdateMemberDto.builder()
                .username(null)
                .password(null)
                .password2(null)
                .email(null)
                .tel(null)
                .build();

        //expected
        mockMvc.perform(patch("/members/{id}",member.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(updateMemberDto))
                        .session(httpSession)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(member.getId()))
                .andExpect(jsonPath("$.userId").value(member.getUserId()))
                .andExpect(jsonPath("$.username").value(member.getUsername()))
                .andExpect(jsonPath("$.email").value(member.getEmail()))
                .andExpect(jsonPath("$.tel").value(member.getTel()))
                .andExpect(jsonPath("$._links.member-info").exists())
        ;
    }


    @Test
    @DisplayName("회원 수정 실패 케이스 - BeanValidation을 어겼을 경우.")
    void updateMember_blankData_success() throws Exception{
        //given
        Member member = beforeMember("hslee0000");


        UpdateMemberDto updateMemberDto = UpdateMemberDto.builder()
                .username("이")
                .password("asdf12")
                .password2("asdf123")
                .email("hslee0710naver.com")
                .tel("010010101011")
                .build();

        //expected
        mockMvc.perform(patch("/members/{id}",member.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(updateMemberDto))
                        .session(httpSession)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.password.messages.length()").value(3))
                .andExpect(jsonPath("$.fieldErrors.username.messages.length()").value(1))
                .andExpect(jsonPath("$.fieldErrors.email.messages.length()").value(1))
                .andExpect(jsonPath("$.fieldErrors.tel.messages.length()").value(1))
                .andDo(document("update-member-fail",
                responseFields(
                        fieldWithPath("timestamp").description("발생한 시간"),
                        fieldWithPath("status").description("예외 상태"),
                        fieldWithPath("path").description("요청 uri"),
                        fieldWithPath("fieldErrors").description("발생한 에외 목록"),
                        fieldWithPath("fieldErrors.*.messages").description("예외 메세지"),
                        fieldWithPath("fieldErrors.*.fieldName").description("예외 발생 필드"),
                        fieldWithPath("fieldErrors.*.rejectedValue").description("거절된 값"),
                        fieldWithPath("_links.main-page.href").description("메인 페이지 링크"),
                        fieldWithPath("_links.profile.href").description("profile")
                ),
                links(linkWithRel("main-page").description("link to mainPage"),
                        linkWithRel("profile").description("link to profile"))
        ))
        ;
    }



    @Test
    @DisplayName("회원 삭제")
    void deleteMember() throws Exception{
        //given
        Member member = beforeMember("hslee0000");

        //expected
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/members/{id}",member.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                                .session(httpSession)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.main-page").exists())
                .andDo(document("delete-member",
                            pathParameters(
                                    parameterWithName("id").description("회원 식별자")
                            ),
                            responseFields(
                                    fieldWithPath("_links.main-page.href").description("메인 페이지 링크"),
                                    fieldWithPath("_links.profile.href").description("profile")
                            ),
                            links(
                                    linkWithRel("main-page").description("link to mainPage"),
                                    linkWithRel("profile").description("link to profile")
                            )
                        ))
        ;

    }
}