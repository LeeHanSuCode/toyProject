package com.toy.toy.service;



import com.toy.toy.dto.BoardWriteDto;
import com.toy.toy.entity.Board;
import com.toy.toy.entity.Member;
import com.toy.toy.repository.BoardRepository;
import com.toy.toy.repository.CommentRepository;
import com.toy.toy.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final FileRepository fileRepository;
    private final FileService fileService;


    //게시글 등록
    public Long register(BoardWriteDto boardWriteDto){
        Board board = Board.builder()
                .subject(boardWriteDto.getSubject())
                .content(boardWriteDto.getSubject())
                .readCount(0)
                .likeCount(0)
                .build();

        //파일 저장
       if(!boardWriteDto.getFiles().isEmpty()){
           fileService.changeFilesAndSave(boardWriteDto.getFiles());
        }

        boardRepository.save(board);

        return board.getId();
    }

    //게시글 목록 보기

    //게시글 보기



    //게시글 수정


    //게시글 삭제
    public void delete(Long boardId){
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalStateException());
        //댓글 삭제
        commentRepository.deleteByBoard(board);

        boardRepository.delete(board);
    }


    //회원 탈퇴시 게시글 삭제
    public void deleteByMember(Member member){

        //댓글 삭제
        boardRepository.findByMember(member.getId())
                .stream()
                .filter(b -> !b.getComments().isEmpty())
                .forEach(b -> commentRepository.deleteByBoard(b));

        //파일 삭제도 구현 필요

        //게시글 삭제
        boardRepository.deleteByMemberId(member.getId());
    }


}
