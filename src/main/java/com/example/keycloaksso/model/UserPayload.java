package com.example.keycloaksso.model;

import lombok.Data;

@Data
public class UserPayload {
    private String username;
    private String password;
    private String email;
}
