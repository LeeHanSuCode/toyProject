package com.toy.toy.dto;

import lombok.Builder;


import java.util.ArrayList;
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
}
