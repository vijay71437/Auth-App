package com.vijay.auth.services;

import com.vijay.auth.dtos.UserDto;

public interface AuthService {
    UserDto registerUser(UserDto userDto);

}
