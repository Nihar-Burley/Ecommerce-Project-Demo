package com.company.common.dto.user.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {

    private String token;
    private String type;

    private long expiresAt;
    private long expiresIn;
}
