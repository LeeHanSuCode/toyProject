package com.toy.toy.admin.service;

import com.toy.toy.admin.dto.AdminMemberDto;
import com.toy.toy.admin.repository.AdminMemberRepository;
import com.toy.toy.controller.exception_controller.exception.MemberNotFoundException;
import com.toy.toy.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class AdminMemberService {
    //관리자의 기능.

    /*1.회원
        회원 목록
            -> 회원 상세 보기
                        -> 회원이 작성한 게시글 보기.
     */

    /*2.게시글
        게시글 목록
            -> 게시글 상세 보기
                    ->게시글 작성한 회원 보기
                    ->게시글 삭제

            -> 좋아요 -20개 이상 받은 게시글 보기.
                     -> 게시글 삭제
     */

    private final AdminMemberRepository adminMemberRepository;


    //회원 목록 조회  AdminMemberDto(id , username , userId , ssn )
    public Page<Member> findAll(Pageable pageable){
        return adminMemberRepository.findAll(pageable);
    }




    //회원 검색(query dsl적용할 때 같이 하자)
    public Page<AdminMemberDto> findByCondition(Pageable pageable , String condition){
        return null;
    }



    //회원 단건 조회 (
    public AdminMemberDto findById(Long id){
        Member findMember = adminMemberRepository.findByIdFetchBoard(id)
                .orElseThrow(() -> new MemberNotFoundException("탈퇴한 회원 혹은 존재하지 않는 회원입니다."));

        List<Long> boardIds = findMember.getBoards()
                .stream().map(b -> b.getId())
                .collect(Collectors.toList());

        //회원 상세 정보 ,  작성한 게시글 식별자
        return new AdminMemberDto(findMember,boardIds);
    }

}
