package com.toy.toy.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter @Setter
public class BoardUpdateDto {

    private Long boardId;

    private String boardContent;

    //새로 저장한 파일
    private List<MultipartFile> newFiles;

    //살아남은 파일 목록.
    private List<Long> aliveFiles;


}
