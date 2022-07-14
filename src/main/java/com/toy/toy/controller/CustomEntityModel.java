package com.toy.toy.controller;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

public class CustomEntityModel extends EntityModel {

    public CustomEntityModel(){

    }

    /*
    public void addLink(WebMvcLinkBuilder webMvcLinkBuilder , String withRel){
        this.add(webMvcLinkBuilder.withRel(withRel));
    }

    public void addSelf(WebMvcLinkBuilder webMvcLinkBuilder){
        this.add(webMvcLinkBuilder.withSelfRel());
    }

    public void addMainPage(){
        this.add(Link.of("http://www.localhost:8080").withRel("main-page"));
    }*/

    public void addProfile(String profile){
        this.add(Link.of(profile).withRel("profile"));
    }

}
