package com.toy.toy.entity;

import com.toy.toy.entity.mappedEntity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class Files extends BaseEntity {

    @Builder
    public Files(Long id , String uploadFilename , String serverFilename , Board board){
        this.id = id;
        this.uploadFilename = uploadFilename;
        this.serverFilename = serverFilename;
        addBoardToFile(board);
    }



    @Id @GeneratedValue
    @Column(name = "FILES_ID")
    private Long id;

    private String uploadFilename;

    private String serverFilename;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BOARD_ID")
    private Board board;


    //연관관계 메서드
    public void addBoardToFile(Board board){
        this.board = board;
        board.getFiles().add(this);
    }
}
