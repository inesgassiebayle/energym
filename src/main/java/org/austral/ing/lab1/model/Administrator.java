package org.austral.ing.lab1.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Administrator{

    @Id
    @GeneratedValue(generator = "userGen", strategy = GenerationType.SEQUENCE)
    private Long adminId;

    @OneToMany(mappedBy = "administrator", cascade = CascadeType.ALL)
    private Set<Room> rooms = new HashSet<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    private User user;

    public void setUser(User user){
        this.user=user;
    }


    public Set<Room> getRooms() {
        return rooms;
    }

    public Administrator() {}

}

//    @OneToMany(mappedBy = "administrator", cascade = CascadeType.ALL)
//    private Set<Room> rooms = new HashSet<>();
//
//    public Set<Room> getRooms() {
//        return rooms;
//    }
//
//    public Administrator() {
//        super();
//    }
//
//    public Administrator(String firstName, String lastName, String email, String username, String password) {
//        super(firstName, lastName, email, username, password);
//    }


