package com.toy.toy.service;

import com.toy.toy.dto.responseDto.FilesResponse;
import com.toy.toy.dto.validationDto.UpdateBoardDto;
import com.toy.toy.entity.Board;
import com.toy.toy.entity.Files;
import com.toy.toy.repository.FileRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class FileServiceTest {


    @Mock
    private FileRepository fileRepository;
    @Spy
    private FileTransfer fileTransfer;
    @InjectMocks
    private FileService fileService;

    private Board board;

    @BeforeEach
    void setUp_data(){
        board = board.builder()
                .id(2L)
                .subject("제목입니다.")
                .content("내용입니다.")
                .readCount(0)
                .build();
    }

    private UpdateBoardDto createUpdateBoardDto(){
        UpdateBoardDto updateBoardDto = new UpdateBoardDto();
        updateBoardDto.setSubject("제목");
        updateBoardDto.setBoardContent("내용");
        updateBoardDto.setAliveFiles(new ArrayList<>());

        return updateBoardDto;
    }

    private List<Files> createFilesData(Long id , Long id2){
        List<Files> filesEntityList = new ArrayList<>();

        Files file1 = Files.builder()
                .id(id)
                .uploadFilename("업로드 이름1")
                .serverFilename("서버 저장 이름1")
                .board(board)
                .build();
        Files file2 = Files.builder()
                .id(id2)
                .uploadFilename("업로드 이름2")
                .serverFilename("서버 저장 이름2")
                .board(board)
                .build();

        filesEntityList.add(file1);
        filesEntityList.add(file2);

        return filesEntityList;
    }

    private List<MultipartFile> createMultipartData(){
        List<MultipartFile> data = new ArrayList<>();
        String writerData = "str1,str2,str3,str4";
        MultipartFile multipartFile1 = new MockMultipartFile("files", "파일명.csv", "text/plain", writerData.getBytes(StandardCharsets.UTF_8));
        MultipartFile multipartFile2 = new MockMultipartFile("files", "파일명.csv", "text/plain", writerData.getBytes(StandardCharsets.UTF_8));
        data.add(multipartFile1);
        data.add(multipartFile2);

        return data;
    }

    @Test
    @DisplayName("file 데이터가 있을 경우 - 저장한 파일 반환")
    void saveFile_data(){
        //given
        List<MultipartFile> filesList = createMultipartData();

        //when
        List<Files> saveFiles = fileService.save(filesList, board);

        //then
        assertThat(filesList.get(0).getOriginalFilename()).isEqualTo(saveFiles.get(0).getUploadFilename());
        assertThat(filesList.get(1).getOriginalFilename()).isEqualTo(saveFiles.get(1).getUploadFilename());
        assertThat(saveFiles.size()).isEqualTo(2);

        verify(fileRepository,times(2)).save(any());
        verify(fileTransfer , times(1)).changeFiles(filesList,board);
    }

    @Test
    @DisplayName("파일 데이터가 없을 경우 - 빈 컬렉션 반환")
    void saveFile_noData(){
        //given
        List<MultipartFile> filesList = new ArrayList<>();

        //when
        List<Files> saveFiles = fileService.save(filesList, board);

        //then
        assertThat(saveFiles.size()).isEqualTo(0);

        verify(fileRepository,times(0)).save(any());
    }

    @Test
    @DisplayName("파일 조회 - 게시글에 등록된 파일이 있을 경우")
    void findFile_boardWithFiles(){
        //given
        List<Files> filesEntityList = createFilesData(4L ,5L);

        doReturn(filesEntityList).when(fileRepository).findByBoard(board);

        //when
        List<FilesResponse> filesResponseList = fileService.getFiles(board);

        //then
        assertThat(filesResponseList.size()).isEqualTo(2);
        assertThat(filesResponseList.get(0).getUploadFilename()).isEqualTo(filesResponseList.get(0).getUploadFilename());
        assertThat(filesResponseList.get(1).getUploadFilename()).isEqualTo(filesResponseList.get(1).getUploadFilename());

        verify(fileRepository,times(1)).findByBoard(board);
    }

    @Test
    @DisplayName("파일 조회 - 게시글에 등록된 파일이 없을 경우")
    void findFile_noBoardWithFiles(){
        //given
        List<Files> filesEntityList = new ArrayList<>();

        doReturn(filesEntityList).when(fileRepository).findByBoard(board);

        //when
        List<FilesResponse> filesResponseList = fileService.getFiles(board);

        //then
        assertThat(filesResponseList.size()).isEqualTo(0);

        verify(fileRepository,times(1)).findByBoard(board);
    }


    @Test
    @DisplayName("파일 수정 - 새로운 파일을 저장하는 경우 , 기존과 새로운 파일은 없음")
    void updateFile_newFileSave(){
        //given
        UpdateBoardDto updateBoardDto = new UpdateBoardDto();
        updateBoardDto.setAliveFiles(new ArrayList<>());

        List<MultipartFile> newFiles = createMultipartData();

        List<Files> findFiles = new ArrayList<>();
        List<Files> changeEntitySaveFiles = createFilesData(6L, 7L);

        doReturn(changeEntitySaveFiles).when(fileTransfer).changeFiles(newFiles,board);
        doReturn(findFiles).when(fileRepository).findByBoard(board);

        //when
        fileService.updatedByBoard(updateBoardDto,board,newFiles);

        //then
        verify(fileRepository ,times(1)).save(changeEntitySaveFiles.get(0));
        verify(fileRepository ,times(1)).save(changeEntitySaveFiles.get(1));
    }



    @Test
    @DisplayName("파일 수정 - 살아남은 파일은 없고 , 기존의 파일이 있는 경우 .")
    void updateFile_noAliveFileExist_existFile(){
        //given
        UpdateBoardDto updateBoardDto = createUpdateBoardDto();
        List<Files> filesEntityList = createFilesData(4L,5L);

        ArrayList<MultipartFile> newFiles = new ArrayList<>();
        doReturn(filesEntityList).when(fileRepository).findByBoard(board);


        //when
        fileService.updatedByBoard(updateBoardDto , board, newFiles);

        //then
        verify(fileRepository , times(1)).findByBoard(board);
        verify(fileRepository , times(1)).delete(filesEntityList.get(0));
        verify(fileRepository , times(1)).delete(filesEntityList.get(1));
    }

    @Test
    @DisplayName("파일 수정 - 파일의 일부만 삭제되고 , 일부는 추가되는 경우")
    void updateFile_noAliveFileExist_existFile2(){
        //given
        UpdateBoardDto updateBoardDto = createUpdateBoardDto();
        List<Long> aliveFileId = Arrays.asList(5L);
        updateBoardDto.setAliveFiles(aliveFileId);

        List<Files> findFileList = createFilesData(4L, 5L);

        List<MultipartFile> newFiles = createMultipartData();

        List<Files> changeEntitySaveFiles = createFilesData(6L, 7L);

        doReturn(findFileList).when(fileRepository).findByBoard(board);
        doReturn(changeEntitySaveFiles).when(fileTransfer).changeFiles(newFiles,board);

        //when
        fileService.updatedByBoard(updateBoardDto ,board , newFiles);

        //then
        verify(fileRepository , times(1)).deleteByIdList(any());
        verify(fileRepository , times(2)).save(any());
    }


    @Test
    @DisplayName("파일 삭제")
    void deleteFile(){

        //when
        fileService.deletedByBoard(board.getId());
        //then
        verify(fileRepository , times(1)).deleteByBoard(board.getId());
    }

}