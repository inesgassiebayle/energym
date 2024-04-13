package org.austral.ing.lab1.dto;

import org.austral.ing.lab1.model.User;

public class LoginDto {
    private String username;
    private String password;
    LoginDto(String username, String password){
        this.username = username;
        this.password = password;
    }
    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }
}
