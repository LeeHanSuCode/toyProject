package com.toy.toy.entity;

import com.toy.toy.entity.mappedEntity.BaseEntity;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
public class Like extends BaseEntity {


    public Like(Board board , Member member){
        this.member = member;
        this.board = board;
        this.likeChoice = LikeChoice.NOT;
    }

    @Id @GeneratedValue
    @Column(name = "LIKE_ID")
    private Long id;


    private LikeChoice likeChoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BOARD_ID")
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;
}
