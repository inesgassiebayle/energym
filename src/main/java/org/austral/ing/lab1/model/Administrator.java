package org.austral.ing.lab1.model;

import javax.persistence.*;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Administrator extends User {
    @OneToMany(mappedBy = "administrator", cascade = CascadeType.ALL)
    private Set<Room> rooms = new HashSet<>();

    public Set<Room> getRooms() {
        return rooms;
    }

    public Administrator() {
        super();
    }

    public Administrator(String firstName, String lastName, String email, String username, String password) {
        super(firstName, lastName, email, username, password);
    }
}

