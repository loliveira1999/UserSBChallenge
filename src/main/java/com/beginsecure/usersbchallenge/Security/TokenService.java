package com.beginsecure.usersbchallenge.Security;

import java.util.UUID;

import org.springframework.stereotype.Service;


@Service
public class TokenService {
    private String token;

    public TokenService() {
        this.token = UUID.randomUUID().toString();
    }

    public void setToken() {
        this.token = UUID.randomUUID().toString();
    }

    public boolean isValidToken(String requestToken) {
        return token != null && !token.isBlank() && token.equals(requestToken);
    }

    public String getToken() {
        return this.token;
    }
}
