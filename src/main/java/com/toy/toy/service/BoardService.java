package com.toy.toy.service;



import com.toy.toy.controller.exception_controller.exception.BoardNotFoundException;
import com.toy.toy.controller.exception_controller.exception.MemberNotFoundException;
import com.toy.toy.dto.SearchConditionDto;
import com.toy.toy.dto.validationDto.UpdateBoardDto;
import com.toy.toy.entity.Board;
import com.toy.toy.entity.Files;
import com.toy.toy.entity.Member;
import com.toy.toy.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;




@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class BoardService {


    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final FileRepository fileRepository;
    private final FileService fileService;

    //게시글 등록
    @Transactional
    public Board register(Board board , List<MultipartFile> filesList){

        Board registeredBoard = boardRepository.save(board);
        log.info("boardId={}", registeredBoard.getId());

        //파일 저장 및 변환
        if(filesList != null && filesList.size() > 0) {
            fileService.save(filesList , registeredBoard);
        }


        return registeredBoard;
    }


    //게시글 목록 보기
    public Page<Board> findAll(SearchConditionDto searchConditionDto, Pageable pageable){

        return boardRepository.findByCond(pageable,searchConditionDto);
    }





    //게시글 보기
    @Transactional
    public Board findById(Long id){

        Board findBoard = boardRepository.findBoardWithMember(id)
                .orElseThrow(() -> new BoardNotFoundException("존재하지 않는 게시글입니다."));
        //조회수 증가
        findBoard.addReadCount();

        return findBoard;
    }


    //게시글 수정
    @Transactional
    public Board update(UpdateBoardDto boardUpdateDto , List<MultipartFile> newFiles , Long boardId){
        Board findBoard = boardRepository.findById(boardId)
                            .orElseThrow(() -> new BoardNotFoundException("존재하지 않는 게시글 입니다."));
        //파일 내용 및 제목 수정.
       findBoard.updateBoard(boardUpdateDto.getBoardContent() , boardUpdateDto.getSubject());



        //파일 변경 내역 확인 후 삭제
       fileService.updatedByBoard(boardUpdateDto , findBoard , newFiles);

        return findBoard;
    }




    //게시글 삭제
    @Transactional
    public void delete(Long boardId){

        Board findBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException("존재하지 않는 게시글 입니다."));

        //댓글 삭제
        commentRepository.deleteByBoard(boardId);
        //파일 삭제
        fileRepository.deleteByBoard(boardId);

        boardRepository.delete(findBoard);
    }


    //회원 탈퇴시 게시글 삭제
    @Transactional
    public void deleteByMember(Member member){

        List<Board> boardList = boardRepository.findByMember(member.getId());

        List<Long> boardIdLists = boardList.stream().map(b -> b.getId()).collect(Collectors.toList());

        //댓글 삭제
        commentRepository.deleteByBoardByMember(boardIdLists);

        //파일 삭제
        fileRepository.deleteByBoardByMember(boardIdLists);

        //게시글 삭제
        boardRepository.deleteByMemberId(member.getId());
    }


}
