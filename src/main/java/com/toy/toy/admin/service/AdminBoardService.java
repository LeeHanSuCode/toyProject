package com.toy.toy.admin.service;

import com.toy.toy.admin.dto.AdminBoardDto;
import com.toy.toy.admin.repository.AdminBoardRepository;
import com.toy.toy.controller.exception_controller.exception.BoardNotFoundException;
import com.toy.toy.dto.responseDto.FilesResponse;
import com.toy.toy.entity.Board;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminBoardService {
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



    private final AdminBoardRepository adminBoardRepository;

    //게시글 목록
    public Page<AdminBoardDto> findAll(Pageable pageable){
        return adminBoardRepository.findAllBoardFetchMember(pageable)
                .map(b -> AdminBoardDto.builder()
                        .id(b.getId())
                        .subject(b.getSubject())
                        .readCount(b.getReadCount())
                        .writer(b.getMember().getUserId())
                        .build());
    }}


    //게시글 상세 보기
   /* public AdminBoardDto findById(Long id){

        Board board = adminBoardRepository
                .findOneFetchMemberById(id)
                .orElseThrow(() -> new BoardNotFoundException("게시글이 존재하지 않습니다."));

        List<FilesResponse> files = board.getFiles().stream()
                .map(f -> new FilesResponse(f.getId(), f.getUploadFilename()))
                .collect(Collectors.toList());

        List<CommentDto> comments = board.getComments().stream()
                .map(c -> new CommentDto(c.getId(), c.getContent() ,c.getWriter()))
                .collect(Collectors.toList());;

        return   AdminBoardDto.builder()
                .id(board.getId())
                .subject(board.getSubject())
                .readCount(board.getReadCount())
                .writer(board.getMember().getUserId())
                .filesDtos(files)
                .commentDtos(comments)
                .build();
    }


    //게시글 검색
}
*/