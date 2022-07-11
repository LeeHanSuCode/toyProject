package com.toy.toy.dto.responseDto;

import com.toy.toy.entity.Member;
import lombok.Builder;
import lombok.Getter;


@Getter @Builder
public class MemberResponse {

    private Long id;
    private String username;
    private String userId;
    private String email;
    private String tel;


    public static MemberResponse changeMemberResponse(Member member){
        return  MemberResponse.builder()
                .id(member.getId())
                .userId(member.getUserId())
                .username(member.getUsername())
                .email(member.getEmail())
                .tel(member.getTel())
                .build();
    }


}
