package com.toy.toy.service;

import com.toy.toy.dto.BoardUpdateDto;
import com.toy.toy.dto.FilesDto;
import com.toy.toy.entity.Board;
import com.toy.toy.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;
    private final FileTransfer fileTransfer;
    private final EntityManager em;

    //파일 저장 -> 게시글에 의한 저장만 가능.
    @Transactional
    public void save(List<MultipartFile> files , Board board){
        fileTransfer.changeFiles(files , board)
                .stream()
                .forEach(f ->fileRepository.save(f));
    }



    //파일 수정
    @Transactional
    public List<FilesDto> update(Board board , BoardUpdateDto boardUpdateDto){
        List<FilesDto> existFiles = fileRepository.findByBoard(board);

        List<Long> removeFiles = compareFiles(existFiles, boardUpdateDto.getAliveFiles());

        //파일 삭제
        if(!removeFiles.isEmpty()){
            fileRepository.deleteByIdList(removeFiles);
        }

        //새로운 파일은 저장
        if(boardUpdateDto.getNewFiles().size() > 0 ){
           save(boardUpdateDto.getNewFiles() , board);
        }

        return fileRepository.findByBoard(board);
    }



    //살아남지 못한 파일 반환.
    private List<Long> compareFiles(List<FilesDto> existFiles , List<Long> aliveFiles){

        if(existFiles.size() > 0){

            List<Long> existFilesId = existFiles
                    .stream()
                        .map(f -> f.getId())
                            .collect(Collectors.toList());

          return existFilesId
                    .stream()
                        .filter(id -> aliveFiles.stream().noneMatch(Predicate.isEqual(id)))
                            .collect(Collectors.toList());
        }
        return null;
    }


}
