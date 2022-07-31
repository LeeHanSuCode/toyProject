package com.toy.toy.controller;

import com.toy.toy.argumentResolver.Login;
import com.toy.toy.controller.exception_controller.exception.ValidationNotFieldMatchedException;
import com.toy.toy.dto.responseDto.CommentResponse;
import com.toy.toy.dto.responseDto.LoginResponse;
import com.toy.toy.dto.responseDto.PageAndObjectResponse;
import com.toy.toy.dto.validationDto.WriteContentDto;
import com.toy.toy.entity.Comment;
import com.toy.toy.service.CommentService;
import com.toy.toy.service.PageCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.EntityMode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

import static com.toy.toy.StaticVariable.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Controller
@RequestMapping("/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentService commentService;

    //댓글 목록 가져오기
    @GetMapping("/{boardId}")
    public ResponseEntity getCommentList(@PathVariable Long boardId,
                                        @PageableDefault Pageable pageable){
        Page<CommentResponse> pageComments = commentService.findAll(boardId, pageable);


        List<CommentResponse> content = pageComments.getContent();
        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(Link.of("/docs/index.html#_댓글_목록").withRel(PROFILE));
        PageCalculator pageCalculator = new PageCalculator(10 ,pageComments.getTotalPages() , pageComments.getNumber()+1);
        PageAndObjectResponse<List> listPageResponse = new PageAndObjectResponse<>(content ,pageCalculator,representationModel);


        return ResponseEntity.ok(listPageResponse);
    }


    //댓글 등록
    @PostMapping("/{boardId}")
    public ResponseEntity registerComment(@Login LoginResponse loginResponse, @PathVariable Long boardId,
                                          @Validated @RequestBody WriteContentDto writeContentDto, BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            throw new ValidationNotFieldMatchedException(bindingResult);
        }

        //등록한 entity -> responseDto로 변경
        Comment registryComment = commentService.registry(loginResponse.getId(), boardId, writeContentDto.getContent());
        CommentResponse commentResponse = new CommentResponse().changeCommentResponse(registryComment);

        WebMvcLinkBuilder linkBuilder = linkTo(CommentController.class).slash(boardId);
        WebMvcLinkBuilder commentLink = linkBuilder.slash(commentResponse.getCommentId());

        return ResponseEntity.created(commentLink.toUri()).headers(encodingHeaders()).body(
                EntityModel.of(commentResponse)
                        .add(linkBuilder.withRel("comments-list"))
                        .add(commentLink.withRel("comment-update"))
                        .add(commentLink.withRel("comment-delete"))
                        .add(Link.of("/docs/index.html#_댓글_등록").withRel(PROFILE))
        );
    }


    //댓글 수정
    @PatchMapping("/{boardId}/{id}")
    public ResponseEntity updateComment(@PathVariable Long id ,@PathVariable Long boardId
            ,@Validated @RequestBody WriteContentDto writeContentDto , BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            throw new ValidationNotFieldMatchedException(bindingResult);
        }

        Comment updateComment = commentService.updateComment(id, writeContentDto.getContent());

        return ResponseEntity.ok().body(
                EntityModel.of(new CommentResponse().changeCommentResponse(updateComment))
                .add(linkTo(CommentController.class).slash(boardId).withRel("comments-list"))
                        .add(Link.of("/index.html#_댓글_수정").withRel(PROFILE))
        );
    }

    //댓글 삭제
    @DeleteMapping("/{boardId}/{id}")
    public ResponseEntity deleteComment(@PathVariable Long boardId , @PathVariable Long id){
        log.info("호출 되려나??");
        Long deleteId = commentService.delete(id);

        RepresentationModel representationModel = new RepresentationModel();

        return ResponseEntity.ok().body(
                representationModel
                        .add(linkTo(CommentController.class).slash(boardId).withRel("comments-list"))
                        .add(Link.of("/docs/index.html#_댓글_삭제").withRel(PROFILE))
        );
    }




    //응답 헤더 지정
    private HttpHeaders encodingHeaders(){
        HttpHeaders resHeaders = new HttpHeaders();
        resHeaders.add("Content-Type", "application/hal+json;charset=UTF-8");

        return resHeaders;
    }


}
