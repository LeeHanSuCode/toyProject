package com.toy.toy.entity;

import com.toy.toy.entity.mappedEntity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity @Getter
@NoArgsConstructor
public class Likes {

    //좋아요와 싫어요의 표현을 mode로 처리.
    public Likes(Board board , Member member, String mode){
        this.member = member;
        this.board = board;
        this.likeChoice = findMode(mode);
    }

    private LikeChoice findMode(String mode) {
       return mode.equalsIgnoreCase("like") ? LikeChoice.LIKE : LikeChoice.HATE;
    }


    @Id @GeneratedValue
    @Column(name = "LIKES_ID")
    private Long id;


    @Enumerated(EnumType.STRING)
    private LikeChoice likeChoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BOARD_ID")
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @CreatedDate
    private LocalDateTime createDate;
}