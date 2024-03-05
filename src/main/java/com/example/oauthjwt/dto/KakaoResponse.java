package com.example.oauthjwt.dto;

import java.util.Map;

public class KakaoResponse implements OAuth2Response {

    private final Map<String, Object> properties;

    private final Map<String, Object> account;

    private final String providerId;

    public KakaoResponse(Map<String, Object> attributes) {
        this.properties = (Map<String, Object>) attributes.get("properties");
        this.account = (Map<String, Object>) attributes.get("kakao_account");
        this.providerId = attributes.get("id").toString();
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getProviderId() {
        return this.providerId;
    }

    @Override
    public String getEmail() {
        return this.account.get("email").toString();
    }

    @Override
    public String getName() {
        return this.properties.get("nickname").toString();
    }

    @Override
    public String getProfileImage() {
        return this.properties.get("profile_image").toString();
    }
}
