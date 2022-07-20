package com.toy.toy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toy.toy.StaticVariable;
import com.toy.toy.dto.LoginMemberDto;
import com.toy.toy.dto.validationDto.WriteBoardDto;
import com.toy.toy.entity.Member;
import com.toy.toy.entity.MemberGrade;
import com.toy.toy.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.toy.toy.entity.MemberGrade.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class BoardControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MockHttpServletRequest mockHttpServletRequest;
    
/*
    private List<MultipartFile> getFile(){
        MultipartFile multipartFile = new MultipartFile();

    }
*/

/*    @BeforeEach
    void beforeSet_SessionDataArrangement(){
        Member member = Member.builder()
                .username("이한수")
                .userId("hslee0000")
                .memberGrade(NORMAL)
                .password("asd123!@#")
                .email("hslee0000@naver.com")
                .tel("010-1111-1111")
                .build();

        Member findMember = memberRepository.save(member);

        HttpSession session = mockHttpServletRequest.getSession();

        LoginMemberDto loginMemberDto = LoginMemberDto.builder()
                .userId(findMember.getUserId())
                .build();


        session.setAttribute(StaticVariable.LOGIN_MEMBER , loginMemberDto);

    }


    @Test
    @DisplayName("게시글 등록 성공 - 파일까지 함께 저장.")
    void registerBoardWithFileSave_success() throws Exception{
        //given
        String path = "C:\\Users\\USER\\Desktop\\20220630_222944.png";

        FileInputStream fileInputStream1 = new FileInputStream(path);
        FileInputStream fileInputStream2 = new FileInputStream(path);

        MockMultipartFile filesList1 = new MockMultipartFile(
                "filesList", "20220630_222944.png", MediaType.IMAGE_PNG_VALUE, fileInputStream1);

        MockMultipartFile filesList2 = new MockMultipartFile(
                "filesList", "20220630_222944.png", MediaType.IMAGE_PNG_VALUE, fileInputStream2);

        WriteBoardDto writeBoardDto = WriteBoardDto.builder()
                .subject("제목입니다.")
                .boardContent("내용입니다.")
                .build();


        String jsonWriteBoardDto = objectMapper.writeValueAsString(writeBoardDto);
        MockMultipartFile multipartWriteBoardDto = new MockMultipartFile("writeBoardDto","writeBoardDto",MediaType.APPLICATION_JSON_VALUE, jsonWriteBoardDto.getBytes(StandardCharsets.UTF_8));

        //expected
        mockMvc.perform(multipart("/boards")
                .file("filesList",filesList1.getBytes())
                .file("filesList",filesList2.getBytes())
                                .file(multipartWriteBoardDto)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.boardId").exists())
                .andExpect(jsonPath("$.writer").exists())
                .andExpect(jsonPath("$.readCount").value(0))
                .andExpect(jsonPath("$.boardContent").value(writeBoardDto.getBoardContent()))
                .andExpect(jsonPath("$.subject").value(writeBoardDto.getSubject()))
                .andExpect(jsonPath("$.filesDtoList.length()").value(2))
                .andExpect(jsonPath("$._links.board-update.href").exists())
                .andExpect(jsonPath("$._links.board-delete.href").exists())
                ;
    }

    @Test
    @DisplayName("게시글 등록 실패 - Beanvalidation위반")
    void registerBoard_fail_byBeanValidation() throws Exception {
        //given
        WriteBoardDto writeBoardDto = WriteBoardDto.builder()
                .subject(null)
                .boardContent("")
                .build();

        String jsonWriteBoardDto = objectMapper.writeValueAsString(writeBoardDto);
        MockMultipartFile multipartWriteBoardDto = new MockMultipartFile("writeBoardDto","writeBoardDto",MediaType.APPLICATION_JSON_VALUE, jsonWriteBoardDto.getBytes(StandardCharsets.UTF_8));

        //expected
        mockMvc.perform(multipart("/boards")
                        .file(multipartWriteBoardDto)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.path").value("uri=/boards"))
                .andExpect(jsonPath("$.fieldErrors.length()").value(2))
                .andExpect(jsonPath("$.fieldErrors.subject").exists())
                .andExpect(jsonPath("$.fieldErrors.boardContent").exists())

        ;
    }*/


   /* @Test
    @DisplayName("게시글 상세 보기 -성공 사례")
    void findBoardWithFiles_success() throws Exception {
        //given


       // String jsonWriteBoardDto = objectMapper.writeValueAsString(writeBoardDto);
        MockMultipartFile multipartWriteBoardDto = new MockMultipartFile("writeBoardDto", "writeBoardDto", MediaType.APPLICATION_JSON_VALUE, jsonWriteBoardDto.getBytes(StandardCharsets.UTF_8));

        //expected
        mockMvc.perform(get("/boards")

                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.path").value("uri=/boards"))
                .andExpect(jsonPath("$.fieldErrors.length()").value(2))
                .andExpect(jsonPath("$.fieldErrors.subject").exists())
                .andExpect(jsonPath("$.fieldErrors.boardContent").exists())

        ;
    }*/
}