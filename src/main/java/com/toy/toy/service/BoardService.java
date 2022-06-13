package com.toy.toy.service;



import com.toy.toy.entity.Member;
import com.toy.toy.repository.BoardRepository;
import com.toy.toy.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;



    //회원 탈퇴시 게시글 삭제
    public void deleteByMember(Member member){
        //게시글 삭제
        boardRepository.findByMember(member)
                .stream()
                .filter(b -> !b.getComments().isEmpty())
                .forEach(b -> commentRepository.deleteByBoard(b));

        //파일 삭제도 구현 필요
    }


}
