package com.toy.toy.controller;

import com.toy.toy.StaticVariable;
import com.toy.toy.argumentResolver.Login;
import com.toy.toy.controller.exception_controller.exception.ValidationNotFieldMatchedException;
import com.toy.toy.dto.LoginMemberDto;
import com.toy.toy.dto.responseDto.CommentResponse;
import com.toy.toy.dto.responseDto.LoginResponse;
import com.toy.toy.entity.Comment;
import com.toy.toy.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.connector.Response;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import static com.toy.toy.StaticVariable.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Controller
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

/*
    @GetMapping("/{boardId}")
    public ResponseEntity getCommentList(@PathVariable Long boardId){

    }
*/


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
