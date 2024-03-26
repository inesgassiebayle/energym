package org.austral.ing.lab1.model;

import com.google.gson.Gson;

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

    @Column(nullable = false)
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
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

    public User(){}

    public static UserBuilder create(String email, String username) {
        return new UserBuilder(email, username);
    }

    private User(UserBuilder builder) {
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.password = builder.password;
        this.email = builder.email;
    }

    public static User fromJson(String json) {
        final Gson gson = new Gson();
        return gson.fromJson(json, User.class);
    }

    public String asJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static class UserBuilder {
        private final String email;
        private String firstName;
        private String lastName;
        private String password;
        private String type;
        private String username;

        public UserBuilder(String email, String username) {
            this.email = email;
            this.username = username;
        }

        public UserBuilder password(String password) {
            this.password = password;
            return this;
        }

        public UserBuilder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public UserBuilder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public UserBuilder type(String type) {
            this.type = type;
            return this;
        }

        public User build() {
            return new User(this);
        }

    }

}
