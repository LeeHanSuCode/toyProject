package com.toy.toy.controller;

import com.toy.toy.StaticVariable;
import com.toy.toy.argumentResolver.Login;
import com.toy.toy.controller.exception_controller.exception.ValidationNotFieldMatchedException;
import com.toy.toy.dto.SearchConditionDto;
import com.toy.toy.dto.responseDto.BoardResponse;
import com.toy.toy.dto.responseDto.LoginResponse;
import com.toy.toy.dto.responseDto.PageAndObjectResponse;
import com.toy.toy.dto.validationDto.UpdateBoardDto;
import com.toy.toy.dto.validationDto.WriteBoardDto;
import com.toy.toy.entity.*;
import com.toy.toy.service.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;
import java.util.Objects;
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



    //게시글 목록
    @GetMapping
    public ResponseEntity findBoards(@RequestBody SearchConditionDto searchConditionDto,
            @PageableDefault(sort = "id",direction = Sort.Direction.DESC) Pageable pageable){


        Page<Board> pageBoard = boardService.findAll(searchConditionDto,pageable);

        //게시글 정보
        List<EntityModel<BoardResponse>> collect = pageBoard.getContent()
                .stream().map(
                        b -> EntityModel.of(new BoardResponse(b, b.getMember()))
                                .add(linkTo(BoardController.class).slash(b.getId()).withSelfRel())
                ).collect(Collectors.toList());

        //페이지 정보
        PageCalculator pageCalculator = new PageCalculator(10, pageBoard.getTotalPages(), pageBoard.getNumber() + 1);
        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(Link.of("/docs/index.html#_게시글_목록_조회").withRel(PROFILE));
        PageAndObjectResponse<List> listPageResponse = new PageAndObjectResponse<>(collect ,pageCalculator,representationModel);


        return ResponseEntity.ok()
                .headers(encodingHeaders())
                .body(listPageResponse);
    }

    //게시글 보기
    @GetMapping("/{id}")
    public ResponseEntity findOne(@PathVariable Long id , @Login LoginResponse loginResponse){

        Board findBoard = boardService.findById(id);


        WebMvcLinkBuilder webMvcLinkBuilder = getWebMvcLinkBuilder(findBoard);

        BoardResponse boardResponse = new BoardResponse(findBoard, findBoard.getMember());

        EntityModel<BoardResponse> entityModel = EntityModel.of(boardResponse)
                .add(linkTo(BoardController.class).withRel(BOARD_LIST))
                .add(Link.of("/docs/index.html#_게시글_단건_조회").withRel(PROFILE));

        if(loginResponse!=null && findBoard.getMember().getId().equals(loginResponse.getId())){
            entityModel
                    .add(webMvcLinkBuilder.withRel(BOARD_UPDATE))
                    .add(webMvcLinkBuilder.withRel(BOARD_DELETE))
                    ;
        }

        return ResponseEntity.ok()

                .body(entityModel);
    }


    //게시글 등록.
    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity register(@Login LoginResponse loginResponse
            , @RequestPart(value = "writeBoardDto") @Validated WriteBoardDto writeBoardDto
            , @RequestPart(value = "filesList" , required = false) List<MultipartFile> filesList
            , BindingResult bindingResult){


        //validation
        if(bindingResult.hasErrors()){
            throw new ValidationNotFieldMatchedException(bindingResult);
        }

        Member findMember = memberService.findById(loginResponse.getId());

        //게시글 저장
       Board registerBoard = boardService.register(writeBoardDto.changeEntity(findMember) , filesList);

        WebMvcLinkBuilder location = getWebMvcLinkBuilder(registerBoard);

        return ResponseEntity.created(location.toUri())
                .headers(encodingHeaders())
                .body(EntityModel.of( new BoardResponse(registerBoard,  findMember))
                        .add(location.withRel(BOARD_UPDATE))
                        .add(location.withRel(BOARD_DELETE))
                        .add(location.withSelfRel())
                        .add(Link.of("/docs/index.html#_게시글_등록파일_포함").withRel(PROFILE)));
    }




    //게시글 수정
    @PostMapping("/update/{id}")
    public ResponseEntity updateBoard(@RequestPart(value = "newFiles" , required = false) List<MultipartFile> newFiles,
            @RequestPart @Validated UpdateBoardDto updateBoardDto , BindingResult bindingResult,
                                      @PathVariable Long id){

        //validation
        if(bindingResult.hasErrors()){
             throw new ValidationNotFieldMatchedException(bindingResult);
        }

        Board updateBoard = boardService.update(updateBoardDto , newFiles , id);


        WebMvcLinkBuilder webMvcLinkBuilder = getWebMvcLinkBuilder(updateBoard);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(webMvcLinkBuilder.withRel(BOARD_INFO))
                .add(linkTo(BoardController.class).withRel(BOARD_LIST))
                .add(Link.of("/docs/index.html#_게시글_수정").withRel(PROFILE));

        return ResponseEntity.ok()
                .body(representationModel);
    }

    //게시글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity deleteBoard(@PathVariable Long id){

        boardService.delete(id);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(linkTo(BoardController.class).withRel(BOARD_LIST))
                .add(linkTo(HomeController.class).withRel(MAIN_PAGE))
                .add(Link.of("/docs/index.html#_게시글_삭제").withRel(PROFILE));
        return ResponseEntity.ok()
                .body(representationModel);

    }


    private WebMvcLinkBuilder getWebMvcLinkBuilder(Board board){
        return linkTo(BoardController.class).slash(board.getId());
    }

    //응답 헤더 지정
    private HttpHeaders encodingHeaders(){
        HttpHeaders resHeaders = new HttpHeaders();
        resHeaders.add("Content-Type", "application/hal+json;charset=UTF-8");

        return resHeaders;
    }

}
