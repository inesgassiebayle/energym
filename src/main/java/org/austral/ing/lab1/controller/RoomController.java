package org.austral.ing.lab1.controller;

import com.google.gson.Gson;
import org.austral.ing.lab1.dto.ActivityDto;
import org.austral.ing.lab1.dto.RoomCreationDto;
import org.austral.ing.lab1.dto.RoomDeletionDto;
import org.austral.ing.lab1.model.Activity;
import org.austral.ing.lab1.model.Room;
import org.austral.ing.lab1.querrys.Activities;
import org.austral.ing.lab1.querrys.Rooms;
import spark.Request;
import spark.Response;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

public class RoomController {
    private final Rooms rooms;
    private final Activities activities;
    private final Gson gson = new Gson();
    public RoomController(EntityManager entityManager) {
        this.rooms = new Rooms(entityManager);
        this.activities = new Activities(entityManager);
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

        Room room = new Room(name, capacity);

        for(String activity: activities1){
            room.setActivity(activities.findActivityByName(activity));
        }

        rooms.persist(room);

        res.type("application/json");
        return room.asJson();
    }

    public String deleteRoom(Request req, Response res){
        String name = req.params(":name");
        if(name == null){
            return "Invalid input";
        }
        if(rooms.findRoomByName(name)==null){
            return "Room does not exist";
        }
        Room room = rooms.findRoomByName(name);
        rooms.delete(room);
        res.type("application/json");
        return room.asJson();
    }

    public String getRooms(Request req, Response res){
        List<Room> rooms1 = rooms.findAllRooms();
        List<String> names = new ArrayList<>();
        for(Room room: rooms1){
            names.add(room.getName());
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
}
