package org.austral.ing.lab1.model;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Activity {
    @Id
    @GeneratedValue(generator = "userGen", strategy = GenerationType.SEQUENCE)
    private Long activityId;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToMany
    @JoinTable(
            name = "activityRoom",
            joinColumns = @JoinColumn(name = "activityId"),
            inverseJoinColumns = @JoinColumn(name = "roomId")
    )
    private Set<Room> rooms;

    public Activity(String name){
        this.name = name;
    }

    public Activity(){

    }
}
