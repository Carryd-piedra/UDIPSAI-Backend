package com.ucacue.udipsai.security.dto;

import lombok.Data;

@Data
public class TokenRefreshRequest {
    private String refreshToken;
}
