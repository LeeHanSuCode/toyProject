package com.toy.toy.service;

import com.toy.toy.dto.responseDto.CommentResponse;
import com.toy.toy.entity.Board;
import com.toy.toy.entity.Comment;
import com.toy.toy.entity.Member;
import com.toy.toy.entity.MemberGrade;
import com.toy.toy.repository.BoardRepository;
import com.toy.toy.repository.CommentRepository;
import com.toy.toy.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
class CommentServiceTest {
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private BoardRepository boardRepository;
    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private CommentService commentService;

    private Member member;

    private Board board;

    private Comment comment;

    @BeforeEach
    void setUp_data(){

        member  = member.builder()
                .id(1L)
                .username("홍길동")
                .userId("hslee0000")
                .memberGrade(MemberGrade.NORMAL)
                .password("wmf123!@#")
                .email("dhfl111@naver.com")
                .tel("000-0000-0000")
                .build();

        board = board.builder()
                .id(2L)
                .subject("제목입니다.")
                .content("내용입니다.")
                .readCount(0)
                .member(member)
                .build();

        comment = comment.builder()
                .id(3L)
                .member(member)
                .board(board)
                .writer(member.getUserId())
                .content("댓글 내용입니다.")
                .build();

    }

    //게시글 등록.
    @Test
    void registry_success(){
        //given

        String content = "댓글 내용입니다.";

        doReturn(Optional.of(member)).when(memberRepository).findById(member.getId());
        doReturn(Optional.of(board)).when(boardRepository).findById(board.getId());
        doReturn(comment).when(commentRepository).save(any());

        //when
        Comment registry = commentService.registry(member.getId(), board.getId(), content);

        //then
        assertThat(registry).isNotNull();
        assertThat(registry.getContent()).isEqualTo(content);
        assertThat(registry.getMember()).isEqualTo(member);
        assertThat(registry.getBoard()).isEqualTo(board);
        assertThat(registry.getId()).isEqualTo(comment.getId());

        verify(memberRepository,times(1)).findById(member.getId());
        verify(boardRepository, times(1)).findById(board.getId());
        verify(commentRepository, times(1)).save(any());
    }


    @Test
    void findCommentList_success(){
        //given
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdDate").descending());
        Long boardId = board.getId();
        List<Comment> comments = new ArrayList<>();
        comments.add(comment);
        Page<Comment> pageComment = new PageImpl<>(comments);

        doReturn(pageComment).when(commentRepository).findByBoardId(boardId,pageable);

        //when
        Page<CommentResponse> pageComments = commentService.findAll(boardId, pageable);

        //then
        assertThat(pageComments.getContent().get(0).getContent()).isEqualTo(comment.getContent());
        assertThat(pageComments.getContent().get(0).getCommentId()).isEqualTo(comment.getId());
        assertThat(pageComments.getContent().get(0).getUserId()).isEqualTo(comment.getWriter());

        verify(commentRepository , times(1)).findByBoardId(boardId,pageable);
    }


    @Test
    void updateComment(){
        //given
        String content = "바뀐 내용입니다.";
        Long commentId = comment.getId();

        doReturn(Optional.of(comment)).when(commentRepository).findById(commentId);

        //when
        Comment updateComment = commentService.updateComment(commentId, content);

        //then
        assertThat(updateComment.getContent()).isEqualTo(content);
        assertThat(updateComment.getId()).isEqualTo(comment.getId());

        verify(commentRepository , times(1)).findById(commentId);
    }

    @Test
    void deleteComment(){
        //given
        Long commentId = comment.getId();

        doReturn(Optional.of(comment)).when(commentRepository).findById(commentId);

        //when
        Long deleteId = commentService.delete(commentId);

        //then
        assertThat(deleteId).isEqualTo(commentId);
        verify(commentRepository,times(1)).findById(commentId);
    }
}