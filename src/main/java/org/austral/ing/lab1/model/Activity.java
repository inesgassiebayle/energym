package org.austral.ing.lab1.model;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Activity {
    @Id
    @GeneratedValue(generator = "userGen", strategy = GenerationType.SEQUENCE)
    private Long activityId;

    public Long getActivityId() {
        return activityId;
    }

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToMany
    @JoinTable(
            name = "activityRoom",
            joinColumns = @JoinColumn(name = "activityId"),
            inverseJoinColumns = @JoinColumn(name = "roomId")
    )
    private Set<Room> rooms;

    public Set<Room> getRooms(){
        return rooms;
    }

    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL)
    private Set<Class> classes = new HashSet<>();

    public Set<Class> getClasses() {
        return classes;
    }


    public Activity(String name){
        this.name = name;
        this.rooms = new HashSet<>();
    }

    public Activity(){
        this.rooms= new HashSet<>();
    }


}
