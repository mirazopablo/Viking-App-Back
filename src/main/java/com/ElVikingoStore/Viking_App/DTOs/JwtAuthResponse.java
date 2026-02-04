package com.ElVikingoStore.Viking_App.DTOs;


import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "JwtAuthResponse", description = "JWT Authentication Response Dto")
public class JwtAuthResponse {
    @Schema(description = "JWT Access Token", example="eyJhbGciOi")
    private String accessToken;
    @Schema(description = "Token Type", example="Bearer")
    private String tokenType = "Bearer";

    public JwtAuthResponse(String accessToken) {
        this.accessToken = accessToken;
    }
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }
}