package com.example.service;

import com.example.dto.RegisterRequest;
import com.example.dto.UserResponse;

public interface UserService {

    UserResponse register(RegisterRequest request);
}
