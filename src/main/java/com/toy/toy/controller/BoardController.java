package com.toy.toy.controller;

import com.toy.toy.StaticVariable;
import com.toy.toy.argumentResolver.Login;
import com.toy.toy.controller.exception_controller.exception.ValidationNotFieldMatchedException;
import com.toy.toy.dto.LoginMemberDto;
import com.toy.toy.dto.responseDto.BoardResponse;
import com.toy.toy.dto.responseDto.FilesResponse;
import com.toy.toy.dto.validationDto.UpdateBoardDto;
import com.toy.toy.dto.validationDto.WriteBoardDto;
import com.toy.toy.entity.*;
import com.toy.toy.repository.LikeRepository;
import com.toy.toy.service.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.toy.toy.StaticVariable.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Controller
@RequestMapping("/boards")
@RequiredArgsConstructor
@Slf4j
public class BoardController {

    private final BoardService boardService;
    private final MemberService memberService;
    private final FileService fileService;
    private final LikeService likeService;
    private final FileTransfer fileTransfer;
    private final CommentService commentService;

    //게시글 목록
    @GetMapping
    public ResponseEntity findBoards(@PageableDefault(page=0,size = 10,sort = "id",direction = Sort.Direction.DESC)
                                                 Pageable pageable){

        //페이지 정보 넘겨서 게시글을 가져온다.
        //게시글에 맞는 파일 목록을 가져 온다. -> 어떻게??
        //boardService.findAll(pageable)

        return ResponseEntity.ok().build();
    }

    //게시글 보기
    @GetMapping("/{id}")
    public ResponseEntity findOne(@PathVariable Long id , @Login LoginMemberDto loginMemberDto){

        Board findBoard = boardService.findById(id, loginMemberDto);

        List<FilesResponse> files = fileService.getFiles(findBoard);

        Integer choice = 0;

        if(loginMemberDto != null) {
            choice = likeService.isClickLike(findBoard.getId(), loginMemberDto.getId());
        }

        WebMvcLinkBuilder webMvcLinkBuilder = getWebMvcLinkBuilder(findBoard);

        EntityModel<BoardResponse> entityModel = EntityModel.of(new BoardResponse(findBoard, files))
                .add(linkTo(BoardController.class).withRel("board-list"));

        if(loginMemberDto!=null){
            entityModel
                    .add(webMvcLinkBuilder.withRel("update-board"))
                    .add(webMvcLinkBuilder.withRel("delete-board"));
        }

        return ResponseEntity.ok().body(entityModel);
    }


    //게시글 등록.
    @PostMapping
    public ResponseEntity register(@Login Member loginMember,@RequestBody WriteBoardDto writeBoardDto , BindingResult bindingResult){

        //validation
        if(bindingResult.hasErrors()){
            throw new ValidationNotFieldMatchedException(bindingResult);
        }

        Member findMember = memberService.findById(loginMember.getId());

        //게시글 저장
       Board registerBoard = boardService.register(writeBoardDto.changeEntity(findMember));

        List<FilesResponse> saveFiles = new ArrayList<>();

        //파일 저장 및 변환
        if(writeBoardDto.getFiles().size() > 0) {
            List<Files> filesList = fileTransfer.changeFiles(writeBoardDto.getFiles(), registerBoard);
            saveFiles = fileService.save(filesList)
                    .stream()
                    .map(FilesResponse::new)
                    .collect(Collectors.toList());
        }

        WebMvcLinkBuilder location = getWebMvcLinkBuilder(registerBoard);

        return ResponseEntity.created(location.toUri())
                .body(EntityModel.of( new BoardResponse(registerBoard, saveFiles))
                        .add(location.withRel("board-update"))
                        .add(location.withRel("board-delete")));
    }




    //게시글 수정
    @PatchMapping("/{id}")
    public ResponseEntity updateBoard(@RequestBody UpdateBoardDto updateBoardDto , BindingResult bindingResult,
                                      @PathVariable Long id){

        //validation
        if(bindingResult.hasErrors()){
             throw new ValidationNotFieldMatchedException(bindingResult);
        }

        Board updateBoard = boardService.update(updateBoardDto);


        //새로운 파일 저장
        if(updateBoardDto.getNewFiles().size() > 0) {
            List<Files> newFilesList = fileTransfer.changeFiles(updateBoardDto.getNewFiles(), updateBoard);
            fileService.save(newFilesList);
        }

        //넘어오지 않은 파일 제거.
        fileService.update(updateBoard , updateBoardDto);

        WebMvcLinkBuilder webMvcLinkBuilder = getWebMvcLinkBuilder(updateBoard);

        return ResponseEntity.ok()
                .body(EntityModel.of(updateBoard.getId())
                        .add(webMvcLinkBuilder.withRel("board-info"))
                        .add(linkTo(BoardController.class).withRel("board-list"))
                );
    }


    //게시글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity deleteBoard(@PathVariable Long id){
        //좋아요 삭제
        likeService.deletedByBoard(id);
        //댓글 삭제
        commentService.deletedByBoard(id);
        //파일 삭제
        fileService.deletedByBoard(id);
        //게시글 삭제
        boardService.delete(id);


        return ResponseEntity.ok()
                .body(new CustomEntityModel()
                        .add(linkTo(BoardController.class).withRel("board-list"))
                        .add(Link.of(MAIN_PAGE)));
    }


    private WebMvcLinkBuilder getWebMvcLinkBuilder(Board board){
        return linkTo(BoardController.class).slash(board.getId());
    }

}
