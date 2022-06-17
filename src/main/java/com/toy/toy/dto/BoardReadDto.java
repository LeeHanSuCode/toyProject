package com.toy.toy.dto;

import lombok.Builder;


import java.util.List;

@Builder
public class BoardReadDto {

    private Long boardId;

    private String subject;

    private String writer;

    private Integer likeCount;

    private Integer readCount;

    private String boardContent;

    private List<FilesDto> filesDtoList;

    //해당 게시글을 보는 회원이 게시글을 눌렀는지 안눌렀는지 여부확인
    private boolean isChoice;
}
