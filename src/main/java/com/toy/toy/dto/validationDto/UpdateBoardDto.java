package com.toy.toy.dto.validationDto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class UpdateBoardDto {


    private String subject;

    private String boardContent;
    //살아남은 파일 목록.
    private List<Long> aliveFiles;


}
