package com.toy.toy.repository;

import com.toy.toy.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long>,BoardRepositoryCustom {

    @Modifying
    @Query("delete from Board b where b.member.id = :id")
    public void deleteByMemberId(@Param("id") Long id);

    @Query("select b from Board b where b.member.id = :id")
    public List<Board> findByMember(@Param("id") Long id);

    @Query("select b from Board b join fetch b.member where b.id=:id")
    public Optional<Board> findBoardWithMember(@Param("id")Long id);

    @Query(value = "select b from Board b join fetch b.member",countQuery = "select count(b.id) from Board b")
    public Page<Board> findAllWithMember(Pageable pageable);

}


