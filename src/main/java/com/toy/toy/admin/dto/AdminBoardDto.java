package com.toy.toy.admin.dto;

import com.toy.toy.dto.CommentDto;
import com.toy.toy.dto.responseDto.FilesResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class AdminBoardDto {

    private Long id;
    private String subject;
    private Integer readCount;
    private Integer likeCount;
    private String writer;

    //상세보기에서는 아래 필드까지 필요
    private List<FilesResponse> filesDtos;
    private List<CommentDto> commentDtos;
}
