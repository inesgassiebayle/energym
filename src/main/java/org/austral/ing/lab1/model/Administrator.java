package org.austral.ing.lab1.model;

import javax.persistence.*;
import javax.persistence.*;

@Entity
public class Administrator extends User {
    public Administrator() {
        super();
    }

    public Administrator(String firstName, String lastName, String email, String username, String password) {
        super(firstName, lastName, email, username, password);
    }
}

