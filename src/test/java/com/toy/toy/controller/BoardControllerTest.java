package com.toy.toy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toy.toy.StaticVariable;
import com.toy.toy.dto.SearchConditionDto;
import com.toy.toy.dto.responseDto.LoginResponse;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
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
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriScheme = "https",uriHost = "api.boards.com" , uriPort = 443)
@ExtendWith(RestDocumentationExtension.class)
@Transactional
@Slf4j
class BoardControllerTest {


    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MockHttpSession mockHttpSession;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private FileRepository fileRepository;



    //게시글 목록 전용 등록 게시글
    //회원 : 10명
    //게시글 : 120개 (회원당 12개씩)
    //파일 : 240개 (게시글당 2개씩)

    int memberCount = 10;
    int boardCount = 11;
    int fileCount = 1;

    int count = 1;

    @BeforeEach
    @DisplayName("게시글 목록 조회시 필요한 데이터 셋팅")
    void beforeSet_findBoardListData(){

        log.info("count={}" , count);

        for(int i=0 ; i<memberCount; i++){
            Member member = Member.builder()
                    .username("이한수"+i)
                    .userId("hslee000"+i)
                    .memberGrade(NORMAL)
                    .password("asd123!@#"+i)
                    .email("hslee0000@naver.com"+i)
                    .tel("010-1111-111"+i)
                    .build();

            Member saveMember = memberRepository.save(member);

            for(int j=0 ; j< boardCount ; j++){
                Board board = Board.builder()
                        .subject(saveMember.getUserId() + "제목" + j)
                        .content(saveMember.getUserId() + "내용" + j)
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
    @DisplayName("게시글 목록 조회시 필요한 session셋팅")
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



        LoginResponse loginResponse = LoginResponse.builder()
                .id(findMember.getId())
                .userId(findMember.getUserId())
                .build();


        mockHttpSession.setAttribute(LOGIN_MEMBER , loginResponse);

    }

    //session 필요없음
    //쿼리 파라미터도 필요없음(page, size , sort)
    //SearchCoditionDto 필요없음
    //그냥 Get요청만 날리면 됨됨
    @Test
    @DisplayName("게시글 목록 가져오기 성공 - 기본값으로 넘겨줄때")
    void findBoardList_noCondition_success() throws Exception{
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
               .andExpect(jsonPath("$.content[0].boardContent").exists())
               .andExpect(jsonPath("$.content[0].filesDtoList.length()").value(fileCount))
               .andExpect(jsonPath("$.content[0].filesDtoList[0].id").exists())
               .andExpect(jsonPath("$.content[0].filesDtoList[0].uploadFilename").exists())
               .andExpect(jsonPath("$.content[0].filesDtoList[0].uploadFilename").exists())
               .andExpect(jsonPath("$.content[0].links.length()").value(1))
               .andExpect(jsonPath("$.pageInfo.length()").value(11))
               .andExpect(jsonPath("$.pageInfo[0].links[0].href").exists())
               .andExpect(jsonPath("$.pageInfo[0].pageNum").value(1))
               .andDo(document("find-boardList-success",
                       requestFields(
                               fieldWithPath("userId").description("회원 아이디"),
                               fieldWithPath("subject").description("게시글 제목")
                       ),
                       responseFields(
                               fieldWithPath("content[].boardId").description("게시글 식별자 아이디"),
                               fieldWithPath("content[].subject").description("게시글 제목"),
                               fieldWithPath("content[].writer").description("게시글 작성자"),
                               fieldWithPath("content[].readCount").description("조회수"),
                               fieldWithPath("content[].boardContent").description("게시글 내용"),
                               fieldWithPath("content[].filesDtoList[]").description("게시글 파일 목록"),
                               fieldWithPath("content[].filesDtoList[].id").description("파일 식별자 아이디"),
                               fieldWithPath("content[].filesDtoList[].uploadFilename").description("파일 이름"),
                               fieldWithPath("content[].links[].href").description("해당 게시글 보기 링크"),
                               fieldWithPath("content[].links[].rel").description("해당 게시글 보기 withRel").ignored(),
                               fieldWithPath("pageInfo[].pageNum").description("페이지 번호"),
                               fieldWithPath("pageInfo[].links[].href").description("페이지 번호 링크"),
                               fieldWithPath("pageInfo[].links[].rel").description("페이지 번호 링크 withRel").ignored()
                       )
               ))
       ;
    }


    @Test
    @DisplayName("게시글 목록 가져오기 성공 - subject를 포함하는 게시글을 가져올 때")
    void findBoardList_whereCondition_subject() throws Exception{
        //given
        SearchConditionDto searchCond = SearchConditionDto.builder()
                .subject("제목0")
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
                .andExpect(jsonPath("$.content[0].boardContent").exists())
                .andExpect(jsonPath("$.content[0].filesDtoList.length()").value(fileCount))
                .andExpect(jsonPath("$.content[0].filesDtoList[0].id").exists())
                .andExpect(jsonPath("$.content[0].filesDtoList[0].uploadFilename").exists())
                .andExpect(jsonPath("$.content[0].filesDtoList[0].uploadFilename").exists())
                .andExpect(jsonPath("$.content[0].links.length()").value(1))
                .andExpect(jsonPath("$.pageInfo.length()").value(1))
                .andExpect(jsonPath("$.pageInfo[0].links[0].href").exists())
                .andExpect(jsonPath("$.pageInfo[0].pageNum").value(1))
        ;
    }



    @Test
    @DisplayName("게시글 목록 가져오기 성공 - 작성한 userId와 일치하는 게시글을 가져올 때")
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
                .andExpect(jsonPath("$.content[0].boardId").exists())
                .andExpect(jsonPath("$.content[0].subject").exists())
                .andExpect(jsonPath("$.content[0].writer").exists())
                .andExpect(jsonPath("$.content[0].readCount").exists())
                .andExpect(jsonPath("$.content[0].boardContent").exists())
                .andExpect(jsonPath("$.content[0].filesDtoList.length()").value(fileCount))
                .andExpect(jsonPath("$.content[0].filesDtoList[0].id").exists())
                .andExpect(jsonPath("$.content[0].filesDtoList[0].uploadFilename").exists())
                .andExpect(jsonPath("$.content[0].filesDtoList[0].uploadFilename").exists())
                .andExpect(jsonPath("$.content[0].links.length()").value(1))
                .andExpect(jsonPath("$.pageInfo.length()").value(2))
                .andExpect(jsonPath("$.pageInfo[0].links[0].href").exists())
                .andExpect(jsonPath("$.pageInfo[0].pageNum").value(1))
        ;
    }

/*
    @Test
    @DisplayName("게시글 목록 가져오기 성공 - 정렬 조건을 날짜로 줄 때")
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
                .andExpect(jsonPath("$.content[0].boardId").value(229))
                .andExpect(jsonPath("$.content[0].subject").value("hslee0009제목10"))
                .andExpect(jsonPath("$.content[0].writer").value("hslee0009"))
                .andExpect(jsonPath("$.content[0].readCount").value(10))
                .andExpect(jsonPath("$.content[0].boardContent").value("hslee0009내용10"))
                .andExpect(jsonPath("$.content[0].filesDtoList[0].length()").value(2))
                .andExpect(jsonPath("$.content[0].links[0].length()").value(2))
                .andExpect(jsonPath("$.pageInfo.length()").value(11))

        ;
    }*/


    @Test
    @DisplayName("게시글 목록 가져오기 성공 - 정렬 조건을 조회수로 줄 때")
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
                .andExpect(jsonPath("$.content[0].boardId").value(209))
                .andExpect(jsonPath("$.content[0].subject").value("hslee0009제목0"))
                .andExpect(jsonPath("$.content[0].writer").value("hslee0009"))
                .andExpect(jsonPath("$.content[0].readCount").value(0))
                .andExpect(jsonPath("$.content[0].boardContent").value("hslee0009내용0"))
                .andExpect(jsonPath("$.content[0].filesDtoList[0].length()").value(2))
                .andExpect(jsonPath("$.content[0].links[0].length()").value(2))
                .andExpect(jsonPath("$.pageInfo.length()").value(11))
        ;
    }



