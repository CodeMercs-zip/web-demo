package com.rgs.web_demo.controller;

import com.rgs.web_demo.dto.request.InquiryRequestDto;
import com.rgs.web_demo.dto.response.ApiResponseDto;
import com.rgs.web_demo.service.InquiryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "Inquiry", description = "문의 관리 API")
@Slf4j
@RestController
@RequestMapping("/api/inquiries")
@RequiredArgsConstructor
public class InquiryController extends BaseController {

    private final InquiryService inquiryService;

    @Operation(summary = "문의 접수", description = "새로운 문의를 접수하고 Slack으로 알림을 전송합니다")
    @PostMapping
    public ResponseEntity<ApiResponseDto<Void>> createInquiry(@Valid @RequestBody InquiryRequestDto requestDto) {

        inquiryService.createInquiry(requestDto);

        return ok(null);
    }
}