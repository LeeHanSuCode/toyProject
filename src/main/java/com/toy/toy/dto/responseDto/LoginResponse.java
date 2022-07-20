package com.toy.toy.dto.responseDto;

import com.toy.toy.entity.MemberGrade;
import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginResponse {
    @Builder
    public LoginResponse(Long id , String userId , MemberGrade memberGrade){
        this.id = id;
        this.userId = userId;
        this.memberGrade = memberGrade;
    }

    private Long id;
    private String userId;
    private MemberGrade memberGrade;
}
