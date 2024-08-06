package com.beginsecure.usersbchallenge.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/token")
public class TokenController {

    @Autowired
    private TokenService tokenService;

    @GetMapping("validateToken")
    public String validateToken(@RequestBody String requestToken) {
        if (this.tokenService.isValidToken(requestToken))
            return "Token is Valid!";
        else
            return  "Token is Invalid!";
    }

    @GetMapping("generateToken")
    public String generateToken() {
        this.tokenService.setToken();
        return this.tokenService.getToken();
    }

    @GetMapping("getToken")
    public String getToken() {
        return this.tokenService.getToken();
    }
}
