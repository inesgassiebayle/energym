package org.austral.ing.lab1.model;

import javax.persistence.*;

@Entity
public class Professor extends User {
    public Professor() {
        super();
    }

    public Professor(String firstName, String lastName, String email, String username, String password) {
        super(firstName, lastName, email, username, password);
    }
}

