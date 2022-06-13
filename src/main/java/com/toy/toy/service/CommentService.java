package com.toy.toy.service;

import com.toy.toy.dto.CommentDto;
import com.toy.toy.entity.Board;
import com.toy.toy.entity.Comment;
import com.toy.toy.entity.Member;
import com.toy.toy.repository.BoardRepository;
import com.toy.toy.repository.CommentRepository;
import com.toy.toy.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    //댓글 등록
    @Transactional
    public Long registry(CommentDto commentDto, Long memberId , Long boardId){

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalStateException());

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalStateException());

        Comment comment = Comment.builder()
                .content(commentDto.getContent())
                .writer(member.getUserId())
                .member(member)
                .board(board)
                .build();

        commentRepository.save(comment);

        return comment.getId();
    }


    //게시글당 댓글 조회
    public List<CommentDto> findAll(Long boardId){
        List<Comment> comments = commentRepository.findByBoardId(boardId);

        if(comments.isEmpty()){
            return Collections.emptyList();
        }

        return comments.stream()
                .map(c -> CommentDto.builder()
                        .id(c.getId())
                        .content(c.getContent())
                        .writer(c.getWriter())
                        .build())
                .collect(Collectors.toList());
    }


    //게시글 수정
    public void update(CommentDto commentDto){
        Comment comment = commentRepository.findById(commentDto.getId())
                                .orElseThrow(() -> new IllegalArgumentException());

        comment.updateComment(comment.getContent());
    }

    //게시글 삭제
    public void delete(Long commentId){
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException());

        commentRepository.delete(comment);
    }
}
