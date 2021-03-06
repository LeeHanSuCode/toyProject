package com.toy.toy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
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

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriScheme = "https",uriHost = "api.member.com" , uriPort = 443)
@ExtendWith(RestDocumentationExtension.class)
@Transactional
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
                .username("?????????")
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
    @DisplayName("?????? ?????? ?????? ?????????")
    void joinMember_success() throws Exception{
        //given
        JoinMemberDto joinMemberDto = JoinMemberDto.builder()
                .username("?????????")
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
                .andDo(document("join-member",
                            requestFields(
                                    fieldWithPath("username").description("?????? ??????"),
                                    fieldWithPath("userId").description("?????? ?????????"),
                                    fieldWithPath("password").description("????????????"),
                                    fieldWithPath("password2").description("???????????? ??????"),
                                    fieldWithPath("email").description("?????????"),
                                    fieldWithPath("tel").description("????????? ??????"),
                                    fieldWithPath("isIdCheck").description("????????? ?????? ?????? ??????")
                            ),
                        responseFields(
                                fieldWithPath("id").description("?????????"),
                                fieldWithPath("userId").description("?????? ?????????"),
                                fieldWithPath("email").description("?????????"),
                                fieldWithPath("tel").description("????????? ??????"),
                                fieldWithPath("username").description("?????? ??????"),
                                fieldWithPath("_links.self.href").description("my self ??????"),
                                fieldWithPath("_links.member-update.href").description("?????? ?????? ??????"),
                                fieldWithPath("_links.member-delete.href").description("?????? ?????? ??????"),
                                fieldWithPath("_links.profile.href").description("profile")
                        ),
                        links(
                            linkWithRel("self").description("link to self"),
                            linkWithRel("member-update").description("?????? ?????? ??????"),
                            linkWithRel("member-delete").description("?????? ?????? ??????"),
                            linkWithRel("profile").description("link to profile")

                        )
                        ))

        ;
    }


    @Test
    @DisplayName("?????? ?????? ?????? ????????? - ?????? null ???????????? ?????? ??????.")
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
                .andExpect(jsonPath("$.fieldErrors.password.messages[0]").value("password??? ????????? ?????????."))
                .andExpect(jsonPath("$.fieldErrors.userId.messages[0]").value("userId??? ????????? ?????????."))
                .andExpect(jsonPath("$.fieldErrors.email.messages[0]").value("email??? ????????? ?????????."))
                .andExpect(jsonPath("$.fieldErrors.tel.messages[0]").value("tel??? ????????? ?????????."))
                .andExpect(jsonPath("$.fieldErrors.username.messages[0]").value("username??? ????????? ?????????."))
                .andExpect(jsonPath("$.fieldErrors.isIdCheck.messages[0]").value("???????????? ??????????????? ?????? ?????????."))
                .andExpect(jsonPath("$.fieldErrors.password.messages.length()").value(1))
                .andExpect(jsonPath("$.fieldErrors.userId.messages.length()").value(1))
                .andExpect(jsonPath("$.fieldErrors.email.messages.length()").value(1))
                .andExpect(jsonPath("$.fieldErrors.tel.messages.length()").value(1))
                .andExpect(jsonPath("$.fieldErrors.username.messages.length()").value(1))
                .andExpect(jsonPath("$.fieldErrors.isIdCheck.messages.length()").value(1))

        ;

    }
    @Test
    @DisplayName("?????? ?????? ?????? ????????? - ???????????? ???????????? ????????? ??????")
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
    @DisplayName("?????? ?????? ?????? ????????? - ????????? ???????????? ????????? ??????")
    void joinMember_fail_beanValidation_wrongData() throws Exception{
        //given
        JoinMemberDto joinMemberDto = JoinMemberDto.builder()
                .username("???")
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
                .andExpect(jsonPath("$.fieldErrors.userId.messages[0]").value("userId??? 8 ~ 20?????? ????????? ????????? ?????????."))
                .andExpect(jsonPath("$.fieldErrors.email.messages[0]").value("????????? ????????? ???????????? ????????????."))
                .andExpect(jsonPath("$.fieldErrors.tel.messages[0]").value("??????????????? ???????????? ????????????."))
                .andExpect(jsonPath("$.fieldErrors.username.messages[0]").value("username??? 2 ~ 4?????? ????????? ????????? ?????????."))
                .andExpect(jsonPath("$.fieldErrors.password.messages.length()").value(2))
                .andExpect(jsonPath("$.fieldErrors.userId.messages.length()").value(1))
                .andExpect(jsonPath("$.fieldErrors.email.messages.length()").value(1))
                .andExpect(jsonPath("$.fieldErrors.tel.messages.length()").value(1))
                .andExpect(jsonPath("$.fieldErrors.username.messages.length()").value(1))
                .andExpect(jsonPath("$.fieldErrors.isIdCheck.messages.length()").value(1))
                .andDo(document("join-member-fail",
                        responseFields(
                                fieldWithPath("timestamp").description("????????? ??????"),
                                fieldWithPath("status").description("?????? ??????"),
                                fieldWithPath("path").description("?????? uri"),
                                fieldWithPath("fieldErrors").description("????????? ?????? ??????"),
                                fieldWithPath("fieldErrors.*.messages").description("?????? ?????????"),
                                fieldWithPath("fieldErrors.*.fieldName").description("?????? ?????? ??????"),
                                fieldWithPath("fieldErrors.*.rejectedValue").description("????????? ???"),
                                fieldWithPath("_links.main-page.href").description("?????? ????????? ??????"),
                                fieldWithPath("_links.profile.href").description("profile")
                        ),
                        links(linkWithRel(MAIN_PAGE).description("link to mainPage"),
                                linkWithRel(PROFILE).description("link to profile"))
                        )
                )


        ;

    }


    @Test
    @DisplayName("?????? ?????? ??????")
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
                                parameterWithName("id").description("?????? ?????????")
                        ),
                        responseFields(
                                fieldWithPath("id").description("?????????"),
                                fieldWithPath("userId").description("?????? ?????????"),
                                fieldWithPath("email").description("?????????"),
                                fieldWithPath("tel").description("????????? ??????"),
                                fieldWithPath("username").description("?????? ??????"),
                                fieldWithPath("_links.self.href").description("my self ??????"),
                                fieldWithPath("_links.member-update.href").description("?????? ?????? ??????"),
                                fieldWithPath("_links.member-delete.href").description("?????? ?????? ??????"),
                                fieldWithPath("_links.profile.href").description("profile")
                        ),
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("member-update").description("?????? ?????? ??????"),
                                linkWithRel("member-delete").description("?????? ?????? ??????"),
                                linkWithRel("profile").description("link to profile")

                        )
                ))
        ;

    }

    @Test
    @DisplayName("?????? ?????? ?????? ?????? - ???????????? ?????? ????????? ??????")
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
                .andExpect(jsonPath("$.message").value("???????????? ?????? ???????????????."))
                .andExpect(jsonPath("$._links.main-page.href").value("http://www.localhost:8080"))
                .andDo(document( "find-member-fail",
                        pathParameters(
                                parameterWithName("id").description("?????? ?????????")
                        ),
                        responseFields(
                                fieldWithPath("timestamp").description("????????? ??????"),
                                fieldWithPath("code").description("?????? ??????"),
                                fieldWithPath("path").description("?????? uri"),
                                fieldWithPath("message").description("?????? ?????????"),
                                fieldWithPath("_links.main-page.href").description("?????? ????????? ??????"),
                                fieldWithPath("_links.profile.href").description("profile")
                        ),
                        links(linkWithRel("main-page").description("link to mainPage"),
                                linkWithRel("profile").description("link to profile"))
                        ))
                ;
    }


    @Test
    @DisplayName("?????? ?????? ?????? ?????????")
    void updateMember_success() throws Exception{
        //given
        Member member = beforeMember("hslee0000");

        UpdateMemberDto updateMemberDto = UpdateMemberDto.builder()
                .username("??????2")
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
                                parameterWithName("id").description("?????? ?????????")
                        ),
                        requestFields(
                                fieldWithPath("username").description("?????? ??????"),
                                fieldWithPath("password").description("????????????"),
                                fieldWithPath("password2").description("???????????? ??????"),
                                fieldWithPath("email").description("?????????"),
                                fieldWithPath("tel").description("????????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("id").description("?????????"),
                                fieldWithPath("userId").description("?????? ?????????"),
                                fieldWithPath("email").description("?????????"),
                                fieldWithPath("tel").description("????????? ??????"),
                                fieldWithPath("username").description("?????? ??????"),
                                fieldWithPath("_links.member-info.href").description("?????? ?????? ?????? ??????"),
                                fieldWithPath("_links.main-page.href").description("?????? ????????? ??????"),
                                fieldWithPath("_links.profile.href").description("profile")
                        ),
                        links(
                                linkWithRel("member-info").description("?????? ?????? ?????? ??????"),
                                linkWithRel("main-page").description("link to mainPage"),
                                linkWithRel("profile").description("link to profile")
                        )
                        ))
        ;


    }

    @Test
    @DisplayName("?????? ?????? ?????? ????????? - null??? ????????? ???????????? ???????????? ?????? ??????.")
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
    @DisplayName("?????? ?????? ?????? ????????? - BeanValidation??? ????????? ??????.")
    void updateMember_blankData_success() throws Exception{
        //given
        Member member = beforeMember("hslee0000");


        UpdateMemberDto updateMemberDto = UpdateMemberDto.builder()
                .username("???")
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
                        fieldWithPath("timestamp").description("????????? ??????"),
                        fieldWithPath("status").description("?????? ??????"),
                        fieldWithPath("path").description("?????? uri"),
                        fieldWithPath("fieldErrors").description("????????? ?????? ??????"),
                        fieldWithPath("fieldErrors.*.messages").description("?????? ?????????"),
                        fieldWithPath("fieldErrors.*.fieldName").description("?????? ?????? ??????"),
                        fieldWithPath("fieldErrors.*.rejectedValue").description("????????? ???"),
                        fieldWithPath("_links.main-page.href").description("?????? ????????? ??????"),
                        fieldWithPath("_links.profile.href").description("profile")
                ),
                links(linkWithRel("main-page").description("link to mainPage"),
                        linkWithRel("profile").description("link to profile"))
        ))
        ;
    }



    @Test
    @DisplayName("?????? ??????")
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
                                    parameterWithName("id").description("?????? ?????????")
                            ),
                            responseFields(
                                    fieldWithPath("_links.main-page.href").description("?????? ????????? ??????"),
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