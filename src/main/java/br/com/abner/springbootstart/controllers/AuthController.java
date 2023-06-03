package br.com.abner.springbootstart.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.abner.springbootstart.data.vo.v1.AccountCredentialsVO;
import br.com.abner.springbootstart.services.AuthServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Auth", description = "Authentication Endpoint")
@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    AuthServices authServices;

    @SuppressWarnings("rawtypes")
    @Operation(summary = "Authenticates a user and returns a token")
    @PostMapping(value = "/signin")
    public ResponseEntity signIn(@RequestBody AccountCredentialsVO data) {
        if (isParamsNull(data))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");
        ResponseEntity token = authServices.signIn(data);
        if(token == null)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");
        return token;
    }

    @SuppressWarnings("rawtypes")
    @Operation(summary = "Refresh token for authenticated user and returns a token")
    @PutMapping(value = "/refresh/{username}")
    public ResponseEntity refreshToken(
            @PathVariable("username") String username,
            @RequestHeader("Authorization") String refreshToken) {
        if(isParamsNull(username, refreshToken))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");
        ResponseEntity token = authServices.refreshToken(username, refreshToken);
        return token;
    }

    private boolean isParamsNull(String username, String refreshToken) {
        return refreshToken == null || refreshToken.isBlank() ||
            username == null || username.isBlank();
    }

    private boolean isParamsNull(AccountCredentialsVO data) {
        return data == null 
            || data.getUsername() == null || data.getUsername().isBlank()
            || data.getPassword() == null || data.getPassword().isBlank();
    }
}
