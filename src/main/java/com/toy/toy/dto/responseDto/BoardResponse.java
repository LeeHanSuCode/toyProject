package com.toy.toy.dto.responseDto;

import com.toy.toy.entity.Board;
import com.toy.toy.entity.Member;
import lombok.*;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class BoardResponse {

    public BoardResponse(Board board , Member member){
        this.boardId = board.getId();
        this.subject = board.getSubject();
        this.writer = member.getUsername();
        this.boardContent = board.getContent();
        this.readCount = board.getReadCount();

        if(board.getFiles().size() > 0) {
            this.filesDtoList =  board.getFiles()
                    .stream()
                    .map(FilesResponse::new)
                    .collect(Collectors.toList());
        }else{
            this.filesDtoList = Collections.emptyList();
        }
    }

    private Long boardId;

    private String subject;

    private String writer;


    private Integer readCount;

    private String boardContent;

    private List<FilesResponse> filesDtoList = new ArrayList<>();

}
