package com.toy.toy.service;



import com.toy.toy.controller.exception_controller.exception.BoardNotFoundException;
import com.toy.toy.dto.LoginMemberDto;
import com.toy.toy.dto.responseDto.BoardResponse;
import com.toy.toy.dto.validationDto.UpdateBoardDto;
import com.toy.toy.dto.responseDto.FilesResponse;
import com.toy.toy.entity.Board;
import com.toy.toy.entity.LikeChoice;
import com.toy.toy.entity.Likes;
import com.toy.toy.entity.Member;
import com.toy.toy.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


//파일의 경우 변환등 추가 로직이 필요하다 싶으면, fileService에 위임 . (역할 분리를 위함)
//아니다 싶으면 바로 repository접근해서 가져오자.

//고민이다. 굳이 위임이 필요없는 작업들도 FileService에 위임해서 , FileRepository에 대한 의존성 줄이는 게 맞는가
//아니면 지금처럼 , 둘 다 의존하고 편하게 사용하는게 맞는 건가.

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BoardService {


    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final FileService fileService;
    private final FileRepository fileRepository;

    //게시글 등록
    @Transactional
    public Board register(Board board){
        return boardRepository.save(board);
    }


    //게시글 목록 보기
    public Page<BoardResponse> findAll(Pageable pageable){
        return boardRepository.findAll(pageable)
                .map(b -> BoardResponse.builder()
                        .boardId(b.getId())
                        .subject(b.getSubject())
                        .writer(b.getMember().getUserId())
                        .readCount(b.getReadCount())
                        .likeCount(b.getLikeCount())
                        .build());
    }



    //게시글 보기
    @Transactional
    public Board findById(Long id , LoginMemberDto loginMemberDto){

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

       /* //댓글 삭제
        boardList.stream()
                .filter(b -> !b.getComments().isEmpty())
                .forEach(b -> commentRepository.deleteByBoard(b));

        //파일 삭제
        boardList.stream()
                .filter(f -> !f.getFiles().isEmpty())
                        .forEach(f -> fileRepository.deleteByBoard(f));
*/
        //게시글 삭제
        boardRepository.deleteByMemberId(member.getId());
    }


}
