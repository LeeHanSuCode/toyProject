package com.toy.toy.dto.validationDto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class UpdateBoardDto {

    @NotBlank
    private String subject;

    @NotBlank
    private String boardContent;
    //살아남은 파일 목록.
    private List<Long> aliveFiles = new ArrayList<>();


}
