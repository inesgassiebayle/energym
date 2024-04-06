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

    public String asJson(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("activityId", this.activityId);
        jsonObject.addProperty("name", this.name);
        return jsonObject.toString();
    }


}
