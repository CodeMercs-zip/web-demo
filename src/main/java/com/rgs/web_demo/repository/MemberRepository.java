package com.rgs.web_demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rgs.web_demo.domain.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

}
