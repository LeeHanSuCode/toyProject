package com.toy.toy.controller;

import com.toy.toy.controller.exception_controller.exception.ValidationNotFieldMatchedException;
import com.toy.toy.dto.MemberResponse;
import com.toy.toy.dto.validationDto.JoinMemberDto;
import com.toy.toy.dto.validationDto.UpdateMemberDto;
import com.toy.toy.entity.Member;
import com.toy.toy.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/members")
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final ModelMapper modelMapper;

        //회원 목록 가져오기
        @GetMapping
        public ResponseEntity findAll(@PageableDefault Pageable pageable){

            Page<EntityModel<MemberResponse>> members = memberService.findAll(pageable).map(m ->
                    EntityModel.of(
                            MemberResponse.builder()
                                    .username(m.getUsername())
                                    .userId(m.getUserId())
                                    .email(m.getEmail())
                                    .tel(m.getTel())
                                    .createdDate(m.getCreatedDate())
                                    .build()).add(linkTo(MemberController.class).slash(m.getId())
                            .withSelfRel()));



        return ResponseEntity.ok().body(members);
    }


    //회원 가입
   @PostMapping
    public ResponseEntity join(@RequestBody @Valid JoinMemberDto joinMemberDto ,BindingResult bindingResult){

        if(!joinMemberDto.getPassword().equals(joinMemberDto.getPassword2())){
            bindingResult.rejectValue("password","NotEquals","비밀번호가 일치하지 않습니다");
        }

        if(bindingResult.hasErrors()){
            throw new ValidationNotFieldMatchedException(bindingResult);
        }

       Member joinMember = modelMapper.map(joinMemberDto, Member.class);

       Member joinSuccessMember = memberService.join(joinMember);

       WebMvcLinkBuilder locationBuilder = linkTo(MemberController.class).slash(joinMember.getId());
       URI location = locationBuilder.toUri();

       EntityModel<MemberResponse> member = EntityModel.of(MemberResponse.builder()
                       .userId(joinMember.getUserId())
                       .username(joinMember.getUsername())
                       .email(joinMember.getEmail())
                       .tel(joinMember.getTel())
                       .createdDate(joinMember.getCreatedDate())
                       .build())
               .add(locationBuilder.withSelfRel())
               .add(locationBuilder.withRel("update-member"));


       return ResponseEntity.created(location).body(member);
    }

    //회원 상세 정보
    @GetMapping("/{id}")
    public ResponseEntity findOne(@PathVariable Long id){
        Member findMember = memberService.findById(id);

        WebMvcLinkBuilder linkBuilder = linkTo(MemberController.class).slash(findMember.getId());


        EntityModel<MemberResponse> model = EntityModel.of(
                        MemberResponse.builder()
                                .userId(findMember.getUserId())
                                .username(findMember.getUsername())
                                .email(findMember.getEmail())
                                .tel(findMember.getTel())
                                .createdDate(findMember.getCreatedDate())
                                .build()
                ).add(linkBuilder.withSelfRel())
                .add(linkBuilder.withRel("update-member"))
                .add(linkBuilder.withRel("delete-member"));

        return ResponseEntity.ok().body(model);
    }

    //회원수정
    @PatchMapping("/{id}")
    public ResponseEntity update(@RequestBody @Valid UpdateMemberDto updateMemberDto , @PathVariable Long id){
        Member updateMember = memberService.update(updateMemberDto , id);

        WebMvcLinkBuilder linkBuilder = linkTo(MemberController.class).slash(updateMember.getId());


        EntityModel<MemberResponse> model = EntityModel.of(
                        MemberResponse.builder()
                                .userId(updateMember.getUserId())
                                .username(updateMember.getUsername())
                                .email(updateMember.getEmail())
                                .tel(updateMember.getTel())
                                .createdDate(updateMember.getCreatedDate())
                                .build()
                ).add(linkBuilder.withSelfRel())
                .add(linkBuilder.withRel("update-member"))
                .add(linkBuilder.withRel("delete-member"));

        return ResponseEntity.ok().body(model);
    }


    //회원삭제
    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Long id){

        memberService.delete(id);

        Map<String ,String> greeting = new HashMap<>();
        greeting.put("greeting" , "Thank you for using it so far");


        return ResponseEntity.ok()
                .body(EntityModel.of(greeting)
                        .add(Link.of("http://www.localhost:8080")
                                .withRel("main-page")));
    }




}
