package com.springboot.socialhub_api.api.service;


import com.springboot.socialhub_api.api.model.LoginCredentials;
import com.springboot.socialhub_api.api.model.LoginResponse;
import com.springboot.socialhub_api.api.model.User;
import com.springboot.socialhub_api.api.repositories.UserRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private final Map<String,Integer> loggedInTokens;
    private final UserRepository repository;

    @Autowired
    public AuthService(UserRepository repository) {
        this.loggedInTokens = new HashMap<>();
        this.repository = repository;
    }

    public LoginResponse login(LoginCredentials loginCredentials){
        Optional<User> user;
        loginCredentials.setPassword(DigestUtils.sha256Hex(loginCredentials.getPassword()));
        user=this.repository.findByEmailAndPassword(loginCredentials.getEmail(), loginCredentials.getPassword());

        if(user.isPresent()){
            String token = UUID.randomUUID().toString();
            int userId = user.get().getId();
            this.loggedInTokens.put(token,userId);
            return new LoginResponse(token,userId);
        }
        return null;
    }

    public boolean isLoggedIn(String token){
        return this.loggedInTokens.containsKey(token);
    }

    public void register(User user){
        user.setPassword(DigestUtils.sha256Hex(user.getPassword()));
        repository.save(user);
    }
}
