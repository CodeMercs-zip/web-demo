package com.rgs.web_demo.service;

import com.rgs.web_demo.config.SlackConfig;
import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import com.slack.api.model.Attachment;
import com.slack.api.webhook.Payload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SlackNotificationService {

    private final Slack slack;
    private final SlackConfig slackConfig;

    // 지정된 채널에 메시지 전송 (Bot Token 또는 Webhook 자동 선택)
    public void sendMessage(String channel, String message) {
        // Slack 설정 확인
        if (!isSlackConfigured()) {
            log.warn("Slack configuration is missing. Message not sent: {}", message);
            return;
        }

        try {
            // Bot Token이 설정된 경우 Bot API 사용
            if (slackConfig.getBotToken() != null && !slackConfig.getBotToken().isEmpty()) {
                sendMessageWithBotToken(channel, message);
                // Webhook URL이 설정된 경우 Webhook 사용
            } else if (slackConfig.getWebhookUrl() != null && !slackConfig.getWebhookUrl().isEmpty()) {
                sendMessageWithWebhook(message);
            } else {
                log.error("Neither bot token nor webhook URL is configured");
            }
        } catch (Exception e) {
            // 메시지 전송 실패 시 로그 출력
            log.error("Failed to send Slack message", e);
        }
    }

    // 기본 채널에 메시지 전송 (채널 지정 없이)
    public void sendMessage(String message) {
        sendMessage(slackConfig.getDefaultChannel(), message);
    }

    // 포맷된 메시지 전송 (제목, 색상 포함)
    public void sendFormattedMessage(String channel, String title, String message, String color) {
        // Slack 설정 확인
        if (!isSlackConfigured()) {
            log.warn("Slack configuration is missing. Message not sent: {}", message);
            return;
        }

        try {
            // Bot Token이 설정된 경우 Bot API로 포맷된 메시지 전송
            if (slackConfig.getBotToken() != null && !slackConfig.getBotToken().isEmpty()) {
                sendFormattedMessageWithBotToken(channel, title, message, color);
                // Webhook URL이 설정된 경우 Webhook으로 포맷된 메시지 전송
            } else if (slackConfig.getWebhookUrl() != null && !slackConfig.getWebhookUrl().isEmpty()) {
                sendFormattedMessageWithWebhook(title, message, color);
            }
        } catch (Exception e) {
            // 포맷된 메시지 전송 실패 시 로그 출력
            log.error("Failed to send formatted Slack message", e);
        }
    }


    // Bot Token을 사용한 일반 메시지 전송
    private void sendMessageWithBotToken(String channel, String message) throws IOException, SlackApiException {
        // 메시지 전송 요청 생성
        ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                .channel(channel)
                .text(message)
                .build();

        // Slack API 호출
        ChatPostMessageResponse response = slack.methods(slackConfig.getBotToken()).chatPostMessage(request);

        // 응답 결과 확인 및 로깅
        if (!response.isOk()) {
            log.error("Failed to send message to Slack: {}", response.getError());
        } else {
            log.info("Message sent to Slack channel: {}", channel);
        }
    }

    // Webhook을 사용한 일반 메시지 전송
    private void sendMessageWithWebhook(String message) throws IOException {
        // Webhook 페이로드 생성
        Payload payload = Payload.builder()
                .text(message)
                .build();

        // Webhook URL로 메시지 전송
        slack.send(slackConfig.getWebhookUrl(), payload);
        log.info("Message sent to Slack via webhook");
    }

    private void sendFormattedMessageWithBotToken(String channel, String title, String message, String color)
            throws IOException, SlackApiException {
        // Attachment 객체를 직접 생성하여 포맷된 메시지 작성
        Attachment attachment = Attachment.builder()
                .color(color)
                .title(title)
                .text(message)
                .build();

        // Slack API 요청 생성 (Bot Token 사용)
        ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                .channel(channel)
                .attachments(List.of(attachment))
                .build();

        // Slack API 호출
        ChatPostMessageResponse response = slack.methods(slackConfig.getBotToken()).chatPostMessage(request);

        // 응답 결과 확인 및 로깅
        if (!response.isOk()) {
            log.error("Failed to send formatted message to Slack: {}", response.getError());
        } else {
            log.info("Formatted message sent to Slack channel: {}", channel);
        }
    }

    // Webhook을 사용한 포맷된 메시지 전송 (JSON attachment 사용)
    private void sendFormattedMessageWithWebhook(String title, String message, String color) throws IOException {
        // Slack Webhook용 JSON 페이로드 직접 생성 (attachments 배열 포함)
        String payload = String.format(
                "{\"attachments\":[{\"color\":\"%s\",\"title\":\"%s\",\"text\":\"%s\",\"mrkdwn_in\":[\"text\"]}]}",
                color, title, message
        );

        // Raw JSON 문자열로 Webhook 전송
        slack.send(slackConfig.getWebhookUrl(), payload);
        log.info("Formatted message sent to Slack via webhook");
    }

    // Slack 설정이 올바르게 되어있는지 확인 (Bot Token 또는 Webhook URL 중 하나라도 있으면 OK)
    private boolean isSlackConfigured() {
        return (slackConfig.getBotToken() != null && !slackConfig.getBotToken().isEmpty()) ||
                (slackConfig.getWebhookUrl() != null && !slackConfig.getWebhookUrl().isEmpty());
    }

}