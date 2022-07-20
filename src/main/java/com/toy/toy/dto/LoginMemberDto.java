package com.toy.toy.dto;


import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter @Setter
public class LoginMemberDto {

    @NotBlank
    private String userId;

    @NotBlank
    private String password;
}
