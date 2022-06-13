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

    //싫어요 갯수가 20개를 넘어가면 삭제되게끔 할것인데 이때 , 옵저버 패턴을 한번 사용해 볼까?
    private Integer likeCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;


    @OneToMany(mappedBy = "board")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "board")
    private List<Files> files = new ArrayList<>();

}
