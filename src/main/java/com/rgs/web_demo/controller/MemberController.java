package com.rgs.web_demo.controller;

import com.rgs.web_demo.dto.request.MemberCreateRequestDto;
import com.rgs.web_demo.dto.request.MemberUpdateRequestDto;
import com.rgs.web_demo.dto.response.ApiResponseDto;
import com.rgs.web_demo.dto.response.MemberResponseDto;
import com.rgs.web_demo.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
@Tag(name = "Member", description = "회원 관리 API")
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    @Operation(summary = "회원 생성", description = "새로운 회원을 생성합니다.")
    public ResponseEntity<ApiResponseDto<MemberResponseDto>> createMember(
            @Valid @RequestBody MemberCreateRequestDto requestDto) {
        try {
            MemberResponseDto member = memberService.createMember(requestDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponseDto.success("회원이 생성되었습니다.", member));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDto.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "회원 단일 조회", description = "ID로 회원 정보를 조회합니다.")
    public ResponseEntity<ApiResponseDto<MemberResponseDto>> getMember(
            @Parameter(description = "회원 ID", required = true)
            @PathVariable Long id) {
        try {
            MemberResponseDto member = memberService.getMember(id);
            return ResponseEntity.ok(ApiResponseDto.success(member));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.error(e.getMessage()));
        }
    }

    @GetMapping
    @Operation(summary = "회원 목록 조회", description = "회원 목록을 페이징으로 조회합니다.")
    public ResponseEntity<ApiResponseDto<Page<MemberResponseDto>>> getMembers(
            @Parameter(description = "페이징 정보")
            @PageableDefault(size = 20) Pageable pageable) {
        try {
            Page<MemberResponseDto> members = memberService.getMembers(pageable);
            return ResponseEntity.ok(ApiResponseDto.success(members));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "회원 수정", description = "회원 정보를 수정합니다.")
    public ResponseEntity<ApiResponseDto<MemberResponseDto>> updateMember(
            @Parameter(description = "회원 ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody MemberUpdateRequestDto requestDto) {
        try {
            MemberResponseDto member = memberService.updateMember(id, requestDto);
            return ResponseEntity.ok(ApiResponseDto.success("회원 정보가 수정되었습니다.", member));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDto.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "회원 삭제", description = "회원을 삭제합니다.")
    public ResponseEntity<ApiResponseDto<Void>> deleteMember(
            @Parameter(description = "회원 ID", required = true)
            @PathVariable Long id) {
        try {
            memberService.deleteMember(id);
            return ResponseEntity.ok(ApiResponseDto.success("회원이 삭제되었습니다.", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.error(e.getMessage()));
        }
    }
}