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
public class Member extends BaseEntity {



    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String username;

    private String userId;

    private String ssn;

    private MemberGrade memberGrade;

    private String password;

    private String email;

    private String tel;

    @OneToMany(mappedBy = "member")
    private List<Board> boards = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Comment> comments = new ArrayList<>();

    //회원 수정
    public void updateMember(String username , String password , String email , String tel){
            this.username = username;
            this.password = password;
            this.email = email;
            this.tel = tel;
    }

}
