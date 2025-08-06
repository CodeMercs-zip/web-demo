package com.rgs.web_demo.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.rgs.web_demo.vo.MemberVo;

@Mapper
public interface MemberMapper {

	// 이메일로 회원 1명 조회
	MemberVo selectMemberByEmail(String email);

	// 회원 등록
	int insertMember(MemberVo member);
}
