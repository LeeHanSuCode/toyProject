package com.toy.toy.dto;

import lombok.Builder;
import lombok.Getter;


@Getter
public class FilesDto {

    @Builder
    public FilesDto(Long id , String uploadFilename , String serverFilename){
        this.id = id;
        this.uploadFilename = uploadFilename;
        this.serverFilename =serverFilename;
    }

    private Long id;
    private String uploadFilename;
    private String serverFilename;
}
