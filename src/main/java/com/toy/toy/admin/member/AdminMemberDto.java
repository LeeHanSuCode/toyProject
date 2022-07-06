package com.toy.toy.admin.member;


import com.toy.toy.entity.Member;
import com.toy.toy.entity.MemberGrade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
public class AdminMemberDto {

    public AdminMemberDto(){};

    public AdminMemberDto(Member member , List<Long> boardIds){
        this.id = member.getId();
        this.userId = member.getUserId();
        this.username = member.getUsername();
        this.memberGrade = member.getMemberGrade();
        this.email = member.getEmail();
        this.tel = member.getTel();
        this.boardId = boardIds;
    }


    private Long id;
    private String username;
    private String userId;
    private MemberGrade memberGrade;
    private String email;
    private String tel;
    private List<Long> boardId;



}
