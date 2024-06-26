package org.austral.ing.lab1.model;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.persistence.*;
import javax.validation.constraints.Pattern;

@Entity
public class User{
    @Id
    @GeneratedValue(generator = "userGen", strategy = GenerationType.SEQUENCE)
    private Long userId;

    public Long getId() {
        return userId;
    }

    public void setId(Long id) {
        this.userId = id;
    }

    @Column()
    private String firstName;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Column()
    private boolean state;

    public void activate(){
        this.state = true;
    }

    public void deactivate(){
        this.state = false;
    }

    public boolean state(){
        return state;
    }

    @Column()
    private String lastName;

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Column(nullable = false, unique = true)
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Column
    private UserType type;

    public UserType getType() {
        return type;
    }

    public void setType(UserType type) {
        this.type = type;
    }

    @Column(nullable = false)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "La contraseña debe tener al menos 8 caracteres, incluyendo al menos una letra y un número")
    private String password;

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    @Column(nullable = false, unique = true)
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User(String firstName, String lastName, String email, String username, String password){
        setUsername(username);
        setLastName(lastName);
        setFirstName(firstName);
        setEmail(email);
        setPassword(password);
        activate();
    }

    public User(){

    }

    public String asJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", this.userId);
        jsonObject.addProperty("firstName", this.firstName);
        jsonObject.addProperty("lastName", this.lastName);
        jsonObject.addProperty("username", this.username);
        jsonObject.addProperty("type", String.valueOf(this.type));
        jsonObject.addProperty("email", this.email);
        return jsonObject.toString();
    }

}