package com.toy.toy.service;

import com.toy.toy.dto.BoardWriteDto;
import com.toy.toy.entity.Board;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class Service {

    @Test
    public void test(){
        List<BoardWriteDto> list = new ArrayList<>();

        list.stream().forEach(b -> b.getSubject());
    }
}
