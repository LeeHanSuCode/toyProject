package com.toy.toy.admin.repository;

import com.toy.toy.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface AdminBoardRepository extends JpaRepository<Board,Long> {


    @Query(value = "select b from Board b join fetch b.member" , countQuery = "select count(b.id) from Board b")
    public Page<Board> findAllBoardFetchMember(Pageable pageable);

    @Query("select b from Board b join fetch b.member where b.id=:id")
    public Optional<Board> findOneFetchMemberById(@Param("id") Long id);
}
