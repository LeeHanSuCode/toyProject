package com.toy.toy.service;


import com.toy.toy.controller.exception_controller.exception.MemberNotFoundException;
import com.toy.toy.dto.validationDto.JoinMemberDto;
import com.toy.toy.dto.validationDto.UpdateMemberDto;
import com.toy.toy.entity.Member;
import com.toy.toy.repository.CommentRepository;
import com.toy.toy.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BoardService boardService;
    private final CommentRepository commentRepository;


    //회원 가입
    @Transactional
    public Member join(Member member){

        memberRepository.save(member);

        return member;
    }



    //회원 상세 조회
    public Member findById(Long memberId){
       return memberRepository.findById(memberId)
                                    .orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다."));
    }


    //회원 수정 하기
    @Transactional
    public void update(UpdateMemberDto updateMemberDto , Long id){

        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다."));

        //변경 감지 이용
        member.updateMember(updateMemberDto.getUsername() , updateMemberDto.getPassword()
                , updateMemberDto.getEmail() , updateMemberDto.getTel());

    }




    //회원 삭제
    @Transactional
    public void
    delete(Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다."));

        //회원이 작성한 댓글들을 삭제
        if(member.getComments()!=null && !member.getComments().isEmpty()){
            commentRepository.deleteByMember(member);
        }

        //회원이 작성한 게시글과 거기에 작성된 댓글,파일 삭제
        if(member.getBoards()!=null && !member.getBoards().isEmpty()){
            boardService.deleteByMember(member);
        }

        memberRepository.delete(member);
    }

}
