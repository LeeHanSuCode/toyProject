package com.toy.toy.service;

import com.toy.toy.dto.validationDto.UpdateBoardDto;
import com.toy.toy.dto.responseDto.FilesResponse;
import com.toy.toy.dto.validationDto.WriteBoardDto;
import com.toy.toy.entity.Board;
import com.toy.toy.entity.Files;
import com.toy.toy.repository.BoardRepository;
import com.toy.toy.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private final FileRepository fileRepository;
    private final FileTransfer fileTransfer;

    //파일 저장 -> 게시글에 의한 저장만 가능.
    @Transactional
    public List<Files> save(List<MultipartFile> filesList , Board board){

        if(filesList!= null && filesList.size() > 0) {
            List<Files> changeFilesList = fileTransfer.changeFiles(filesList, board);

            changeFilesList.stream()
                    .forEach(f -> {
                        fileRepository.save(f);
                    });

            return changeFilesList;
        }

        return Collections.emptyList();
    }


    //파일 조회
    public List<FilesResponse> getFiles(Board board){
        List<Files> files = fileRepository.findByBoard(board);

        if(files.size() > 0){
            return files.stream()
                    .map(FilesResponse::new).collect(Collectors.toList());
        }

        return Collections.emptyList();
    }



    //파일 수정
    @Transactional
    public void updatedByBoard(UpdateBoardDto updateBoardDto ,Board board){

        List<Files> findFiles = fileRepository.findByBoard(board);

        if(findFiles!= null && findFiles.size() > 0){
            //살아남은 파일이 없고 , 기존의 파일이 있는 경우 전부 삭제.
            if(updateBoardDto.getAliveFiles() == null) {
                findFiles.stream()
                        .forEach(f -> fileRepository.delete(f));
            }else {
                //살아남은 파일이 있고 , 기존의 파일이 있는 경우 비교.
                List<Long> existFilesId = findFiles.stream()
                        .map(f -> f.getId())
                        .collect(Collectors.toList());

                List<Long> deleteFilesId = compareFiles(existFilesId, updateBoardDto.getAliveFiles());

                //비교된 결과를 가지고 삭제
                if(deleteFilesId.size() > 0){
                    fileRepository.deleteByIdList(deleteFilesId);
                }
            }
            }
        }


    //살아남지 못한 파일 반환.
    private List<Long> compareFiles(List<Long> existFiles , List<Long> aliveFiles){
          return existFiles
                    .stream()
                        .filter(id -> aliveFiles.stream().noneMatch(Predicate.isEqual(id)))
                            .collect(Collectors.toList());
    }


    //파일 삭제
    public void deletedByBoard(Long boardId){
        fileRepository.deleteByBoard(boardId);
    }

}
