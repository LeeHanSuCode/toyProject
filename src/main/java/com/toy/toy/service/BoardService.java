package com.toy.toy.service;



import com.toy.toy.dto.BoardReadDto;
import com.toy.toy.dto.BoardUpdateDto;
import com.toy.toy.dto.BoardWriteDto;
import com.toy.toy.dto.FilesDto;
import com.toy.toy.entity.Board;
import com.toy.toy.entity.Like;
import com.toy.toy.entity.Member;
import com.toy.toy.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


//파일의 경우 변환이 필요하다 싶으면, fileService에 위임 .
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
    private final MemberRepository memberRepository;

    //게시글 등록
    @Transactional
    public Long register(BoardWriteDto boardWriteDto , String userId){

        Member findMember = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException());

        Board board = Board.builder()
                .subject(boardWriteDto.getSubject())
                .content(boardWriteDto.getBoardContent())
                .member(findMember)
                .likeCount(0)
                .readCount(0)
                .build();

        //게시글을 먼저 저장
        boardRepository.save(board);

        //파일 로직은 file Service에 위임
        if(!boardWriteDto.getFiles().isEmpty()){
            fileService.save(boardWriteDto.getFiles() , board);
        }

        return board.getId();
    }


    //게시글 목록 보기
    public Page<BoardReadDto> findAll(Pageable pageable){
        return boardRepository.findAll(pageable)
                .map(b -> BoardReadDto.builder()
                        .boardId(b.getId())
                        .subject(b.getSubject())
                        .writer(b.getMember().getUserId())
                        .readCount(b.getReadCount())
                        .build());
    }




    //게시글 보기
    @Transactional
    public BoardReadDto findById(Long id){
        Board findBoard = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException());

        //조회 수 증가
        findBoard.addReadCount();

        List<FilesDto> filesDtoList = fileRepository.findByBoard(findBoard);

        return BoardReadDto.builder()
                .boardId(findBoard.getId())
                .subject(findBoard.getSubject())
                .boardContent(findBoard.getContent())
                .readCount(findBoard.getReadCount()+1)
                .likeCount(findBoard.getLikeCount())
                .writer(findBoard.getMember().getUserId())
                .filesDtoList(filesDtoList)
                .build();
    }


    //게시글 수정
    @Transactional
    public BoardReadDto update(BoardUpdateDto boardUpdateDto){
        Board findBoard = boardRepository.findById(boardUpdateDto.getBoardId())
                            .orElseThrow(() -> new IllegalStateException());

        //파일 내용 수정
       findBoard.changeContent(boardUpdateDto.getBoardContent());

        //파일 변경 내역 위임
        List<FilesDto> updateFiles = fileService.update(findBoard, boardUpdateDto);


        return BoardReadDto.builder()
                .boardId(findBoard.getId())
                .subject(findBoard.getSubject())
                .boardContent(findBoard.getContent())
                .readCount(findBoard.getReadCount())
                .writer(findBoard.getMember().getUserId())
                .filesDtoList(updateFiles)
                .build();
    }



    //게시글 삭제
    @Transactional
    public void delete(Long boardId){
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalStateException());
        //댓글 삭제
        commentRepository.deleteByBoard(board);

        //파일 삭제
        fileRepository.deleteByBoard(board);

        boardRepository.delete(board);
    }


    //회원 탈퇴시 게시글 삭제
    @Transactional
    public void deleteByMember(Member member){

        List<Board> boardList = boardRepository.findByMember(member.getId());

        //댓글 삭제
        boardList.stream()
                .filter(b -> !b.getComments().isEmpty())
                .forEach(b -> commentRepository.deleteByBoard(b));

        //파일 삭제
        boardList.stream()
                .filter(f -> !f.getFiles().isEmpty())
                        .forEach(f -> fileRepository.deleteByBoard(f));

        //게시글 삭제
        boardRepository.deleteByMemberId(member.getId());
    }


}
