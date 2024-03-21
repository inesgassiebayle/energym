package org.austral.ing.lab1.model;

import javax.persistence.*;
import javax.validation.constraints.Pattern;

@Entity
public abstract class User {
    @Id
    @GeneratedValue(generator = "userGen", strategy = GenerationType.SEQUENCE)
    private Long userId;

    @Column()
    private String firstName;

    @Column()
    private String lastName;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "La contraseña debe tener al menos 8 caracteres, incluyendo al menos una letra y un número")
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    public User(String firstName, String lastName, String email, String username, String password){
        this.firstName=firstName;
        this.lastName=lastName;
        this.email=email;
        this.username=username;
        this.password=password;
    }

    public User() {

    }
}
