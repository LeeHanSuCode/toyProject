package com.toy.toy.entity;

import com.toy.toy.entity.mappedEntity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity @Getter
@NoArgsConstructor
public class Like extends BaseEntity {

    //좋아요와 싫어요의 표현을 mode로 처리.
    public Like(Board board , Member member, String mode){
        this.member = member;
        this.board = board;

        if(mode.equalsIgnoreCase("like")){
            this.likeChoice = LikeChoice.LIKE;
        }else{
            this.likeChoice = LikeChoice.HATE;
        }
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
