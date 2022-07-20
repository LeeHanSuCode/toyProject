package com.toy.toy.entity;


import com.toy.toy.entity.mappedEntity.BaseEntity;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board extends BaseEntity {

    @Builder
    public Board(String subject , String content , Integer readCount , Member member){
        this.subject = subject;
        this.content  =content;
        this.readCount = readCount;
        this.member = member;
    }


    @Id @GeneratedValue
    @Column(name = "BOARD_ID")
    private Long id;

    private String subject;

    private String content;

    private Integer readCount;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;


    @OneToMany(mappedBy = "board")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "board")
    private List<Files> files = new ArrayList<>();


    //조회수 증가
    public void addReadCount(){
        this.readCount++;
    }

    //게시글 수정
    public void updateBoard(String content , String subject){
        if(content != null && !content.isBlank()){
            this.content = content;
        }

        if(subject != null && !subject.isBlank()){
            this.subject = subject;
        }

    }

}
