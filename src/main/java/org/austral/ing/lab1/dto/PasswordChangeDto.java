package org.austral.ing.lab1.dto;

public class PasswordChangeDto {
    String username;
    String password;
    String passwordConfirmation;
    public PasswordChangeDto(String username, String password, String passwordConfirmation){
        this.username = username;
        this.password = password;
        this.passwordConfirmation = passwordConfirmation;
    }
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
    public String getPasswordConfirmation(){
        return passwordConfirmation;
    }
}
