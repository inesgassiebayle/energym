package org.austral.ing.lab1.dto;

public class SignupUserDto {

    private String email;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    public SignupUserDto() {
    }

    public SignupUserDto(String email, String username, String password, String firstName, String lastName) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public String getLastName() {
        return lastName;
    }
    public String getFirstName() {
        return firstName;
    }

}
