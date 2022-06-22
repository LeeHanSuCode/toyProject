package com.toy.toy.service;


import com.toy.toy.controller.exception_controller.exception.MemberNotFoundException;
import com.toy.toy.dto.JoinMemberDto;
import com.toy.toy.dto.UpdateMemberDto;
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
    public Long join(JoinMemberDto joinMemberDto){

        Member member = joinMemberDto.changeEntity();

        memberRepository.save(member);

        return member.getId();
    }


    //회원 목록 조회
    //전체 목록에 보여줄 데이터 -> 이름 , 아이디 , 주민번호
    public Page<JoinMemberDto> findAll(Pageable pageable){
        return memberRepository.findAll(pageable)
                .map(m -> JoinMemberDto.builder()
                        .username(m.getUsername())
                        .userId(m.getUserId())
                        .ssn(m.getSsn())
                        .build());
    }


    //회원 상세 조회
    //상세보기 -> 회원 모든 정보 공개(비밀번호 제외)
    public UpdateMemberDto findById(Long memberId){
        Member member = memberRepository.findById(memberId)
                                    .orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다."));

        return UpdateMemberDto.builder()
                .username(member.getUsername())
                .userId(member.getUserId())
                .ssn(member.getSsn())
                .email(member.getEmail())
                .tel(member.getTel())
                .build();
    }


    //회원 수정 하기
    @Transactional
    public void update(UpdateMemberDto updateMemberDto){
        Member member = memberRepository.findById(updateMemberDto.getId())
                .orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다."));

        //변경 감지 이용
        member.updateMember(updateMemberDto.getUsername() , updateMemberDto.getPassword()
                , updateMemberDto.getEmail() , updateMemberDto.getTel());
    }




    //회원 삭제
    @Transactional
    public void delete(Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다."));

        //회원이 작성한 댓글들을 삭제
        if(!member.getComments().isEmpty()){
            commentRepository.deleteByMember(member);
        }

        //회원이 작성한 게시글과 거기에 작성된 댓글,파일 삭제
        if(!member.getBoards().isEmpty()){
            boardService.deleteByMember(member);
        }

        memberRepository.delete(member);
    }

}
