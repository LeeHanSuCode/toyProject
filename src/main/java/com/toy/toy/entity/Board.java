package com.toy.toy.entity;


import com.toy.toy.entity.mappedEntity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Board extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "board_id")
    private Long id;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;


    @OneToMany(mappedBy = "board")
    private List<Comment> comments = new ArrayList<>();

}
