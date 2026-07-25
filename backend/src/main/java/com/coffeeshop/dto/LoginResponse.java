package com.coffeeshop.dto;

public class LoginResponse {
    private String token;
    private String role;
    private String name;
    private Long id;

    public LoginResponse(String token, String role, String name, Long id) {
        this.token = token;
        this.role = role;
        this.name = name;
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
