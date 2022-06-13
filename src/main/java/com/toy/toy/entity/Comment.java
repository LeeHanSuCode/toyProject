package com.toy.toy.entity;

import com.toy.toy.entity.mappedEntity.BaseEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Comment extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "comments_id")
    private Long id;

    private String writer;

    private String content;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;


    public void updateComment(String content){
        this.content = content;
    }
}
