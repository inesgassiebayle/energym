package org.austral.ing.lab1.model;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Room {
    @Id
    @GeneratedValue(generator = "userGen", strategy = GenerationType.SEQUENCE)
    private Long roomId;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private Integer capacity;

    @ManyToMany(mappedBy = "rooms")
    private Set<Activity> activities;

    public Room(String name, Integer capacity){
        this.name = name; this.capacity=capacity;
    }

    public Room(){

    }


}
