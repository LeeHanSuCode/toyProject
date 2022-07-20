package com.toy.toy.dto.responseDto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginResponse {
    @Builder
    public LoginResponse(Long id , String userId){
        this.id = id;
        this.userId = userId;
    }

    private Long id;
    private String userId;
}
