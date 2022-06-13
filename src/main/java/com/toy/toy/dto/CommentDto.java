package com.toy.toy.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CommentDto {
    @Builder
    public CommentDto(Long id , String content , String writer){
        this.id = id;
        this.content = content;
        this.writer = writer;
    }

    private Long id;

    private String content;

    private String writer;

}
