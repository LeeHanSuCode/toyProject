package com.toy.toy.service;

import com.toy.toy.entity.Board;
import com.toy.toy.entity.LikeChoice;
import com.toy.toy.entity.Likes;
import com.toy.toy.entity.Member;
import com.toy.toy.repository.BoardRepository;
import com.toy.toy.repository.LikeRepository;
import com.toy.toy.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {

    private final LikeRepository likeRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    //조회
    public Integer isClickLike(Long boardId , Long memberId){
        Optional<Likes> findLike = likeRepository.findByMemberAndBoard(boardId, memberId);

        return (!findLike.isPresent())? 0 : (findLike.get().equals(LikeChoice.LIKE)) ? 1 : -1;
    }


    //좋아요 추가
    public Integer add(String mode, Long boardId , Long memberId){

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalStateException());

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalStateException());

        Likes like = new Likes(board, member, mode);

        likeRepository.save(like);
        board.changeLikeCount(like.getLikeChoice().name());         //누른 버튼으로 인한 board에 추가.

        return board.getLikeCount();                                //좋아요 갯수 반환.
    }


    //삭제
    public Integer delete(String mode , Long likeId){
        Likes like = likeRepository.findById(likeId)
                .orElseThrow(() -> new IllegalStateException());//이떄 fetch join으로 board정보까지 가져와야 한다.

        like.getBoard().deleteLikeCount(like.getLikeChoice().name());

        likeRepository.delete(like);

        return like.getBoard().getLikeCount();
    }


    //삭제
    public void deletedByBoard(Long boardId){
       likeRepository.deletedByBoard(boardId);
    }
}
