package com.vijay.auth.dtos;

public record TokenResponse(
        String accessToken,
        String refreshToken,
        long expriresIn,
        String tokenType,
        UserDto user
) {
    public static  TokenResponse of(String accessToken,String refreshToken,long expriresIn,String tokenType,UserDto user){
        return new TokenResponse(accessToken,refreshToken,expriresIn,"Bearer",user);
    }
}
