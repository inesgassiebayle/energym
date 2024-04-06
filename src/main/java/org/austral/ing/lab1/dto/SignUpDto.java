package org.austral.ing.lab1.dto;

public class SignUpDto {
    private  String firstName;
    private  String lastName;
    private  String email;
    private  String username;
    private  String password;
    SignUpDto(String firstName, String lastName, String email, String username, String password){
        this.firstName=firstName;
        this.lastName=lastName;
        this.email=email;
        this.username=username;
        this.password=password;
    }
    public SignUpDto(){}
    public String getFirstName() {
        return firstName;
    }
    public String getEmail() {
        return email;
    }
    public String getLastName(){
        return lastName;
    }
    public String getPassword(){
        return password;
    }
    public String getUsername(){
        return username;
    }
}
