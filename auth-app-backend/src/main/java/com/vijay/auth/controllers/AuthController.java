package com.vijay.auth.controllers;

import com.vijay.auth.dtos.LoginRequest;
import com.vijay.auth.dtos.UserDto;
import com.vijay.auth.dtos.TokenResponse;
import com.vijay.auth.entities.RefreshToken;
import com.vijay.auth.entities.User;
import com.vijay.auth.repositories.RefreshTokenRepository;
import com.vijay.auth.repositories.UserRepository;
import com.vijay.auth.security.CookieService;
import com.vijay.auth.security.JwtService;
import com.vijay.auth.services.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

import org.apache.catalina.webresources.TomcatJarInputStream;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final ModelMapper modelMapper;
    private final RefreshTokenRepository refreshTokenRepository;

    private final CookieService cookieService;

    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@RequestBody UserDto userDto){
        return ResponseEntity.ok(authService.registerUser(userDto));
    }
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response){
        //authenticate
        Authentication authentication=  authenticate(loginRequest);
        User user=userRepository.findByEmail(loginRequest.email()).orElseThrow(()->new BadCredentialsException("Invalide Username or password"));
        if(!user.isEnabled()){
            throw new DisabledException("User is disabled");
        }
        String jti= UUID.randomUUID().toString();
        var refreshTokenOb= RefreshToken.builder().jti(jti).user(user).createdAt(Instant.now()).expiresAt(Instant.now().plusSeconds(jwtService.getRefreshTtlSeconds())).revoked(false).build();
        refreshTokenRepository.save(refreshTokenOb);
        //generate token
        String accessToken= jwtService.generateAccessToken(user);
        String refreshToken=jwtService.generateRefreshToken(user,refreshTokenOb.getJti());

        cookieService.attachRefreshCookie(response,refreshToken,(int)jwtService.getRefreshTtlSeconds());
        cookieService.addNoStoreHeaders(response);
       TokenResponse tokenResponse= TokenResponse.of(accessToken,refreshToken, jwtService.getAccessTtlSeconds(),"Bearer",modelMapper.map(user,UserDto.class));
       return ResponseEntity.ok(tokenResponse);

    }

    private Authentication authenticate(LoginRequest loginRequest) {
        try{
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.email(),loginRequest.password()));
        }catch (Exception e){
            throw new BadCredentialsException("Invalid Username or password");
        }
    }

}
