package com.toy.toy.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter @Setter
@Builder
public class BoardWriteDto {

    private String subject;

    private String boardContent;

    private List<MultipartFile> files;
}
