package com.vijay.auth.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public record RefreshTokenRequest(
        String refreshToken
) {
}
