package com.toy.toy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toy.toy.StaticVariable;
import com.toy.toy.dto.SearchConditionDto;
import com.toy.toy.dto.validationDto.LoginMemberDto;
import com.toy.toy.dto.validationDto.WriteBoardDto;
import com.toy.toy.entity.Board;
import com.toy.toy.entity.Files;
import com.toy.toy.entity.Member;
import com.toy.toy.entity.MemberGrade;
import com.toy.toy.repository.BoardRepository;
import com.toy.toy.repository.CommentRepository;
import com.toy.toy.repository.FileRepository;
import com.toy.toy.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.toy.toy.StaticVariable.*;
import static com.toy.toy.entity.MemberGrade.*;
import static org.springframework.hateoas.MediaTypes.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BoardControllerTest {


    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MockHttpServletRequest mockHttpServletRequest;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private FileRepository fileRepository;



    //????????? ?????? ?????? ?????? ?????????
    //?????? : 10???
    //????????? : 120??? (????????? 12??????)
    //?????? : 240??? (???????????? 2??????)

    int memberCount = 10;
    int boardCount = 12;
    int fileCount = 2;

    @BeforeEach
    void beforeSet_findBoardListData(){


        for(int i=0 ; i<memberCount; i++){
            Member member = Member.builder()
                    .username("?????????"+i)
                    .userId("hslee000"+i)
                    .memberGrade(NORMAL)
                    .password("asd123!@#"+i)
                    .email("hslee0000@naver.com"+i)
                    .tel("010-1111-111"+i)
                    .build();

            Member saveMember = memberRepository.save(member);

            for(int j=0 ; j< boardCount ; j++){
                Board board = Board.builder()
                        .subject(saveMember.getUserId() + "??????" + j)
                        .content(saveMember.getUserId() + "??????" + j)
                        .readCount(j)
                        .member(saveMember)
                        .build();

                Board saveBoard = boardRepository.save(board);

                for(int k=0; k<fileCount ; k++){
                    Files file = Files.builder()
                            .serverFilename(saveMember.getUserId() + "." + saveBoard.getSubject() + "." + k)
                            .uploadFilename(saveMember.getUserId() + "." + saveBoard.getSubject() + "." + k)
                            .board(saveBoard)
                            .build();

                    fileRepository.save(file);
                }
            }

        }
    }


    @BeforeEach
    void beforeSet_SessionDataArrangement(){

        Member member = Member.builder()
                .username("?????????")
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


        session.setAttribute(LOGIN_MEMBER , loginMemberDto);

    }

    //session ????????????
    //?????? ??????????????? ????????????(page, size , sort)
    //SearchCoditionDto ????????????
    //?????? Get????????? ????????? ??????
   @Test
    @DisplayName("????????? ?????? ???????????? ?????? - ??????????????? ????????????")
    void findBoardList_noCondition() throws Exception{
        //given
       SearchConditionDto searchCond = SearchConditionDto.builder()
               .build();

       //expected
       mockMvc.perform(get("/boards")
                       .accept(HAL_JSON)
                       .content(objectMapper.writeValueAsString(searchCond))
                       .contentType(MediaType.APPLICATION_JSON)
       )
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.content.length()").value(10))
               .andExpect(jsonPath("$.content[0].boardId").exists())
               .andExpect(jsonPath("$.content[0].subject").exists())
               .andExpect(jsonPath("$.content[0].writer").exists())
               .andExpect(jsonPath("$.content[0].readCount").exists())
               .andExpect(jsonPath("$.content[0].filesDtoList.length()").value(fileCount))
               .andExpect(jsonPath("$.content[0].filesDtoList[0].id").exists())
               .andExpect(jsonPath("$.content[0].filesDtoList[0].uploadFilename").exists())
               .andExpect(jsonPath("$.content[0].filesDtoList[0].uploadFilename").exists())
               .andExpect(jsonPath("$.content[0].links.length()").value(1))
               .andExpect(jsonPath("$.pageInfo.length()").value(11))
               .andExpect(jsonPath("$.pageInfo[0].links[0].href").exists())
               .andExpect(jsonPath("$.pageInfo[0].pageNum").value(1))
               .andExpect(jsonPath("$.pageInfo[10].nextPageNum").value(11))
       ;
    }

    @Test
    @DisplayName("????????? ?????? ???????????? ?????? - subject??? ???????????? ???????????? ????????? ???")
    void findBoardList_whereCondition_subject() throws Exception{
        //given
        SearchConditionDto searchCond = SearchConditionDto.builder()
                .subject("??????0")
                .build();

        //expected
        mockMvc.perform(get("/boards")
                        .accept(HAL_JSON)
                        .content(objectMapper.writeValueAsString(searchCond))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(10))
                .andExpect(jsonPath("$.pageInfo.length()").value(1))
        ;
    }


    @Test
    @DisplayName("????????? ?????? ???????????? ?????? - ????????? userId??? ???????????? ???????????? ????????? ???")
    void findBoardList_whereCondition_userId() throws Exception{
        //given
        SearchConditionDto searchCond = SearchConditionDto.builder()
                .userId("hslee0000")
                .build();

        //expected
        mockMvc.perform(get("/boards")
                        .accept(HAL_JSON)
                        .content(objectMapper.writeValueAsString(searchCond))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(10))
                .andExpect(jsonPath("$.pageInfo.length()").value(2))
        ;
    }

    @Test
    @DisplayName("????????? ?????? ???????????? ?????? - ?????? ????????? ????????? ??? ???")
    void findBoardList_orderByCondition_createdDate() throws Exception{
        //given
        SearchConditionDto searchCond = SearchConditionDto.builder()
                .build();

        //expected
        mockMvc.perform(get("/boards").param("sort","createdDate,DESC")
                        .accept(HAL_JSON)
                        .content(objectMapper.writeValueAsString(searchCond))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(10))
                .andExpect(jsonPath("$.content[0].writer").value("hslee0009"))
                .andExpect(jsonPath("$.content[0].subject").value("hslee0009??????11"))
        ;
    }


    @Test
    @DisplayName("????????? ?????? ???????????? ?????? - ?????? ????????? ???????????? ??? ???")
    void findBoardList_orderByCondition_readCount() throws Exception{
        //given
        SearchConditionDto searchCond = SearchConditionDto.builder()
                .build();

        //expected
        mockMvc.perform(get("/boards").param("sort","readCount,ASC")
                        .accept(HAL_JSON)
                        .content(objectMapper.writeValueAsString(searchCond))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(10))
                .andExpect(jsonPath("$.content[0].readCount").value(0))
        ;
    }



    @Test
    @DisplayName("????????? ?????? ?????? - ???????????? ?????? ??????.")
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
                .subject("???????????????.")
                .boardContent("???????????????.")
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
    @DisplayName("????????? ?????? ?????? - Beanvalidation??????")
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
    }

/*
    @Test
    @DisplayName("????????? ?????? ?????? -?????? ??????")
    void findBoardWithFiles_success() throws Exception {
        //given


        String jsonWriteBoardDto = objectMapper.writeValueAsString(writeBoardDto);
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