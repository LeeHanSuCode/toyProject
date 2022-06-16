package com.toy.toy.dto;

public class LikeDto {

    public LikeDto(Long id , Integer likeCount){
        this.id = id;
        this.likeCount = likeCount;
    }

    private Long id;

    private Integer likeCount;

    private Long memberId;

    private Long boardId;
}
