package com.financeapp.service;

import com.financeapp.dto.RegistrationDto;
import com.financeapp.model.User;

public interface UserService {
    User register(RegistrationDto dto);
    User findByUsername(String username);
    boolean usernameExists(String username);
    boolean emailExists(String email);
}
