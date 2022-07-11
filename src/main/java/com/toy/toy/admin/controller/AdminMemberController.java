package com.toy.toy.admin.controller;

import com.toy.toy.admin.service.AdminMemberService;
import com.toy.toy.controller.MemberController;
import com.toy.toy.dto.responseDto.MemberResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Controller("/admin/members")
public class AdminMemberController {

    private AdminMemberService adminMemberService;

    //회원 목록 가져오기
    @GetMapping
    public ResponseEntity findAll(@PageableDefault Pageable pageable){

        Page<EntityModel<MemberResponse>> members = adminMemberService.findAll(pageable).map(m ->
                EntityModel.of(
                        MemberResponse.builder()
                                .username(m.getUsername())
                                .userId(m.getUserId())
                                .email(m.getEmail())
                                .tel(m.getTel())
                                .build()).add(linkTo(MemberController.class).slash(m.getId())
                        .withSelfRel()));



        return ResponseEntity.ok().body(members);
    }
}
