package com.rgs.web_demo.dto.oauth;

import java.util.Map;

public class NaverUserInfo extends OAuth2UserInfo {

    public NaverUserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    @SuppressWarnings("unchecked")
    public String getId() {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        if (response != null) {
            return (String) response.get("id");
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String getName() {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        if (response != null) {
            return (String) response.get("name");
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String getEmail() {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        if (response != null) {
            return (String) response.get("email");
        }
        return null;
    }

    @Override
    public String getProvider() {
        return "naver";
    }
}