    @Test
    @DisplayName("게시글 등록 성공 - 파일까지 함께 저장.")
    void registerBoardWithFileSave_success() throws Exception{
        //given
        String path = "C:\\Users\\USER\\fileUpload\\20220630_222944.png";


        MockMultipartFile filesList1 = new MockMultipartFile(
                "filesList", "20220630_222944.png", "image/png",  new FileInputStream(path));

        MockMultipartFile filesList2 = new MockMultipartFile(
                "filesList", "20220630_222944.png", "image/png", new FileInputStream(path));

        WriteBoardDto createdWriteBoardDto = WriteBoardDto.builder()
                .subject("제목입니다.")
                .boardContent("내용입니다.")
                .build();

        String writeBoardDtoString = objectMapper.writeValueAsString(createdWriteBoardDto);
        MockMultipartFile writeBoardDto = new MockMultipartFile("writeBoardDto","writeBoardDto",MediaType.APPLICATION_JSON_VALUE, writeBoardDtoString.getBytes(StandardCharsets.UTF_8));

        //expected
        mockMvc.perform(multipart("/boards")
                .file("filesList",filesList1.getBytes())
                .file("filesList",filesList2.getBytes())
                                .file(writeBoardDto)
                                .session(mockHttpSession)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(jsonPath("$.boardId").exists())
                .andExpect(jsonPath("$.subject").value("제목입니다."))
                .andExpect(jsonPath("$.writer").exists())
                .andExpect(jsonPath("$.boardContent").value("내용입니다."))
                .andExpect(jsonPath("$.readCount").value(0))
                .andExpect(jsonPath("$.filesDtoList.length()").value(2))
                .andExpect(jsonPath("$.filesDtoList[0].id").exists())
                .andExpect(jsonPath("$.filesDtoList[0].uploadFilename").exists())
                .andExpect(jsonPath("$._links.board-update.href").exists())
                .andExpect(jsonPath("$._links.board-delete.href").exists())

                .andDo(document("find-board-success" ,
                        requestParts(
                                partWithName("filesList").description("첨부 이미지"),
                                partWithName("writeBoardDto").description("제목과 내용")
                        ),
                        responseFields(
                                fieldWithPath("boardId").description("게시글 식별자"),
                                fieldWithPath("subject").description("게시글 제목"),
                                fieldWithPath("writer").description("게시글 작성자"),
                                fieldWithPath("boardContent").description("게시글 내용"),
                                fieldWithPath("readCount").description("게시글 조회수"),
                                fieldWithPath("filesDtoList[].id").description("파일 식별자"),
                                fieldWithPath("filesDtoList[].uploadFilename").description("파일 이름"),
                                fieldWithPath("_links.board-update.href").description("게시글 수정 링크"),
                                fieldWithPath("_links.board-delete.href").description("게시글 삭제 링크"),
                                fieldWithPath("_links.self.href").description("self")
                        ),
                        links(
                                linkWithRel("board-update").description("게시글 수정 링크"),
                                linkWithRel("board-delete").description("게시글 삭제 링크"),
                                linkWithRel("self").description("link to self")
                        )
                        ))

                ;
    }
    /*
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
    }
*/

/*
    @Test
    @DisplayName("게시글 상세 보기 -성공 사례")
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