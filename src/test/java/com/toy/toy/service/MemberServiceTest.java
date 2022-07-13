package com.toy.toy.service;
import com.toy.toy.dto.validationDto.JoinMemberDto;
import com.toy.toy.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@Slf4j
class MemberServiceTest {


    private BoardService boardService;
    private CommentRepository commentRepository;
    private MemberService memberService;




    private JoinMemberDto joinMemberDto(){
        return JoinMemberDto.builder()
                .username("이한수")
                .userId("dlsdn857758")
                .password("please123@@")
                .password2("please123@@")
                .email("dlsdn857758@gmail.com")
                .tel("01073633380")
                .build();
    }
}