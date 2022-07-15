package com.toy.toy.service;



import com.toy.toy.controller.exception_controller.exception.BoardNotFoundException;
import com.toy.toy.dto.validationDto.UpdateBoardDto;
import com.toy.toy.entity.Board;
import com.toy.toy.entity.Member;
import com.toy.toy.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;




@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BoardService {


    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final FileRepository fileRepository;

    //게시글 등록
    @Transactional
    public Board register(Board board){
        return boardRepository.save(board);
    }


    //게시글 목록 보기
    public Page<Board> findAll(Pageable pageable){
        return boardRepository.findAllWithMember(pageable);
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
    public Board update(UpdateBoardDto boardUpdateDto){
        Board findBoard = boardRepository.findById(boardUpdateDto.getBoardId())
                            .orElseThrow(() -> new BoardNotFoundException("존재하지 않는 게시글 입니다."));
        //파일 내용 수정
       findBoard.changeContent(boardUpdateDto.getBoardContent());

        return findBoard;
    }


    @Transactional
    public Board updateAddLikeCount(Long boardId){
        Board findBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException("존재하지 않는 게시글입니다."));

        findBoard.addLikeCount();

        return findBoard;
    }

    @Transactional
    public Board subtractLikeCount(Long boardId){
        Board findBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException("존재하지 않는 게시글입니다."));

        findBoard.subtractLikeCount();

        return findBoard;
    }


    //게시글 삭제
    @Transactional
    public void delete(Long boardId){
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException("존재하지 않는 게시글 입니다."));


        boardRepository.delete(board);
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
