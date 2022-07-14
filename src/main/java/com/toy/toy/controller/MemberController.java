package com.toy.toy.controller;

import com.toy.toy.StaticVariable;
import com.toy.toy.controller.exception_controller.exception.ValidationNotFieldMatchedException;
import com.toy.toy.dto.responseDto.MemberResponse;
import com.toy.toy.dto.validationDto.JoinMemberDto;
import com.toy.toy.dto.validationDto.UpdateMemberDto;
import com.toy.toy.entity.Member;
import com.toy.toy.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import javax.validation.Valid;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.toy.toy.StaticVariable.*;
import static com.toy.toy.dto.responseDto.MemberResponse.changeMemberResponse;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/members")
@Slf4j
public class MemberController {

    private final MemberService memberService;




    //회원 가입
   @PostMapping
    public ResponseEntity join(@RequestBody @Valid JoinMemberDto joinMemberDto ,BindingResult bindingResult){

       if(joinMemberDto.getPassword() != null && joinMemberDto.getPassword2() != null &&
               !joinMemberDto.getPassword().equals(joinMemberDto.getPassword2())){
           bindingResult.rejectValue("password","NotEquals","비밀번호가 일치하지 않습니다");
       }

       if(bindingResult.hasErrors()){
            throw new ValidationNotFieldMatchedException(bindingResult);
        }

       Member joinMember = memberService.join(joinMemberDto.changeEntity());

       WebMvcLinkBuilder locationBuilder = getWebMvcLinkBuilder(joinMember.getId());

      CustomEntityModel.of(joinMember);

       return ResponseEntity.created(locationBuilder.toUri()).body(
               CustomEntityModel.of(changeMemberResponse(joinMember))
               .add(locationBuilder.withSelfRel())
               .add(locationBuilder.withRel("update-member"))
               .add(locationBuilder.withRel("delete-member"))
                       .add(Link.of("/docs/index.html#_회원_가입").withRel("profile"))
       );

    }



    //회원 상세 정보
    @GetMapping("/{id}")
    public ResponseEntity findOne(@PathVariable Long id){
        Member findMember = memberService.findById(id);

        WebMvcLinkBuilder linkBuilder = getWebMvcLinkBuilder(findMember.getId());

        return ResponseEntity.ok().body(
                EntityModel.of(changeMemberResponse(findMember))
                .add(linkBuilder.withSelfRel())
                .add(linkBuilder.withRel("update-member"))
                .add(linkBuilder.withRel("delete-member"))
                        .add(Link.of("/docs/index.html#_회원_조회").withRel("profile"))
        );
    }

    //회원수정
    @PatchMapping("/{id}")
    public ResponseEntity update(@RequestBody @Valid UpdateMemberDto updateMemberDto ,BindingResult bindingResult
            , @PathVariable Long id){

         if(updateMemberDto.getPassword() != null && updateMemberDto.getPassword2() != null
                    && !updateMemberDto.getPassword().equals(updateMemberDto.getPassword2())){
            bindingResult.rejectValue("password","NotEquals","비밀번호가 일치하지 않습니다");
         }

        if(bindingResult.hasErrors()){
            throw new ValidationNotFieldMatchedException(bindingResult);
        }

        memberService.update(updateMemberDto , id);

        Member findMember = memberService.findById(id);

        WebMvcLinkBuilder linkBuilder = getWebMvcLinkBuilder(findMember.getId());


        return ResponseEntity.ok().body(
                EntityModel.of(changeMemberResponse(findMember))
                .add(linkBuilder.withRel("member-info"))
                        .add(Link.of("/docs/index.html#_회원_수정").withRel("profile"))
                        .add(Link.of(MAIN_PAGE).withRel("main-page"))
        );
    }


    //회원삭제
    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Long id){

        memberService.delete(id);

        Map<String ,String> greeting = new HashMap<>();
        greeting.put("greeting" , "Thank you for using it so far");


        return ResponseEntity.ok()
                .body(EntityModel.of(greeting)
                        .add(Link.of("/docs/index.html#_회원_삭제").withRel("profile"))
                        .add(Link.of(MAIN_PAGE).withRel("main-page")));
    }


    private WebMvcLinkBuilder getWebMvcLinkBuilder(Long memberId) {
        return linkTo(MemberController.class).slash(memberId);
    }



}
