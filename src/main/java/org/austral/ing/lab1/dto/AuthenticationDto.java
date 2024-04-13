package org.austral.ing.lab1.dto;

import org.austral.ing.lab1.model.User;

public class AuthenticationDto {
    private String username;
    private String password;
    private String token;
    private String email;
    private String type;
    public AuthenticationDto(User user, String token){
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.token = token;
        this.email = user.getEmail();
        this.type = user.getType().toString();
    }

    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }
    public String getType(){
        return type;
    }
    public String getEmail(){
        return email;
    }

    public String getToken(){
        return token;
    }

}
