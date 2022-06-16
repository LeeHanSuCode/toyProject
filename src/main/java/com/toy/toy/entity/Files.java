package com.toy.toy.entity;

import com.toy.toy.entity.mappedEntity.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class Files extends BaseEntity {

    public Files(String uploadFilename , String serverFilename , Board board){
        this.uploadFilename = uploadFilename;
        this.serverFilename = serverFilename;
        this.board = board;
    }

    @Id @GeneratedValue
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
