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


    //Like 존재 조회
    public String isClickLike(Long boardId , Long memberId){
        Optional<Likes> findLike = likeRepository.findByMemberAndBoard(boardId, memberId);

        LikeChoice likeChoice =(!findLike.isPresent())? LikeChoice.NOTHING : (findLike.get().equals(LikeChoice.LIKE)) ? LikeChoice.LIKE : LikeChoice.HATE;
        return likeChoice.toString();
    }


    //좋아요 추가
    public void add(Board board , Member member){

        Likes like = new Likes(board, member);
        like.addLikes();

        likeRepository.save(like);
    }

    //싫어요 추가
    public void subtract(Board board , Member member){
        Likes like = new Likes(board, member);
        like.subtractLikes();

        likeRepository.save(like);
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
