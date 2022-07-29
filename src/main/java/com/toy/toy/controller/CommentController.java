package com.toy.toy.controller;

import com.toy.toy.argumentResolver.Login;
import com.toy.toy.controller.exception_controller.exception.ValidationNotFieldMatchedException;
import com.toy.toy.dto.responseDto.CommentResponse;
import com.toy.toy.dto.responseDto.LoginResponse;
import com.toy.toy.dto.responseDto.PageAndObjectResponse;
import com.toy.toy.entity.Comment;
import com.toy.toy.service.CommentService;
import com.toy.toy.service.PageCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.toy.toy.StaticVariable.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Controller
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    //댓글 목록 가져오기
    @GetMapping("/{boardId}")
    public ResponseEntity getCommentList(@Login LoginResponse loginResponse , @PathVariable Long boardId,
                                        @PageableDefault Pageable pageable){
        Page<CommentResponse> pageComments = commentService.findAll(boardId, pageable)
                .map(c -> CommentResponse.builder()
                        .commentId(c.getId())
                        .memberId(loginResponse.getId())
                        .boardId(boardId)
                        .content(c.getContent())
                        .content(c.getWriter())
                        .build());

        List<CommentResponse> content = pageComments.getContent();
        PageCalculator pageCalculator = new PageCalculator(10 ,pageComments.getTotalPages() , pageComments.getNumber()+1);
        PageAndObjectResponse<List> listPageResponse = new PageAndObjectResponse<>(content ,pageCalculator);


        return ResponseEntity.ok(listPageResponse);
    }


    //댓글 등록
    @PostMapping("/{boardId}")
    public ResponseEntity registerComment(@Login LoginResponse loginResponse, @PathVariable Long boardId,
                                          @RequestBody String content , BindingResult bindingResult){
        //validation
        validationCheckContent(content , bindingResult);

        //등록한 entity -> responseDto로 변경
        Comment registryComment = commentService.registry(loginResponse.getId(), boardId, content);
        CommentResponse commentResponse = new CommentResponse().changeCommentResponse(registryComment);

        WebMvcLinkBuilder linkBuilder = linkTo(CommentController.class).slash(boardId);
        WebMvcLinkBuilder commentLink = linkBuilder.slash(commentResponse.getCommentId());

        return ResponseEntity.created(commentLink.toUri()).body(
                EntityModel.of(commentResponse)
                        .add(linkBuilder.withRel("comments-list"))
                        .add(commentLink.withRel("comment-update"))
                        .add(commentLink.withRel("comment-delete")
        ));
    }


    //댓글 수정
    @PatchMapping("/{boardId}/{id}")
    public ResponseEntity updateComment(@PathVariable Long id ,@PathVariable Long boardId ,@RequestBody String content , BindingResult bindingResult){
       //validation
        validationCheckContent(content, bindingResult);

        Comment updateComment = commentService.updateComment(id, content);

        return ResponseEntity.ok().body(
                EntityModel.of(new CommentResponse().changeCommentResponse(updateComment))
                .add(linkTo(CommentController.class).slash(boardId).withRel("comments-list"))
        );
    }

    //댓글 삭제
    @DeleteMapping("/{boardId}/{id}")
    public ResponseEntity deleteComment(@PathVariable Long boardId , @PathVariable Long id){
        Long deleteId = commentService.delete(id);


        return ResponseEntity.ok().body(
                EntityModel.of(deleteId)
                        .add(Link.of(MAIN_PAGE))
                        .add(linkTo(CommentController.class).slash(boardId).withRel("comments-list"))
        );
    }


    private void validationCheckContent(@RequestBody String content, BindingResult bindingResult) {
        //validation
        if(content == null && content.isBlank()){
            bindingResult.rejectValue("content" , "NotBlank" , "내용은 필수 값입니다.");
            throw new ValidationNotFieldMatchedException(bindingResult);
        }
    }


}
