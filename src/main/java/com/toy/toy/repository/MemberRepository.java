package com.toy.toy.repository;




import com.toy.toy.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {


    public Optional<Member> findByUserId(String userId);
}
