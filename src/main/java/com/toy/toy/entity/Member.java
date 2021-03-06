package com.toy.toy.entity;

import com.toy.toy.entity.mappedEntity.BaseEntity;
import lombok.*;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)

public class Member extends BaseEntity{

    @Builder
    public Member(Long id , String username , String userId , MemberGrade memberGrade , String password
    ,String email , String tel){
        this.id = id;
        this.username = username;
        this.userId = userId;
        this.memberGrade = memberGrade;
        this.password = password;
        this.email = email;
        this.tel = tel;
    }


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
        if(username!= null && !username.isBlank()){
            this.username = username;
        }
        if(password != null && !password.isBlank()){
            this.password = password;
        }
        if(email != null && !email.isBlank()){
            this.email = email;
        }
        if(tel != null && !tel.isBlank()){
            this.tel = tel;
        }

    }



}
