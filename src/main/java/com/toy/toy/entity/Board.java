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
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Board extends BaseEntity {

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
    public void changeContent(String content){
        this.content = content;
    }

}
