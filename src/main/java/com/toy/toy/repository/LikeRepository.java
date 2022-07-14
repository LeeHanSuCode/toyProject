package com.toy.toy.repository;


import com.toy.toy.entity.Likes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Likes,Long> {


    @Query("select l from Likes l where l.board.id = :boardId and l.member.id = :memberId")
    public Optional<Likes> findByMemberAndBoard(@Param("boardId")Long boardId ,@Param("memberId") Long memberId);

    @Modifying
    @Query("delete from Likes l where l.board.id=:boardId")
    public void deletedByBoard(@Param("boardId") Long boardId);
}
