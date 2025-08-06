package com.rgs.web_demo.repository;

import com.rgs.web_demo.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberNormalRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    Optional<Member> findByProviderId(String providerId);

    boolean existsByEmail(String email);

    boolean existsByProviderId(String providerId);
}
