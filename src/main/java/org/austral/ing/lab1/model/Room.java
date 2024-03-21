package org.austral.ing.lab1.model;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Room {
    @Id
    @GeneratedValue(generator = "userGen", strategy = GenerationType.SEQUENCE)
    private Long roomId;

    public Long getRoomId(){
        return roomId;
    }

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private Integer capacity;

    @ManyToMany(mappedBy = "rooms")
    private Set<Activity> activities;

    public Set<Activity> getActivities(){
        return activities;
    }

    @ManyToOne
    @JoinColumn(name = "administratorId")
    private Administrator administrator;

    public Administrator getAdministrator() {
        return administrator;
    }

    public void setAdministrator(Administrator administrator) {
        this.administrator = administrator;
    }

    @OneToMany(mappedBy = "room")
    private Set<Class> classes = new HashSet<>();

    public Set<Class> getClasses(){
        return classes;
    }

    public Room(String name, Integer capacity){
        this.name = name;
        this.capacity = capacity;
        this.activities = new HashSet<>();
    }

    public Room(){
        this.activities = new HashSet<>();
    }

}
