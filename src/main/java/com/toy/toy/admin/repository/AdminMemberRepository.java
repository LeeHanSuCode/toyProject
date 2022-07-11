package com.toy.toy.admin.repository;

import com.toy.toy.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface AdminMemberRepository extends JpaRepository<Member , Long> {

    @Query("select distinct m from Member m join fetch m.boards where m.id=:id")
    public Optional<Member> findByIdFetchBoard(@Param("id") Long id);
}
