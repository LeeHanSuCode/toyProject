package com.toy.toy.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.toy.toy.ControllerTestAnnotation;
import com.toy.toy.StaticVariable;
import com.toy.toy.dto.responseDto.LoginResponse;
import com.toy.toy.dto.validationDto.LoginMemberDto;
import com.toy.toy.dto.validationDto.WriteContentDto;
import com.toy.toy.entity.Board;
import com.toy.toy.entity.Comment;
import com.toy.toy.entity.Member;
import com.toy.toy.entity.MemberGrade;
import com.toy.toy.repository.BoardRepository;
import com.toy.toy.repository.CommentRepository;
import com.toy.toy.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

import static com.toy.toy.StaticVariable.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ControllerTestAnnotation
@AutoConfigureRestDocs
@Slf4j
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private CommentRepository commentRepository;

    private MockHttpSession httpSession = new MockHttpSession();

    //사전 회원 등록
   private Member saveMemberData(){
        Member member = Member.builder()
                .username("이한수")
                .userId("dlsdn857758")
                .password("asd123@@")
                .email("dlsdn857758@naver.com")
                .memberGrade(MemberGrade.NORMAL)
                .tel("010-1111-1111")
                .build();
        return memberRepository.save(member);
   }
    //사전 게시글 등록
   private Board saveBoardData(Member member){
       Board board = Board.builder()
               .subject("제목입니다.")
               .content("내용입니다.")
               .readCount(0)
               .member(member)
               .build();

       return boardRepository.save(board);
   }

   //사전 댓글 등록
   private Comment saveCommentData(Member member , Board board){
       Comment comment = Comment.builder()
               .writer(member.getUserId())
               .content("댓글 내용입니다.")
               .member(member)
               .board(board)
               .build();

       return commentRepository.save(comment);
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
    @DisplayName("댓글 등록 - 성공 케이스")
    void commentRegistry_success() throws Exception {
       //given
        Member saveMember = saveMemberData();
        Long boardId = saveBoardData(saveMember).getId();
        LoginResponse loginResponse = createdLoginResponse(saveMember);

        httpSession.setAttribute(LOGIN_MEMBER,loginResponse);

        WriteContentDto writeContentDto = new WriteContentDto();
        writeContentDto.setContent("내용입니다.");

        //expected
        mockMvc.perform(RestDocumentationRequestBuilders.post("/comments/{boardId}",boardId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .session(httpSession)
                .content(objectMapper.writeValueAsString(writeContentDto))
        )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(jsonPath("$.commentId").exists())
                .andExpect(jsonPath("$.memberId").value(saveMember.getId()))
                .andExpect(jsonPath("$.boardId").value(boardId))
                .andExpect(jsonPath("$.userId").value(saveMember.getUserId()))
                .andExpect(jsonPath("$._links.comments-list.href").exists())
                .andExpect(jsonPath("$._links.comment-update.href").exists())
                .andExpect(jsonPath("$._links.comment-delete.href").exists())
                .andDo(document("register-comments",
                        pathParameters(
                                parameterWithName("boardId").description("댓글 달린 게시글 식별자 아이디")
                        )
                        ,
                            requestFields(
                                    fieldWithPath("content").description("댓글 내용")
                            ),
                            responseFields(
                                    fieldWithPath("commentId").description("댓글 식별자 아이디"),
                                    fieldWithPath("memberId").description("댓글 작성자 식별자 아이디"),
                                    fieldWithPath("boardId").description("댓글 달린 게시글 식별자 아이디"),
                                    fieldWithPath("userId").description("작성자"),
                                    fieldWithPath("content").description("댓글 내용"),
                                    fieldWithPath("_links.comments-list.href").description("댓글 목록 가져오기"),
                                    fieldWithPath("_links.comment-update.href").description("댓글 수정하기"),
                                    fieldWithPath("_links.comment-delete.href").description("댓글 삭제하기")
                            ),
                        links(
                                linkWithRel("comments-list").description("댓글 목록 가져오기"),
                                linkWithRel("comment-update").description("댓글 수정하기"),
                                linkWithRel("comment-delete").description("댓글 삭제하기")
                        )
                        ))
        ;
        //index페이지에 만들어주고 , profile설정
    }


    //beanValidation 위반.
    @Test
    @DisplayName("댓글 등록 실패- 빈 내용을 보냈을 경우.")
    void commentRegistry_fail() throws Exception {
        //given
        Member saveMember = saveMemberData();
        Long boardId = saveBoardData(saveMember).getId();
        LoginResponse loginResponse = createdLoginResponse(saveMember);

        httpSession.setAttribute(LOGIN_MEMBER,loginResponse);

       WriteContentDto writeContentDto = new WriteContentDto();

        //expected
        mockMvc.perform(RestDocumentationRequestBuilders.post("/comments/{boardId}",boardId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .session(httpSession)
                .content(objectMapper.writeValueAsString(writeContentDto))
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.path").exists())
                .andExpect(jsonPath("$.fieldErrors.content.messages[0]").value( "content은 필수값 입니다."))
                .andExpect(jsonPath("$.fieldErrors.content.fieldName").value("content"))
                .andExpect(jsonPath("$.fieldErrors.content.rejectedValue").value("값이 들어오지 않음"))

        ;

    }

    //댓글 목록 가져오기
    @Test
    @DisplayName("댓글 목록 가져오기")
    void getCommentList_success() throws Exception {
       //given
        Member member = saveMemberData();
        Board board = saveBoardData(member);
        Long boardId= board.getId();

        Pageable pageable = PageRequest.of(0,3 , Sort.by("id").descending());

        Comment comment1 = saveCommentData(member, board);
        Comment comment2 = saveCommentData(member, board);
        Comment comment3 = saveCommentData(member, board);


        commentRepository.save(comment1);
        commentRepository.save(comment2);
        commentRepository.save(comment3);


        //expected
        mockMvc.perform(RestDocumentationRequestBuilders.get("/comments/{boardId}",boardId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .queryParam("page","0")
                .queryParam("size","3")
                .queryParam("sort","id,desc")
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("content.length()").value(3))
                .andExpect(jsonPath("content[0].boardId").exists())
                .andExpect(jsonPath("content[0].commentId").exists())
                .andExpect(jsonPath("content[0].memberId").exists())
                .andExpect(jsonPath("content[0].userId").exists())
                .andExpect(jsonPath("content[0].content").exists())
                .andExpect(jsonPath("pageInfo.length()").value(1))
                .andDo(document("find-commentsList",
                        pathParameters(
                                parameterWithName("boardId").description("게시글 식별자")
                        ),
                        requestParameters(
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("페이지당 가져올 게시글 갯수"),
                                parameterWithName("sort").description("정렬 조건")
                        ),
                        responseFields(
                                fieldWithPath("content[].commentId").description("댓글 식별자"),
                                fieldWithPath("content[].memberId").description("회원 식별자"),
                                fieldWithPath("content[].boardId").description("게시글 식별자"),
                                fieldWithPath("content[].userId").description("댓글 작성자"),
                                fieldWithPath("content[].content").description("댓글 본문"),
                                fieldWithPath("pageInfo[].pageNum").description("페이지 번호"),
                                subsectionWithPath("pageInfo[].links[]").description("해당 페이지 번호 링크")
                        )
                        ))
        ;
    }


    //게시글 수정
    @Test
    @DisplayName("게시글 수정")
    void updateComment() throws Exception{
        //given
        Member member = saveMemberData();
        Board board = saveBoardData(member);
        Long boardId= board.getId();
        //댓글 저장
        Comment comment = saveCommentData(member,board);
        Comment saveComments = commentRepository.save(comment);

        //세션 준비
        LoginResponse loginResponse = createdLoginResponse(member);
        httpSession.setAttribute(LOGIN_MEMBER,loginResponse);

        //변경될 내용 dto
        WriteContentDto writeContentDto = new WriteContentDto();
        writeContentDto.setContent("바뀐 내용입니다.");

        //expected
        mockMvc.perform(patch("/comments/{boardId}/{id}",boardId,saveComments.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(writeContentDto))
                .session(httpSession)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentId").exists())
                .andExpect(jsonPath("$.memberId").exists())
                .andExpect(jsonPath("$.boardId").exists())
                .andExpect(jsonPath("$.userId").exists())
                .andExpect(jsonPath("$.content").value(writeContentDto.getContent()))
                .andExpect(jsonPath("$._links").exists())
                .andDo(document("update-comment",
                        pathParameters(
                                parameterWithName("boardId").description("게시글 식별자"),
                                parameterWithName("id").description("댓글 식별자")
                        ),
                        requestFields(
                                fieldWithPath("content").description("업데이트될 댓글 내용")
                        ),
                        responseFields(
                            fieldWithPath("commentId").description("댓글 식별자"),
                            fieldWithPath("memberId").description("회원 식별자"),
                            fieldWithPath("boardId").description("게시글 식별자"),
                            fieldWithPath("userId").description("댓글 작성자"),
                            fieldWithPath("content").description("댓글 본문"),
                                subsectionWithPath("_links").description("댓글 목록 이동 링크")

                        )
                        ))

        ;
    }

    //댓글삭제
    @Test
    @DisplayName("댓글 삭제")
    void commentDelete() throws Exception {
        //given
        Member member = saveMemberData();
        Board board = saveBoardData(member);
        Long boardId= board.getId();
        //댓글 저장
        Comment comment = saveCommentData(member,board);
        Comment saveComments = commentRepository.save(comment);

        //세션 준비
        LoginResponse loginResponse = createdLoginResponse(member);
        httpSession.setAttribute(LOGIN_MEMBER,loginResponse);

        //expected
        mockMvc.perform(delete("/comments/{boardId}/{id}",boardId,saveComments.getId())
                        .accept(MediaTypes.HAL_JSON)
                        .session(httpSession)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.comments-list.href").exists())
                .andDo(document("delete-comment",
                        pathParameters(
                                parameterWithName("boardId").description("게시글 식별자"),
                                parameterWithName("id").description("댓글 식별자")
                        ),
                        links(
                                linkWithRel("comments-list").description("댓글 목록 이동 링크")
                        )

                        ))
        ;

    }


}