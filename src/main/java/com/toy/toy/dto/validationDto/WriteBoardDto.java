package com.toy.toy.dto.validationDto;

import com.toy.toy.entity.Board;
import com.toy.toy.entity.Member;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
public class WriteBoardDto {
    @Builder
    public WriteBoardDto(String subject , String boardContent){
        this.subject = subject;
        this.boardContent = boardContent;
    }

    @NotBlank
    private String subject;

    @NotBlank
    private String boardContent;

    List<MultipartFile> filesList;

    public Board changeEntity(Member member) {
       return Board.builder()
                .subject(this.subject)
                .content(this.boardContent)
                .member(member)
                .readCount(0)
               .build()
               ;
    }
}
