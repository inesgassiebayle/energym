package org.austral.ing.lab1.model;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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

    public void setName(String name){
        this.name = name;
    }

    public String getName(){return name; }

    @Column(nullable = false)
    private Integer capacity;

    public Integer getCapacity(){
        return capacity;
    }

    public void setCapacity(Integer capacity){
        this.capacity = capacity;
    }

    @Column
    private boolean state;
    public boolean state(){return state;}
    public void deactivate(){this.state=false;}
    public void activate(){this.state=true;}

    @ManyToMany
    @JoinTable(
            name = "activityRoom",
            joinColumns = @JoinColumn(name = "roomId"),
            inverseJoinColumns = @JoinColumn(name = "activityId")
    )
    private Set<Activity> activities;

    public Set<Activity> getActivities(){
        return activities;
    }

    public void setActivity(Activity activity){
        activities.add(activity);
    }

    public void removeActivity(Activity activity){
        activities.remove(activity);
        activity.getRooms().remove(this);
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
    private Set<Lesson> classes = new HashSet<>();

    public Set<Lesson> getClasses(){
        return classes;
    }

    public Room(String name, Integer capacity){
        this.name = name;
        this.capacity = capacity;
        this.activities = new HashSet<>();
        this.state = true;
    }

    public Room(){
        this.activities = new HashSet<>();
        this.state = true;
    }

    public String asJson(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("roomId", this.roomId);
        jsonObject.addProperty("name", this.name);
        jsonObject.addProperty("capacitiy", this.capacity);
        JsonArray activitiesNames = new JsonArray();
        for (Activity activity : this.activities) {
            activitiesNames.add(activity.getName());
        }
        jsonObject.add("activities", activitiesNames);
        return jsonObject.toString();
    }
}
