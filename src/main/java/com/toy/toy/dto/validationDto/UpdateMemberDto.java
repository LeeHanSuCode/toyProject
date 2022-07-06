package com.toy.toy.dto.validationDto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateMemberDto {


    private String userId;

    private String ssn;

    private String username;

    private String password;

    private String password2;

    private String email;

    private String tel;


}
