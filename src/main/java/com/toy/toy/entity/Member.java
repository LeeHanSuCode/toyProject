package com.toy.toy.entity;

import com.toy.toy.entity.mappedEntity.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Member extends BaseEntity{



    @Id @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;

    private String username;

    private String userId;


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
        if(username != null){
            this.username = username;
        }
        if(password != null){
            this.password = password;
        }
        if(email != null){
            this.email = email;
        }
        if(tel != null){
            this.tel = tel;
        }

    }



}
