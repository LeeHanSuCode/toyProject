package com.toy.toy.repository;

import com.toy.toy.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like,Long> {


    @Query("select l from Like l where l.board.id = :boardId and l.member.id = :memberId")
    public Optional<Like> findByMemberAndBoard(@Param("boardId")Long boardId ,@Param("memberId") Long memberId);
}
