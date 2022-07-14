package com.toy.toy.dto.validationDto;

import com.toy.toy.entity.Board;
import com.toy.toy.entity.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter @Setter
@Builder
public class WriteBoardDto {

    private String subject;

    private String boardContent;

    private List<MultipartFile> files;


    public Board changeEntity(Member member) {
       return Board.builder()
                .subject(this.subject)
                .content(this.boardContent)
                .member(member)
                .readCount(0)
                .likeCount(0)
               .build()
               ;
    }
}
