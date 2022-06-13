package com.toy.toy.service;
import com.toy.toy.entity.Files;
import com.toy.toy.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class FileService {

    private final FileRepository fileRepository;

    @Value("${file.dir}")
    private String fileDir;

    //파일 저장 경로 얻어오기
    public String getFullPath(String filename){

        return fileDir + filename;
    }

    //파일 형식 변환하고 저장.
    @Transactional
    public void changeFilesAndSave(List<MultipartFile> multipartList){
        multipartList.stream()
                .forEach(f -> fileRepository.save(changeFileFormat(f)));

    }

    //단건 파일 형식 변환.
    public Files changeFileFormat(MultipartFile multipartFile){

        String originalFilename = multipartFile.getOriginalFilename();

        String storeFilename = createStoreFileName(originalFilename);

        try {
            multipartFile.transferTo(new File(getFullPath(storeFilename)));
        }catch (IOException e){
            log.error("changeFileFormat occur!");
            throw new IllegalStateException();
        }

        //체크
        return new Files(originalFilename, storeFilename);
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
