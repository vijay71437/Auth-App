package com.vijay.auth.services;

import com.vijay.auth.dtos.UserDto;

public interface UserService {

    UserDto createUser(UserDto userDto);
    UserDto getUserEmail(String email);
    UserDto updateUser(UserDto userDto,String userId);
    void deleteUser(String userId);
    UserDto getUserById(String userId);
    Iterable<UserDto> getAllUsers();
}
