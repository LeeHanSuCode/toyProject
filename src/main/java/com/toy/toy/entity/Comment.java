package com.toy.toy.entity;

import com.toy.toy.entity.mappedEntity.BaseEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

    @Builder
    public Comment(Long id ,String writer , String content , Member member , Board board){
        this.id = id;
        this.writer = writer;
        this.content = content;
        this.member = member;
        this.board = board;
    }

    @Id @GeneratedValue
    @Column(name = "COMMENT_ID")
    private Long id;

    private String writer;

    private String content;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BOARD_ID")
    private Board board;


    public void updateComment(String content){
        this.content = content;
    }
}
