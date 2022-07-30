package com.toy.toy.service;

import com.toy.toy.controller.exception_controller.exception.CommentNotFoundException;
import com.toy.toy.dto.responseDto.CommentResponse;
import com.toy.toy.entity.Board;
import com.toy.toy.entity.Comment;
import com.toy.toy.entity.Member;
import com.toy.toy.repository.BoardRepository;
import com.toy.toy.repository.CommentRepository;
import com.toy.toy.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    //댓글 등록
    public Comment registry (Long memberId , Long boardId,String content){

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalStateException());

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalStateException());

        Comment comment = Comment.builder()
                .content(content)
                .writer(member.getUserId())
                .member(member)
                .board(board)
                .build();

       return commentRepository.save(comment);
    }


    //게시글당 댓글 조회 (이건 어차피 비동기 통신으로 처리)
    @Transactional(readOnly = true)
    public Page<CommentResponse> findAll(Long boardId , Pageable pageable) {
        Page<CommentResponse> responses = commentRepository.findByBoardId(boardId, pageable)
                .map(c -> CommentResponse.builder()
                        .commentId(c.getId())
                        .memberId(c.getMember().getId())
                        .boardId(boardId)
                        .content(c.getContent())
                        .userId(c.getWriter())
                        .build());

        return responses;
    }


    //댓글 수정
    public Comment updateComment(Long commentId , String content){
        Comment findComment = commentRepository.findById(commentId)
                                .orElseThrow(() -> new CommentNotFoundException("존재하지 않는 댓글 입니다."));

        findComment.updateComment(content);

        return findComment;
    }


    //게시글 삭제
    public Long delete(Long commentId){
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("존재하지 않는 댓글 입니다."));

        commentRepository.delete(comment);

        return commentId;
    }

    public void deletedByBoard(Long boardId){
        commentRepository.deleteByBoard(boardId);
    }
}
