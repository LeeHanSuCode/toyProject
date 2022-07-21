package com.toy.toy.dto.validationDto;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter @Setter
@NoArgsConstructor
public class LoginMemberDto {

    @Builder
    public LoginMemberDto(String userId , String password){
        this.userId = userId;
        this.password = password;
    }

    @NotBlank
    private String userId;

    @NotBlank
    private String password;
}
