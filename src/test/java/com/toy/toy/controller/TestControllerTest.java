package com.toy.toy.controller;

import org.assertj.core.api.AbstractThrowableAssert;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TestControllerTest {
/*    TestController testController = new TestController();*/

    @BeforeEach
    void before(){
        System.out.println("몇번??");
    }

    @Test
    void test_ex1(){

    }
    @Test
    void test_ex2(){

    }
    @Test
    void test_ex3(){

    }

}