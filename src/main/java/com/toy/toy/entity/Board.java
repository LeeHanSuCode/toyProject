package com.toy.toy.entity;


import com.toy.toy.entity.mappedEntity.BaseEntity;
import lombok.*;

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
    @Column(name = "board_id")
    private Long id;

    private String subject;

    private String content;

    private Integer readCount;

    private Integer likeCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
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

    //좋아요 증감
    public void changeLikeCount(String mode){
        int num = (mode.equalsIgnoreCase("like")) ? 1 : -1;

        this.likeCount += num;
    }

    //좋아요 증감 삭제
    public void deleteLikeCount(String mode){
        int num = (mode.equalsIgnoreCase("like")) ? -1 : 1;

        this.likeCount +=num;
    }
}
