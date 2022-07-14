package com.toy.toy.service;

import com.toy.toy.dto.validationDto.UpdateBoardDto;
import com.toy.toy.dto.responseDto.FilesResponse;
import com.toy.toy.dto.validationDto.WriteBoardDto;
import com.toy.toy.entity.Board;
import com.toy.toy.entity.Files;
import com.toy.toy.repository.BoardRepository;
import com.toy.toy.repository.FileRepository;
import lombok.RequiredArgsConstructor;
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
public class FileService {

    private final FileRepository fileRepository;
    private final BoardRepository boardRepository;
    private final FileTransfer fileTransfer;
    private final EntityManager em;

    //파일 저장 -> 게시글에 의한 저장만 가능.
    @Transactional
    public List<Files> save(List<Files> files){
        List<Files> saveFiles = new ArrayList<>();

         files.stream()
                 .forEach(f -> saveFiles.add(fileRepository.save(f)));

        return saveFiles;
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
    public void update(Board board,UpdateBoardDto boardUpdateDto){

        List<Files> findFiles = fileRepository.findByBoard(board);

        if(findFiles.size() > 0){
            if(boardUpdateDto.getAliveFiles().size() > 0){
                List<Long> existFilesId = findFiles.stream()
                        .map(f -> f.getId())
                        .collect(Collectors.toList());

                List<Long> deleteFilesId = compareFiles(existFilesId, boardUpdateDto.getAliveFiles());

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
