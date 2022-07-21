package com.toy.toy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.toy.toy.entity.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class SearchConditionDto {

    @Builder
    public SearchConditionDto(String userId , String subject){
        this.userId = userId;
        this.subject = subject;
    }

    private String userId;
    private String subject;

}
