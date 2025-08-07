package com.rgs.web_demo.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "inquiries")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inquiry {

    // 기본키, 자동 증가
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 문의자 이름 (필수, 최대 100자)
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    // 문의자 이메일 (필수, 최대 255자)
    @Column(name = "email", nullable = false, length = 255)
    private String email;

    // 문의 제목 (필수, 최대 200자)
    @Column(name = "subject", nullable = false, length = 200)
    private String subject;

    // 문의 내용 (필수, TEXT 타입)
    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    // 문의자 전화번호 (선택사항, 최대 20자)
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    // 생성일시 (PrePersist로 자동 설정, 수정 불가)
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 엔티티 저장 전 생성일시 자동 설정
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}