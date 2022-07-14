package com.toy.toy.dto.responseDto;

import com.toy.toy.entity.Files;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
public class FilesResponse {

    @Builder
    public FilesResponse(Long id , String uploadFilename , String serverFilename){
        this.id = id;
        this.uploadFilename = uploadFilename;
        this.serverFilename =serverFilename;
    }

    public FilesResponse(Files files){
        this.id = files.getId();
        this.uploadFilename = files.getUploadFilename();
        this.serverFilename = files.getServerFilename();
    }

    private Long id;
    private String uploadFilename;
    private String serverFilename;



}
