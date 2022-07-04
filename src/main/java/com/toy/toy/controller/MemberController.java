package com.toy.toy.controller;

import com.toy.toy.dto.JoinMemberDto;
import com.toy.toy.dto.UpdateMemberDto;
import com.toy.toy.entity.Member;
import com.toy.toy.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;


        @GetMapping
        public ResponseEntity< Page<EntityModel<JoinMemberDto>>> findAll(@PageableDefault Pageable pageable){

            Page<EntityModel<JoinMemberDto>> members = memberService.findAll(pageable).map(m ->
                    EntityModel.of(
                            JoinMemberDto.builder().id(m.getId())
                                    .username(m.getUsername())
                                    .userId(m.getUserId())
                                    .ssn(m.getSsn())
                                    .build()).add(linkTo(MemberController.class).slash(m.getId())
                            .withRel("memberInfo" + m.getId())));



        return ResponseEntity.ok().body(members);
    }


    /*@PostMapping
    public EntityModel<UpdateMemberDto> join(@RequestBody @Valid JoinMemberDto joinMemberDto , BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return
        }
    }*/

}
