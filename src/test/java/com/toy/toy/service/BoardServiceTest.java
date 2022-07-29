package com.toy.toy.service;

import com.toy.toy.controller.exception_controller.exception.BoardNotFoundException;
import com.toy.toy.dto.SearchConditionDto;
import com.toy.toy.dto.validationDto.UpdateBoardDto;
import com.toy.toy.entity.Board;
import com.toy.toy.entity.Files;
import com.toy.toy.entity.Member;
import com.toy.toy.entity.MemberGrade;
import com.toy.toy.repository.BoardRepository;
import com.toy.toy.repository.CommentRepository;
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
import org.springframework.data.domain.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

    @Mock
    private BoardRepository boardRepository;
    @Mock
    private  CommentRepository commentRepository;
    @Mock
    private  FileRepository fileRepository;
    @Mock
   private  FileService fileService;

    @InjectMocks
    private BoardService boardService;

    private List<MultipartFile> filesList = new ArrayList<>();
    private Member member;
    private Board board;

    @BeforeEach
    void setUp_data() throws IOException {

        member  = member.builder()
                .username("홍길동")
                .userId("hslee0000")
                .memberGrade(MemberGrade.NORMAL)
                .password("wmf123!@#")
                .email("dhfl111@naver.com")
                .tel("000-0000-0000")
                .build();

        board = board.builder()
                .id(1L)
                .subject("제목입니다.")
                .content("내용입니다.")
                .readCount(0)
                .member(member)
                .build();

        String writerData = "str1,str2,str3,str4";
        MultipartFile multipartFile1 = new MockMultipartFile("files", "파일명.csv", "text/plain", writerData.getBytes(StandardCharsets.UTF_8));
        MultipartFile multipartFile2 = new MockMultipartFile("files", "파일명.csv", "text/plain", writerData.getBytes(StandardCharsets.UTF_8));
        filesList.add(multipartFile1);
        filesList.add(multipartFile2);

    }

    private List<Files> createFileEntityData(Long id1 , Long id2){
        List<Files> filesEntityList = new ArrayList<>();

        Files file1 = Files.builder()
                .id(id1)
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

    //게시글 등록
    @Test
    @DisplayName("게시글 등록 성공 - 파일이 있을 경우")
    void register_withFileData(){
        //given
        List<Files> fileEntityData = createFileEntityData(1L, 2L);

        doReturn(board).when(boardRepository).save(board);
        doReturn(fileEntityData).when(fileService).save(filesList, board);

        //when
        Board registeredBoard = boardService.register(board, filesList);

        //then
        assertThat(registeredBoard.getSubject()).isEqualTo(board.getSubject());
        assertThat(registeredBoard.getContent()).isEqualTo(board.getContent());

        verify(boardRepository , times(1)).save(board);
        verify(fileService, times(1)).save(filesList,board);

    }

    @Test
    @DisplayName("게시글 등록 성공 - 파일이 없을 경우")
    void register_noFileData(){
        //given
        List<MultipartFile> multipartFiles = new ArrayList<>();
        doReturn(board).when(boardRepository).save(board);

        //when
        Board registeredBoard = boardService.register(board, multipartFiles);

        //then
        assertThat(registeredBoard.getSubject()).isEqualTo(board.getSubject());
        assertThat(registeredBoard.getContent()).isEqualTo(board.getContent());

        verify(boardRepository , times(1)).save(board);
        verify(fileService, times(0)).save(multipartFiles,board);
    }


    @Test
    @DisplayName("게시글 목록 보기")
    void findAll_board(){
        //given
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdDate").descending());

        SearchConditionDto searchCond = SearchConditionDto.builder()
                .userId("hslee0000")
                .build();

        List<Board> boards = new ArrayList<>();
        boards.add(board);

        Page<Board> pageBoard = new PageImpl<>(boards);

        doReturn(pageBoard).when(boardRepository).findByCond(pageable , searchCond);

        //when
        Page<Board> responsePageBoard = boardService.findAll(searchCond, pageable);
        List<Board> responseBoard = responsePageBoard.getContent();

        //then
        assertThat(responseBoard.size()).isEqualTo(boards.size());
        assertThat(responseBoard.get(0).getSubject()).isEqualTo(board.getSubject());
        assertThat(responseBoard.get(0).getContent()).isEqualTo(board.getContent());
        assertThat(responsePageBoard.getSize()).isEqualTo(1);
        assertThat(responsePageBoard.getNumber()).isEqualTo(0);

        verify(boardRepository , times(1)).findByCond(pageable , searchCond);

    }


    @Test
    @DisplayName("게시글 보기 - 성공 케이스")
    void findBoard_success(){
        //given
        Long boardId = board.getId();
        doReturn(Optional.of(board)).when(boardRepository).findBoardWithMember(boardId);

        //when
        Board findBoard = boardService.findById(boardId);

        //then
        assertThat(findBoard.getReadCount()).isEqualTo(1);
        assertThat(findBoard.getSubject()).isEqualTo(board.getSubject());
        assertThat(findBoard.getId()).isEqualTo(boardId);

        verify(boardRepository , times(1)).findBoardWithMember(boardId);
    }

    @Test
    @DisplayName("게시글이 존재하지 않을 경우")
    void findBoard_fail(){
        //given
        Long boardId = board.getId();
        doReturn(Optional.empty()).when(boardRepository).findBoardWithMember(boardId);

        //when , then
        assertThatThrownBy(() -> boardService.findById(boardId))
                .isInstanceOf(BoardNotFoundException.class)
                .hasMessageContaining("존재하지 않는 게시글입니다.");

        verify(boardRepository , times(1)).findBoardWithMember(boardId);
    }


    @Test
    @DisplayName("게시글 수정 - 성공하는 경우")
    void updateBoard_success(){
        //given
        Long boardId = board.getId();
        List<MultipartFile> newFiles = filesList;

        UpdateBoardDto updateBoardDto = new UpdateBoardDto();
        updateBoardDto.setSubject("바뀐 제목");
        updateBoardDto.setBoardContent("바뀐 내용");
        updateBoardDto.setAliveFiles(Arrays.asList(1L,2L,3L));

        doReturn(Optional.of(board)).when(boardRepository).findById(boardId);

        //when
        Board updatedBoard = boardService.update(updateBoardDto, newFiles, boardId);

        //then
        assertThat(updatedBoard.getSubject()).isEqualTo(updateBoardDto.getSubject());
        assertThat(updatedBoard.getContent()).isEqualTo(updateBoardDto.getBoardContent());

        verify(boardRepository , times(1)).findById(boardId);
        verify(fileService , times(1)).updatedByBoard(updateBoardDto ,updatedBoard,newFiles);
    }



    @Test
    @DisplayName("게시글 수정 - 게시글이 없는 경우")
    void updateBoard_fail(){
        //given
        Long boardId = board.getId();
        List<MultipartFile> newFiles = filesList;

        UpdateBoardDto updateBoardDto = new UpdateBoardDto();
        updateBoardDto.setSubject("바뀐 제목");
        updateBoardDto.setBoardContent("바뀐 내용");
        updateBoardDto.setAliveFiles(Arrays.asList(1L,2L,3L));

        doReturn(Optional.empty()).when(boardRepository).findById(boardId);

        //when , then
        assertThatThrownBy(() -> boardService.update(updateBoardDto,newFiles ,boardId))
                .isInstanceOf(BoardNotFoundException.class)
                .hasMessageContaining("존재하지 않는 게시글 입니다.");

        verify(boardRepository , times(1)).findById(boardId);
        verify(fileService , times(0)).updatedByBoard(updateBoardDto ,board,newFiles);
    }


    @Test
    @DisplayName("게시글 삭제 - 성공")
    void deleteBoard_success(){
        //given
        Long boardId = board.getId();
        doReturn(Optional.of(board)).when(boardRepository).findById(boardId);

        //when
        boardService.delete(boardId);

        //then
        verify(boardRepository,times(1)).findById(boardId);
        verify(commentRepository,times(1)).deleteByBoard(boardId);
        verify(fileRepository,times(1)).deleteByBoard(boardId);
        verify(boardRepository,times(1)).delete(board);
    }


    @Test
    @DisplayName("게시글 삭제 - 존재하지 않는 회우너")
    void deleteBoard_fail(){
        //given
        Long boardId = board.getId();
        doReturn(Optional.empty()).when(boardRepository).findById(boardId);

        //when , then
        assertThatThrownBy(() -> boardService.delete(boardId))
                .isInstanceOf(BoardNotFoundException.class)
                        .hasMessageContaining("존재하지 않는 게시글 입니다.");

        //then
        verify(boardRepository,times(1)).findById(boardId);
        verify(commentRepository,times(0)).deleteByBoard(boardId);
        verify(fileRepository,times(0)).deleteByBoard(boardId);
        verify(boardRepository,times(0)).delete(board);
    }


    @Test
    @DisplayName("회원 탈퇴로 인한 게시글 삭제")
    void deleteBoard_byMember(){
        //given
        List<Board> boardList = new ArrayList<>();
        boardList.add(board);

        doReturn(boardList).when(boardRepository).findByMember(member.getId());

        //when
        boardService.deleteByMember(member);

        //then
        verify(boardRepository , times(1)).findByMember(member.getId());
        verify(commentRepository , times(1)).deleteByBoardByMember(any());
        verify(fileRepository , times(1)).deleteByBoardByMember(any());
        verify(boardRepository , times(1)).deleteByMemberId(member.getId());

    }

}