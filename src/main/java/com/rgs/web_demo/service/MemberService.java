package com.rgs.web_demo.service;

import com.rgs.web_demo.domain.Member;
import com.rgs.web_demo.dto.request.MemberCreateRequestDto;
import com.rgs.web_demo.dto.request.MemberUpdateRequestDto;
import com.rgs.web_demo.dto.response.MemberResponseDto;
import com.rgs.web_demo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     * 회원 생성
     */
    @Transactional
    public MemberResponseDto createMember(MemberCreateRequestDto requestDto) {
        Member member = Member.builder()
                .name(requestDto.getName())
                .phoneNumber(requestDto.getPhoneNumber())
                .email(requestDto.getEmail())
                .password(requestDto.getPassword()) // TODO: 암호화 필요
                .memberType(requestDto.getMemberType())
                .build();

        Member savedMember = memberRepository.save(member);
        return MemberResponseDto.from(savedMember);
    }

    /**
     * 회원 단일 조회
     */
    public MemberResponseDto getMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다: " + id));
        return MemberResponseDto.from(member);
    }

    /**
     * 회원 목록 조회 (페이징)
     */
    public Page<MemberResponseDto> getMembers(Pageable pageable) {
        Page<Member> members = memberRepository.findAll(pageable);
        return members.map(MemberResponseDto::from);
    }

    /**
     * 회원 수정
     */
    @Transactional
    public MemberResponseDto updateMember(Long id, MemberUpdateRequestDto requestDto) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다: " + id));

        member.updateInfo(
                requestDto.getName() != null ? requestDto.getName() : member.getName(),
                requestDto.getPhoneNumber() != null ? requestDto.getPhoneNumber() : member.getPhoneNumber(),
                requestDto.getEmail() != null ? requestDto.getEmail() : member.getEmail()
        );

        if (requestDto.getMemberType() != null) {
            member.updateMemberType(requestDto.getMemberType());
        }

        return MemberResponseDto.from(member);
    }

    /**
     * 회원 삭제
     */
    @Transactional
    public void deleteMember(Long id) {
        if (!memberRepository.existsById(id)) {
            throw new RuntimeException("회원을 찾을 수 없습니다: " + id);
        }
        memberRepository.deleteById(id);
    }
}