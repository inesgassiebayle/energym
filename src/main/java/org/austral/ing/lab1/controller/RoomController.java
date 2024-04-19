package org.austral.ing.lab1.controller;

import com.google.gson.Gson;
import org.austral.ing.lab1.dto.RoomCreationDto;
import org.austral.ing.lab1.dto.RoomDeletionDto;
import org.austral.ing.lab1.dto.RoomModifyDto;
import org.austral.ing.lab1.model.Activity;
import org.austral.ing.lab1.model.Room;
import org.austral.ing.lab1.queries.Activities;
import org.austral.ing.lab1.queries.Rooms;
import spark.Request;
import spark.Response;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

public class RoomController {
    private final Rooms rooms;
    private final Activities activities;
    private final Gson gson = new Gson();
    public RoomController() {
        this.rooms = new Rooms();
        this.activities = new Activities();
    }

    public String addRoom(Request req, Response res){
        RoomCreationDto roomDto = gson.fromJson(req.body(), RoomCreationDto.class);
        String name = roomDto.getName();
        Integer capacity = roomDto.getCapacity();
        String[] activities1 = roomDto.getActivities();

        if(name == null || capacity == null){
            return "Invalid input";
        }

        if(rooms.findRoomByName(name)!=null){
            return "Room already exists";
        }

        if(capacity<0){
            return "Invalid capacity";
        }

        if(activities1.length == 0){
            return "No activities selected";
        }

        Room room = new Room(name, capacity);

        for(String activity: activities1){
            Activity activity1 = activities.findActivityByName(activity);
            if(activity1 == null){
                return "Activity does not exist";
            }
            room.setActivity(activity1);
        }

        rooms.persist(room);

        res.type("application/json");
        return room.asJson();
    }
    public String deleteRoom(Request req, Response res){
        String name = req.params(":name");

        if(name == null || name.isBlank()){
            return "Invalid input";
        }

        Room room = rooms.findRoomByName(name);
        if(room == null){
            return "Room does not exist";
        }

        room.deactivate();
        rooms.persist(room);

        res.type("application/json");

        return room.asJson();
    }


    public String getRooms(Request req, Response res){
        List<Room> rooms1 = rooms.findAllRooms();
        List<String> names = new ArrayList<>();
        for(Room room: rooms1){
            if(room.state()){
                names.add(room.getName());
            }
        }
        res.type("application/json");
        return gson.toJson(names);
    }

    public String getRoomActivities(Request req, Response res){
        Room room = rooms.findRoomByName(req.params(":name"));
        List<String> names = new ArrayList<>();
        if(room == null){
            return "Room does not exist";
        }
        for(Activity activity: room.getActivities()){
            names.add(activity.getName());
        }
        res.type("application/json");
        return gson.toJson(names);
    }

    public String getRoomCapacity(Request req, Response res){
        Room room = rooms.findRoomByName(req.params(":name"));
        if(room == null){
            return "Room does not exist";
        }
        res.type("application/json");
        return gson.toJson((room.getCapacity()).toString());
    }

    public String modifyRoom(Request req, Response res){
        RoomModifyDto roomDto = gson.fromJson(req.body(), RoomModifyDto.class);
        String name = roomDto.getName();
        String newName = roomDto.getNewName();
        Integer capacity = roomDto.getCapacity();
        String[] activities1 = roomDto.getActivities();

        if(name.isBlank()){
            return "No room selected";
        }

        Room room = rooms.findRoomByName(name);

        if(room == null){
            return "Room does not exist";
        }



        if (!newName.isBlank() && !newName.equalsIgnoreCase(name)) {
            if (rooms.findRoomByName(newName) != null) {
                return "New room name already exists";
            }
            room.setName(newName);
        }

        if (capacity != null && capacity > 0) {
            room.setCapacity(capacity);
        } else {
            return "Invalid capacity";
        }


        room.getActivities().clear();
        for(String activityName: activities1){
            Activity activity = activities.findActivityByName(activityName);
            if (activity != null) {
                room.getActivities().add(activity);
            } else {
                return "Activity named " + activityName + " does not exist";
            }
        }

        rooms.persist(room);

        res.type("application/json");
        return room.asJson();
    }

}
