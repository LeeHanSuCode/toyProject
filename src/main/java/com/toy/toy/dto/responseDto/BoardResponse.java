package com.toy.toy.dto.responseDto;

import com.toy.toy.entity.Board;
import lombok.Builder;


import java.util.List;

@Builder
public class BoardResponse {

    public BoardResponse(Board board , List<FilesResponse> filesResponseList){
        this.boardId = board.getId();
        this.subject = board.getSubject();
        this.writer = board.getMember().getUsername();
        this.likeCount = board.getLikeCount();
        this.readCount = board.getReadCount();
        this.filesDtoList = filesResponseList;
        this.isChoice = 0;
    }

    private Long boardId;

    private String subject;

    private String writer;

    private Integer likeCount;

    private Integer readCount;

    private String boardContent;

    private List<FilesResponse> filesDtoList;

    //해당 게시글을 보는 회원이 게시글을 눌렀는지 안눌렀는지 여부확인
    private Integer isChoice;
}
