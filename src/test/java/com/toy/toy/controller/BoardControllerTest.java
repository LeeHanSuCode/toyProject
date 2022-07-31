package com.toy.toy.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.toy.toy.ControllerTestAnnotation;
import com.toy.toy.StaticVariable;
import com.toy.toy.dto.SearchConditionDto;
import com.toy.toy.dto.responseDto.LoginResponse;
import com.toy.toy.dto.validationDto.LoginMemberDto;
import com.toy.toy.dto.validationDto.UpdateBoardDto;
import com.toy.toy.dto.validationDto.WriteBoardDto;
import com.toy.toy.entity.Board;
import com.toy.toy.entity.Files;
import com.toy.toy.entity.Member;
import com.toy.toy.entity.MemberGrade;
import com.toy.toy.repository.BoardRepository;
import com.toy.toy.repository.CommentRepository;
import com.toy.toy.repository.FileRepository;
import com.toy.toy.repository.MemberRepository;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Request;
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
import org.springframework.restdocs.hypermedia.HypermediaDocumentation;
import org.springframework.restdocs.hypermedia.LinkDescriptor;
import org.springframework.restdocs.hypermedia.LinksSnippet;
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
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@AutoConfigureRestDocs
@AutoConfigureMockMvc
@SpringBootTest
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




    //사전 회원 등록
    private Member saveMemberData(String userId){
        Member member = Member.builder()
                .username("이한수")
                .userId(userId)
                .password("asd123@@")
                .email("dlsdn857758@naver.com")
                .memberGrade(MemberGrade.NORMAL)
                .tel("010-1111-1111")
                .build();
        return memberRepository.save(member);
    }
    //사전 게시글 등록
    private Board saveBoardData(Member member , String subject, String content ,int readCount){
        Board board = Board.builder()
                .subject(subject)
                .content(content)
                .readCount(readCount)
                .member(member)
                .build();

        return boardRepository.save(board);
    }

    //사전 파일 데이터 등록
    private Files saveFileData(Board board){
        Files files = Files.builder()
                .board(board)
                .serverFilename("서버 저장 이름")
                .uploadFilename("업로드 파일 이름")
                .build();

        return fileRepository.save(files);
    }


    //세션 데이터 준비
    private LoginResponse createdLoginResponse(Member member){
        return LoginResponse.builder()
                .userId(member.getUserId())
                .memberGrade(MemberGrade.NORMAL)
                .id(member.getId())
                .build();
    }

    @Test
    @DisplayName("게시글 목록 가져오기 성공")
    void findBoardList_noCondition_success() throws Exception{
        //given
        String subject = "제목";
        String content = "내용";

        Member member = saveMemberData("hslee0000");
        Board board1 = saveBoardData(member,subject,content,0);
        Board board2 = saveBoardData(member,subject,content,0);
        Board board3 = saveBoardData(member,subject,content,0);

        Files files1 = saveFileData(board1);
        Files files2 = saveFileData(board2);
        Files files3 = saveFileData(board3);

        SearchConditionDto searchCond = SearchConditionDto.builder()
               .build();
       
       //expected
       mockMvc.perform(RestDocumentationRequestBuilders.get("/boards")
                       .queryParam("page","0")
                       .queryParam("size","3")
                       .queryParam("sort","id,desc")
                       .accept(HAL_JSON)
                       .content(objectMapper.writeValueAsString(searchCond))
                       .contentType(MediaType.APPLICATION_JSON)
       )
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.content.length()").value(3))
               .andExpect(jsonPath("$.content[0].boardId").exists())
               .andExpect(jsonPath("$.content[0].subject").exists())
               .andExpect(jsonPath("$.content[0].writer").exists())
               .andExpect(jsonPath("$.content[0].readCount").exists())
               .andExpect(jsonPath("$.content[0].filesDtoList.length()").value(1))
               .andExpect(jsonPath("$.content[0].filesDtoList[0].id").exists())
               .andExpect(jsonPath("$.content[0].filesDtoList[0].uploadFilename").exists())
               .andExpect(jsonPath("$.content[0].links[0]").exists())
               .andExpect(jsonPath("$.pageInfo.length()").value(1))
               .andExpect(jsonPath("$.pageInfo[0].pageNum").exists())
               .andExpect(jsonPath("$.pageInfo[0].links[0].rel").exists())
               .andExpect(jsonPath("$.pageInfo[0].links[0].href").exists())
               .andDo(document("find-boardList",
                            requestParameters(
                                    parameterWithName("page").description("페이지 번호"),
                                    parameterWithName("size").description("페이지당 가져올 게시글 갯수"),
                                    parameterWithName("sort").description("정렬 조건")
                            ),
                       responseFields(
                               fieldWithPath("content[].boardId").description("게시글 식별자"),
                               fieldWithPath("content[].subject").description("게시글 제목"),
                               fieldWithPath("content[].writer").description("게시글 작성자"),
                               fieldWithPath("content[].readCount").description("게시글 조회수"),
                               fieldWithPath("content[].boardContent").description("게시글 내용"),
                               fieldWithPath("content[].filesDtoList[].id").description("게시글의 파일 식별자"),
                               fieldWithPath("content[].filesDtoList[].uploadFilename").description("게시글의 파일 이름"),
                               subsectionWithPath("content[].links[]").description("게시글 상세 보기 링크"),
                               fieldWithPath("pageInfo[].pageNum").description("페이지 번호"),
                               subsectionWithPath("pageInfo[].links[]").description("페이지 번호 링크"),
                               subsectionWithPath("representationModel.links[]").description("profile To Link")
                       )
                       ))
       ;
    }


    @Test
    @DisplayName("게시글 목록 가져오기 성공 - subject를 포함하는 게시글을 가져올 때")
    void findBoardList_whereCondition_subject() throws Exception{
        //given
        Member member = saveMemberData("hslee0000");

        saveBoardData(member,"홍길동 입니다.","홍길동 입니다.",0);
        saveBoardData(member,"임꺽정 입니다.","임꺽정 입니다.",0);
        saveBoardData(member,"신사임당 입니다.","신사임당 입니다.",0);

        SearchConditionDto searchCond = SearchConditionDto.builder()
                .subject("홍길동")
                .build();

        //expected
        mockMvc.perform(get("/boards")
                        .queryParam("page","0")
                        .queryParam("size","3")
                        .queryParam("sort","id,desc")
                        .accept(HAL_JSON)
                        .content(objectMapper.writeValueAsString(searchCond))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].boardId").exists())
                .andExpect(jsonPath("$.content[0].subject").value("홍길동 입니다."))
                .andExpect(jsonPath("$.content[0].writer").exists())
                .andExpect(jsonPath("$.content[0].readCount").exists())
                .andExpect(jsonPath("$.content[0].boardContent").value("홍길동 입니다."))
                .andExpect(jsonPath("$.content[0].filesDtoList.length()").value(0))
                .andExpect(jsonPath("$.content[0].links.length()").value(1))
                .andExpect(jsonPath("$.pageInfo.length()").value(1))
                .andExpect(jsonPath("$.pageInfo[0].links[0].href").exists())
                .andExpect(jsonPath("$.pageInfo[0].pageNum").value(1))
        ;
    }


    @Test
    @DisplayName("게시글 목록 가져오기 성공 - 작성한 userId를 포함하는 게시글 가져올 때")
    void findBoardList_whereCondition_userId() throws Exception{
        //given
        Member member1 = saveMemberData("hslee0000");
        Member member2 = saveMemberData("hslee0001");
        Member member3 = saveMemberData("hslee0002");



        saveBoardData(member1,"제목 hslee0000","내용 hslee0000",0);
        saveBoardData(member2,"제목 hslee0001","내용 hslee0001",0);
        saveBoardData(member3,"제목 hslee0002","내용 hslee0002",0);


        SearchConditionDto searchCond = SearchConditionDto.builder()
                .userId("hslee0000")
                .build();

        //expected
        mockMvc.perform(get("/boards")
                        .queryParam("page","0")
                        .queryParam("size","3")
                        .queryParam("sort","id,desc")
                        .accept(HAL_JSON)
                        .content(objectMapper.writeValueAsString(searchCond))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].boardId").exists())
                .andExpect(jsonPath("$.content[0].subject").value("제목 hslee0000"))
                .andExpect(jsonPath("$.content[0].writer").exists())
                .andExpect(jsonPath("$.content[0].readCount").exists())
                .andExpect(jsonPath("$.content[0].boardContent").value("내용 hslee0000"))
                .andExpect(jsonPath("$.content[0].filesDtoList.length()").value(0))
                .andExpect(jsonPath("$.content[0].links.length()").value(1))
                .andExpect(jsonPath("$.pageInfo.length()").value(1))
                .andExpect(jsonPath("$.pageInfo[0].links[0].href").exists())
                .andExpect(jsonPath("$.pageInfo[0].pageNum").value(1))
        ;
    }



    @Test
    @DisplayName("게시글 목록 가져오기 성공 - 정렬 조건을 조회수로 줄 때")
    void findBoardList_orderByCondition_readCount() throws Exception{
        //given
        Member member = saveMemberData("hslee0000");

        saveBoardData(member,"홍길동 입니다.","홍길동 입니다.",10);
        saveBoardData(member,"임꺽정 입니다.","임꺽정 입니다.",20);
        saveBoardData(member,"신사임당 입니다.","신사임당 입니다.",30);

        SearchConditionDto searchCond = SearchConditionDto.builder()
                .build();

        //expected
        mockMvc.perform(get("/boards")
                        .queryParam("page","0")
                        .queryParam("size","3")
                        .queryParam("sort","readCount,DESC")
                        .accept(HAL_JSON)
                        .content(objectMapper.writeValueAsString(searchCond))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(3))
                .andExpect(jsonPath("$.content[0].boardId").exists())
                .andExpect(jsonPath("$.content[0].subject").exists())
                .andExpect(jsonPath("$.content[0].writer").exists())
                .andExpect(jsonPath("$.content[0].readCount").value(30))
                .andExpect(jsonPath("$.content[2].readCount").value(10))
                .andExpect(jsonPath("$.content[0].filesDtoList.length()").value(0))
                .andExpect(jsonPath("$.content[0].links[0]").exists())
                .andExpect(jsonPath("$.pageInfo.length()").value(1))
                .andExpect(jsonPath("$.pageInfo[0].pageNum").exists())
                .andExpect(jsonPath("$.pageInfo[0].links[0].rel").exists())
                .andExpect(jsonPath("$.pageInfo[0].links[0].href").exists())

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

        Member saveMember = saveMemberData("hslee0000");
        LoginResponse loginResponse = createdLoginResponse(saveMember);
        mockHttpSession.setAttribute(LOGIN_MEMBER,loginResponse);

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
                .andExpect(jsonPath("$.subject").value(createdWriteBoardDto.getSubject()))
                .andExpect(jsonPath("$.writer").exists())
                .andExpect(jsonPath("$.boardContent").value(createdWriteBoardDto.getBoardContent()))
                .andExpect(jsonPath("$.readCount").value(0))
                .andExpect(jsonPath("$.filesDtoList.length()").value(2))
                .andExpect(jsonPath("$.filesDtoList[0].id").exists())
                .andExpect(jsonPath("$.filesDtoList[0].uploadFilename").exists())
                .andExpect(jsonPath("$._links.board-update.href").exists())
                .andExpect(jsonPath("$._links.board-delete.href").exists())
                .andExpect(jsonPath("$._links.self.href").exists())

                .andDo(document("register-board-success" ,
                        requestParts(
                                partWithName("filesList").description("첨부 이미지"),
                                partWithName("writeBoardDto").description("제목과 내용").ignored()
                        ),
                        requestPartFields("writeBoardDto",
                          fieldWithPath("subject").description("게시글 제목"),
                          fieldWithPath("boardContent").description("게시글 내용")
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
                                fieldWithPath("_links.self.href").description("self"),
                                fieldWithPath("_links.profile.href").description("profile To Link")
                        )
                        ))

                ;
    }

    @Test
    @DisplayName("게시글 등록 실패 - Beanvalidation위반")
    void registerBoard_fail_byBeanValidation() throws Exception {
        //given
        WriteBoardDto writeBoardDto = WriteBoardDto.builder()
                .subject("")
                .boardContent("")
                .build();

        String jsonWriteBoardDto = objectMapper.writeValueAsString(writeBoardDto);
        MockMultipartFile multipartWriteBoardDto = new MockMultipartFile("writeBoardDto","writeBoardDto",MediaType.APPLICATION_JSON_VALUE, jsonWriteBoardDto.getBytes(StandardCharsets.UTF_8));

        Member saveMember = saveMemberData("hslee0000");
        LoginResponse loginResponse = createdLoginResponse(saveMember);
        mockHttpSession.setAttribute(LOGIN_MEMBER,loginResponse);


        //expected
        mockMvc.perform(multipart("/boards")
                        .file(multipartWriteBoardDto)
                        .session(mockHttpSession)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(HAL_JSON)

                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.path").value("uri=/boards"))
                .andExpect(jsonPath("$.fieldErrors.subject.messages.length()").value(1))
                .andExpect(jsonPath("$.fieldErrors.subject.messages[0]").value( "subject은 필수값 입니다."))
                .andExpect(jsonPath("$.fieldErrors.subject.fieldName").value("subject"))
                .andExpect(jsonPath("$.fieldErrors.subject.rejectedValue").value("값이 들어오지 않음"))
                .andExpect(jsonPath("$.fieldErrors.boardContent.messages.length()").value(1))
                .andExpect(jsonPath("$.fieldErrors.boardContent.messages[0]").value( "boardContent은 필수값 입니다."))
                .andExpect(jsonPath("$.fieldErrors.boardContent.fieldName").value("boardContent"))
                .andExpect(jsonPath("$.fieldErrors.boardContent.rejectedValue").value("값이 들어오지 않음"))

        ;
    }


    @Test
    @DisplayName("게시글 상세 보기 -성공 사례")
    void findBoard_success() throws Exception {
        //given
        Member member = saveMemberData("hslee0000");
        Board saveBoard = saveBoardData(member, "제목입니다", "내용입니다",0);
        Files files = saveFileData(saveBoard);
        //expected
        mockMvc.perform(RestDocumentationRequestBuilders.get("/boards/{id}",saveBoard.getId())
                        .accept(HAL_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.boardId").value(saveBoard.getId()))
                .andExpect(jsonPath("$.subject").value(saveBoard.getSubject()))
                .andExpect(jsonPath("$.writer").value(member.getUserId()))
                .andExpect(jsonPath("$.readCount").value(saveBoard.getReadCount()))
                .andExpect(jsonPath("$.boardContent").value(saveBoard.getContent()))
                .andExpect(jsonPath("$.filesDtoList.length()").value(1))
                .andExpect(jsonPath("$.filesDtoList[0].id").value(files.getId()))
                .andExpect(jsonPath("$.filesDtoList[0].uploadFilename").value(files.getUploadFilename()))
                .andExpect(jsonPath("$._links.board-list.href").exists())
                .andDo(document("find-board",
                            pathParameters(
                                    parameterWithName("id").description("게시글 식별자")
                            ),
                        responseFields(
                                fieldWithPath("boardId").description("게시글 식별자"),
                                fieldWithPath("subject").description("게시글 제목"),
                                fieldWithPath("writer").description("게시글 작성자"),
                                fieldWithPath("readCount").description("게시글 조회수"),
                                fieldWithPath("boardContent").description("게시글 내용"),
                                fieldWithPath("filesDtoList[].id").description("게시글에 등록된 파일 식별자"),
                                fieldWithPath("filesDtoList[].uploadFilename").description("게시글에 등록된 파일 이름"),
                                fieldWithPath("_links.board-list.href").description("게시글 목록 링크"),
                                fieldWithPath("_links.profile.href").description("profile To Link")
                        )
                        ))
        ;
    }

    @Test
    @DisplayName("게시글 상세 보기 - 존재하지 않는 식별자로 조회할 때")
    void findBoard_fail() throws Exception {
        //given
        Long boardId = 100L;
        //expected
        mockMvc.perform(get("/boards/{id}",boardId)
                        .accept(HAL_JSON)
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.code").value("BoardNotFound"))
                .andExpect(jsonPath("$.path").exists())
                .andExpect(jsonPath("$._links.main-page.href").exists())
                .andExpect(jsonPath("$._links.profile.href").exists())

        ;
    }


    @Test
    @DisplayName("게시글 수정 - 성공")
    void updateBoard_success() throws Exception{
        //given
        Member saveMember = saveMemberData("hslee0000");
        Board saveBoard = saveBoardData(saveMember, "제목", "내용", 0);
        Files file1 = saveFileData(saveBoard);
        Files file2 = saveFileData(saveBoard);

        String path = "C:\\Users\\USER\\fileUpload\\20220630_222944.png";

        MockMultipartFile filesList1 = new MockMultipartFile(
                "filesList", "20220630_222944.png", "image/png",  new FileInputStream(path));

        MockMultipartFile filesList2 = new MockMultipartFile(
                "filesList", "20220630_222944.png", "image/png", new FileInputStream(path));

        UpdateBoardDto updateBoardDtoInstance = new UpdateBoardDto();
        updateBoardDtoInstance.setSubject("바뀐 제목");
        updateBoardDtoInstance.setBoardContent("바뀐 내용");
        updateBoardDtoInstance.getAliveFiles().add(file1.getId());

        String updateBoardDtoString = objectMapper.writeValueAsString(updateBoardDtoInstance);
        MockMultipartFile updateBoardDto = new MockMultipartFile("updateBoardDto","updateBoardDto",MediaType.APPLICATION_JSON_VALUE, updateBoardDtoString.getBytes(StandardCharsets.UTF_8));

        LoginResponse loginResponse = createdLoginResponse(saveMember);
        mockHttpSession.setAttribute(LOGIN_MEMBER,loginResponse);

        //expected
        mockMvc.perform(RestDocumentationRequestBuilders.multipart("/boards/update/{id}",saveBoard.getId())
                .file("newFiles",filesList1.getBytes())
                .file("newFiles",filesList2.getBytes())
                .file(updateBoardDto)
                .session(mockHttpSession)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(HAL_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.board-info.href").exists())
                .andExpect(jsonPath("$._links.board-list.href").exists())
                .andDo(document("update-board",
                            pathParameters(
                                    parameterWithName("id").description("게시글 식별자")
                            ),
                        requestParts(
                                partWithName("newFiles").description("새로 등록될 파일"),
                                partWithName("updateBoardDto").description("수정될 내용").ignored()
                        ),
                        requestPartFields("updateBoardDto",
                                fieldWithPath("subject").description("변경될 제목"),
                                fieldWithPath("boardContent").description("변경될 내용"),
                                fieldWithPath("aliveFiles").description("살안마은 파일 아이디 목록")
                                ),

                        links(
                                linkWithRel(BOARD_LIST).description("게시글 목록 링크"),
                                linkWithRel(BOARD_INFO).description("게시글 상세 보기"),
                                linkWithRel(PROFILE).description("profile To Link")
                        )
                        ))
                ;
    }

    @Test
    @DisplayName("게시글 수정 실패 - BeanValidation 위반")
    void updateBoard_fail_byBeanValidation() throws Exception {
        //given
        UpdateBoardDto updateBoardDtoData = new UpdateBoardDto();
        updateBoardDtoData.setBoardContent("");
        updateBoardDtoData.setSubject("");

        String writeValueAsString = objectMapper.writeValueAsString(updateBoardDtoData);

        MockMultipartFile multipartFileUpdateBoardDto =  new MockMultipartFile("updateBoardDto","updateBoardDto",MediaType.APPLICATION_JSON_VALUE, writeValueAsString.getBytes(StandardCharsets.UTF_8));

        Member saveMember =saveMemberData("hslee0000");
        Board saveBoard = saveBoardData(saveMember, "제목", "내용", 0);
        LoginResponse loginResponse = createdLoginResponse(saveMember);
        mockHttpSession.setAttribute(LOGIN_MEMBER,loginResponse);

        //expected
        mockMvc.perform(RestDocumentationRequestBuilders.multipart("/boards/update/{id}",saveBoard.getId())
                .file(multipartFileUpdateBoardDto)
                .session(mockHttpSession)
                .accept(HAL_JSON)
                .contentType(MediaType.MULTIPART_FORM_DATA)
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.path").exists())
                .andExpect(jsonPath("$.fieldErrors.subject.messages.length()").value(1))
                .andExpect(jsonPath("$.fieldErrors.subject.messages[0]").value( "subject은 필수값 입니다."))
                .andExpect(jsonPath("$.fieldErrors.subject.fieldName").value("subject"))
                .andExpect(jsonPath("$.fieldErrors.subject.rejectedValue").value("값이 들어오지 않음"))
                .andExpect(jsonPath("$.fieldErrors.boardContent.messages.length()").value(1))
                .andExpect(jsonPath("$.fieldErrors.boardContent.messages[0]").value( "boardContent은 필수값 입니다."))
                .andExpect(jsonPath("$.fieldErrors.boardContent.fieldName").value("boardContent"))
                .andExpect(jsonPath("$.fieldErrors.boardContent.rejectedValue").value("값이 들어오지 않음"))
                ;
    }


    @Test
    @DisplayName("게시글 삭제 성공")
    void deleteBoard_success() throws Exception{
        //given
        Member saveMember = saveMemberData("hslee0000");
        Board saveBoard = saveBoardData(saveMember, "제목", "내용",0);
        LoginResponse loginResponse = createdLoginResponse(saveMember);
        mockHttpSession.setAttribute(LOGIN_MEMBER,loginResponse);

        //expected
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/boards/{id}",saveBoard.getId())
                .accept(HAL_JSON)
                .session(mockHttpSession)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("delete-board",
                        pathParameters(
                                parameterWithName("id").description("게시글 식별자")
                        ),
                        links(
                                linkWithRel(BOARD_LIST).description("게시글 목록 링크"),
                                linkWithRel(MAIN_PAGE).description("메인 페이지 링크"),
                                linkWithRel(PROFILE).description("profile To Link")
                        )
                        ))

                ;
    }

    @Test
    @DisplayName("게시글 삭제 실패 - 존재하지 않는 식별자")
    void deleteBoard_fail() throws Exception{
        //given
        Long boardId = 100L;
        Member saveMember = saveMemberData("hslee0000");
        LoginResponse loginResponse = createdLoginResponse(saveMember);
        mockHttpSession.setAttribute(LOGIN_MEMBER,loginResponse);

        //expected
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/boards/{id}",boardId)
                        .accept(HAL_JSON)
                        .session(mockHttpSession)
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.code").value("BoardNotFound"))
                .andExpect(jsonPath("$.path").exists())
                .andExpect(jsonPath("$._links.main-page.href").exists())
                .andExpect(jsonPath("$._links.profile.href").exists())

        ;
    }



}