package com.toy.toy.service;

import com.toy.toy.entity.Board;
import com.toy.toy.entity.Files;
import com.toy.toy.entity.Member;
import com.toy.toy.entity.MemberGrade;
import com.toy.toy.repository.BoardRepository;
import com.toy.toy.repository.CommentRepository;
import com.toy.toy.repository.FileRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

    @Mock
    private BoardRepository boardRepository;
    @Spy
    private  CommentRepository commentRepository;
    @Spy
    private  FileRepository fileRepository;
    @Mock
   // private  FileService fileService = new FileService(fileRepository,);

    @Spy
    private FileTransfer fileTransfer = new FileTransfer();

    @InjectMocks
    private BoardService boardService;

    private List<MultipartFile> filesList;
    private Member member;
    private Board board;

    @BeforeEach
    void setUp_data() throws IOException {

        member  = member.builder()
                .username("홍길동")
                .userId("hslee0000")
                .memberGrade(MemberGrade.NORMAL)
                .password("wmf123!@#")
                .email("dhfl111@naver.com")
                .tel("000-0000-0000")
                .build();

        board = board.builder()
                .subject("제목입니다.")
                .content("내용입니다.")
                .readCount(0)
                .member(member)
                .build();

        String writerData = "str1,str2,str3,str4";
        MultipartFile multipartFile1 = new MockMultipartFile("files", "파일명.csv", "text/plain", writerData.getBytes(StandardCharsets.UTF_8));
        MultipartFile multipartFile2 = new MockMultipartFile("files", "파일명.csv", "text/plain", writerData.getBytes(StandardCharsets.UTF_8));
        filesList.add(multipartFile1);
        filesList.add(multipartFile2);

    }

    //게시글 등록
    @Test
    void register(){

    }



}