package com.courseplatform.dto;

import lombok.*;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String username;
    private String role;
}
