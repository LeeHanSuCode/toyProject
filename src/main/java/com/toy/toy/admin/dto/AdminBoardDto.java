package com.toy.toy.admin.dto;

import com.toy.toy.dto.CommentDto;
import com.toy.toy.dto.FilesDto;
import com.toy.toy.entity.Board;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.repository.query.Param;

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
    private List<FilesDto> filesDtos;
    private List<CommentDto> commentDtos;
}
