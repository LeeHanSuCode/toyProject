package com.toy.toy.controller;

import com.toy.toy.argumentResolver.Login;
import com.toy.toy.dto.responseDto.LoginResponse;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.toy.toy.StaticVariable.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Controller
@RequestMapping("/")
public class HomeController {

    @GetMapping
    public ResponseEntity<RepresentationModel> home(@Login LoginResponse loginResponse){

        var index = new RepresentationModel<>();
        index.add(linkTo(BoardController.class).withRel(BOARD_LIST));

        if(loginResponse == null){
            return ResponseEntity.ok(index);
        }

        index.add(linkTo(MemberController.class).slash(loginResponse.getId()).withRel("member-info"));
        return ResponseEntity.ok(index);
    }

}
