package com.rgs.web_demo.config;

import com.slack.api.Slack;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class SlackConfig {

    // Slack Webhook URL 값
    @Value("${slack.webhook-url:}")
    private String webhookUrl;

    // Slack Bot Token
    @Value("${slack.bot-token:}")
    private String botToken;

    // Slack 기본 채널 값
    @Value("${slack.default-channel:#general}")
    private String defaultChannel;

    @Bean
    public Slack slack() {
        return Slack.getInstance();
    }
}