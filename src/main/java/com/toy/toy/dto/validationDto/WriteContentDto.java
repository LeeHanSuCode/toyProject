package com.toy.toy.dto.validationDto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Setter @Getter
public class WriteContentDto {


    @NotBlank
    private String content;

}
