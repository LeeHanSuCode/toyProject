package com.toy.toy.dto.responseDto;

import com.toy.toy.entity.Board;
import com.toy.toy.entity.Member;
import lombok.*;


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
        this.filesDtoList = board.getFiles()
                .stream()
                .map(FilesResponse::new)
                .collect(Collectors.toList());

    }

    private Long boardId;

    private String subject;

    private String writer;

    private Integer likeCount;

    private Integer readCount;

    private String boardContent;

    private List<FilesResponse> filesDtoList;

    //해당 게시글을 보는 회원이 게시글을 눌렀는지 안눌렀는지 여부확인
    private String isChoice;

    public void isCheck(String choice){
        this.isChoice = choice;
    }
}
