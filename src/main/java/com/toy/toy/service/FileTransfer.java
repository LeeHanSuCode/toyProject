package com.toy.toy.service;
import com.toy.toy.entity.Board;
import com.toy.toy.entity.Files;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;


@Slf4j
@Component
public class FileTransfer {

    @Value("${file.dir}")
    private String fileDir;

    //파일 저장 경로 얻어오기
    public String getFullPath(String filename){

        return fileDir + filename;
    }

    //MultipartFile -> Files
    @Transactional
    public List<Files> changeFiles(List<MultipartFile> multipartList , Board board){
        return multipartList.stream()
                    .map(f -> changeFileFormat(f , board))
                    .collect(Collectors.toList());
    }

    //단건 MultipartFile -> Files.
    public Files changeFileFormat(MultipartFile multipartFile , Board board){

        String originalFilename = multipartFile.getOriginalFilename();

        String storeFilename = createStoreFileName(originalFilename);

        try {
            multipartFile.transferTo(new File(getFullPath(storeFilename)));
        }catch (IOException e){
            log.error("changeFileFormat occur!");
            throw new IllegalStateException();
        }

        return Files.builder()
                .uploadFilename(originalFilename)
                .serverFilename(storeFilename)
                .board(board)
                .build();

    }

    //서버에 저장할 파일명 생성
    private String createStoreFileName(String originalFilename){
        String uuid = UUID.randomUUID().toString();
        String ext = extract(originalFilename);
        return uuid + "." + ext;
    }

    //확장자 추출
    private String extract(String originalFilename){
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos+1);
    }



}
