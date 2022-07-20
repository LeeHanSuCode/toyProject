package com.toy.toy.dto.responseDto;

import com.toy.toy.entity.Comment;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CommentResponse {

    @Builder
    public CommentResponse(Long commentId , Long memberId , Long boardId , String userId , String content){
        this.commentId = commentId;
        this.memberId = memberId;
        this.boardId = boardId;
        this.userId = userId;
        this.content = content;
    }
    private Long commentId;
    private Long memberId;
    private Long boardId;
    private String userId;
    private String content;

    public CommentResponse changeCommentResponse(Comment comment){
        return CommentResponse
                .builder()
                .commentId(comment.getId())
                .memberId(comment.getMember().getId())
                .boardId(comment.getBoard().getId())
                .content(comment.getContent())
                .build();

    }
}
