package com.mergeeats.userservice.dto;

import com.mergeeats.common.models.User;
import io.swagger.v3.oas.annotations.media.Schema;

public class AuthResponse {
    
    @Schema(example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkphbmUgRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
    private String token;
    
    @Schema(example = "Bearer")
    private String tokenType = "Bearer";
    
    private User user;
    
    @Schema(example = "Login successful")
    private String message;
    
    // Constructors
    public AuthResponse() {}
    
    public AuthResponse(String token, User user) {
        this.token = token;
        this.user = user;
    }
    
    public AuthResponse(String token, User user, String message) {
        this.token = token;
        this.user = user;
        this.message = message;
    }
    
    // Getters and Setters
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getTokenType() {
        return tokenType;
    }
    
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}