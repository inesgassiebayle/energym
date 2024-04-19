package org.austral.ing.lab1.model;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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

    public String getName() {
        return name;
    }

    @ManyToMany(mappedBy = "activities")
    private Set<Room> rooms;

    public void setRoom(Room room){
        rooms.add(room);
    }

    public Set<Room> getRooms(){
        return rooms;
    }

    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL)
    private Set<Lesson> classes = new HashSet<>();

    public Set<Lesson> getClasses() {
        return classes;
    }

    @Column
    private boolean state;
    public boolean state(){return state;}
    public void deactivate(){this.state=false;}
    public void activate(){this.state=true;}
    public Activity(String name){
        this.name = name;
        this.rooms = new HashSet<>();
        this.state=true;
    }

    public Activity(){
        this.rooms= new HashSet<>();
        this.state = true;
    }

    public String asJson(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("activityId", this.activityId);
        jsonObject.addProperty("name", this.name);
        return jsonObject.toString();
    }


}
