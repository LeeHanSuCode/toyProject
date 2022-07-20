package com.toy.toy.dto.responseDto;

import com.toy.toy.entity.Files;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
public class FilesResponse {

    @Builder
    public FilesResponse(Long id , String uploadFilename){
        this.id = id;
        this.uploadFilename = uploadFilename;
    }

    public FilesResponse(Files files){
        this.id = files.getId();
        this.uploadFilename = files.getUploadFilename();
    }

    private Long id;
    private String uploadFilename;



}
