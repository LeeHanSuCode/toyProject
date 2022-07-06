package com.toy.toy.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;


@Getter @Builder
public class MemberResponse {

    private String username;
    private String userId;
    private String email;
    private String tel;
    private LocalDateTime createdDate;



}
