package com.toy.toy.service;



import com.toy.toy.dto.BoardReadDto;
import com.toy.toy.dto.BoardUpdateDto;
import com.toy.toy.dto.BoardWriteDto;
import com.toy.toy.dto.FilesDto;
import com.toy.toy.entity.Board;
import com.toy.toy.entity.Member;
import com.toy.toy.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


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
    private final MemberRepository memberRepository;
    private final LikeRepository likeRepository;

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
                        .likeCount(b.getLikeCount())
                        .build());
    }




    //게시글 보기
    @Transactional
    public BoardReadDto findById(Long memberId ,Long id , String mode){
        Board findBoard = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException());

        //조회 수 증가(수정 작업하고 나서 redirect하는 경우는 조회수를 증가하지 않기 위해 처리)
        if(!mode.equalsIgnoreCase("update")) {
            findBoard.addReadCount();
        }

        //여기 곧 페치 조인으로 한번에 조회하도록 할 것임.(그때 수정 부분도 처리해주자)
        //수정 부분에서 왜 redirect대신 BoardReadDto를 반환하냐면 , 해당 방식으로 게시글 보기로 갈떄는
        //조회수 증가를 방지하기 위함.
        List<FilesDto> filesDtoList = fileRepository.findByBoard(findBoard);

        //회원 정보를 가지고 회원 아이디와 게시글 아이디로 된 Like가 있는지 확인 한다.
        //회원은 세션정보를 활용한다.
        //db에 2개를 묶어서 유니크 제약 조건으로 활용 한다.   //해서 값이 있으면 true 없으면 false
        boolean isChoice = (likeRepository.findByMemberAndBoard(id ,memberId).isEmpty()) ? false : true;
        //controller에서 분리해서 넘겨줄까 그럼 걱정없지 않나.
        //likeCount도 마찬가지고 , 그러면 비동기 통신하고 th:field같은거에 의존하지 않아도 되잖아.

        return BoardReadDto.builder()
                .boardId(findBoard.getId())
                .subject(findBoard.getSubject())
                .boardContent(findBoard.getContent())
                .readCount(findBoard.getReadCount()+1)
                .writer(findBoard.getMember().getUserId())
                .likeCount(findBoard.getLikeCount())
                .isChoice(isChoice)
                .filesDtoList(filesDtoList)
                .build();
    }


    //게시글 수정
    @Transactional
    public void update(BoardUpdateDto boardUpdateDto){
        Board findBoard = boardRepository.findById(boardUpdateDto.getBoardId())
                            .orElseThrow(() -> new IllegalStateException());

        //파일 내용 수정
       findBoard.changeContent(boardUpdateDto.getBoardContent());

        //파일 변경 내역 위임
        List<FilesDto> updateFiles = fileService.update(findBoard, boardUpdateDto);

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
