package com.rgs.web_demo.service;

import com.rgs.web_demo.domain.Inquiry;
import com.rgs.web_demo.dto.request.InquiryRequestDto;
import com.rgs.web_demo.repository.InquiryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final SlackNotificationService slackNotificationService;

    // 문의 생성 및 등록
    @Transactional
    public void createInquiry(InquiryRequestDto requestDto) {

        Inquiry inquiry = Inquiry.builder()
                .fullName(requestDto.fullName())
                .email(requestDto.email())
                .subject(requestDto.subject())
                .message(requestDto.message())
                .phoneNumber(requestDto.phoneNumber())
                .build();

        // 데이터베이스에 문의 저장
        Inquiry savedInquiry = inquiryRepository.save(inquiry);

        // 저장된 문의에 대한 Slack 알림 전솤
        sendSlackNotification(savedInquiry);
    }

    // 문의 접수에 대한 Slack 알림 전송 (예외 처리는 SlackNotificationService 내부에서 처리)
    private void sendSlackNotification(Inquiry inquiry) {
        String slackMessage = formatInquiryForSlack(inquiry);
        
        // 문의 전용 채널로 포맷된 메시지 전송
        slackNotificationService.sendFormattedMessage(
                "#문의-알림방", // 문의 전용 채널
                "📝 새로운 문의가 접수되었습니다",
                slackMessage,
                "good" // 초록색 테두리
        );
        
        log.info("Slack notification sent for inquiry ID: {}", inquiry.getId());
    }

    // 문의 내용
    private String formatInquiryForSlack(Inquiry inquiry) {
        return """
                *이름:* %s
                *이메일:* %s
                *전화번호:* %s
                *제목:* %s
                *메시지: %s
                *접수시간:* %s
                """.formatted(
                inquiry.getFullName(),
                inquiry.getEmail(),
                inquiry.getPhoneNumber() != null ? inquiry.getPhoneNumber() : "없음",
                inquiry.getSubject(),
                inquiry.getMessage(),
                inquiry.getCreatedAt().toString()
        );
    }
}