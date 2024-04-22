package org.austral.ing.lab1.controller;

import com.google.gson.Gson;
import org.austral.ing.lab1.dto.RoomCreationDto;
import org.austral.ing.lab1.dto.RoomDeletionDto;
import org.austral.ing.lab1.dto.RoomModifyDto;
import org.austral.ing.lab1.model.Activity;
import org.austral.ing.lab1.model.Lesson;
import org.austral.ing.lab1.model.Room;
import org.austral.ing.lab1.queries.Activities;
import org.austral.ing.lab1.queries.Lessons;
import org.austral.ing.lab1.queries.Rooms;
import spark.Request;
import spark.Response;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RoomController {
    private final Rooms rooms;
    private final Activities activities;
    private final Lessons lessons;
    private final Gson gson = new Gson();
    public RoomController() {
        this.rooms = new Rooms();
        this.activities = new Activities();
        this.lessons = new Lessons();
    }

    public String addRoom(Request req, Response res){
        RoomCreationDto roomDto = gson.fromJson(req.body(), RoomCreationDto.class);
        String name = roomDto.getName();
        Integer capacity = roomDto.getCapacity();
        String[] activities1 = roomDto.getActivities();
        if(name == null || capacity == null){
            res.status(400);
            return "Invalid input";
        }
        Room room = rooms.findRoomByName(name);
        if(room!=null){
            if(room.state()){
                res.status(400);
                return "Room already exists";
            }
            room.activate();
            if (capacity > 0) {
                room.setCapacity(capacity);
            } else {
                res.status(400);
                return "Invalid capacity";
            }
            room.getActivities().clear();
            for(String activityName: activities1){
                Activity activity = activities.findActivityByName(activityName);
                if (activity != null) {
                    if(activity.state()){
                        room.getActivities().add(activity);
                    }
                    else{
                        res.status(400);
                        return "Activity named " + activityName + " does not exist";
                    }
                } else {
                    res.status(400);
                    return "Activity named " + activityName + " does not exist";
                }
            }
            rooms.persist(room);
            res.type("application/json");
            return room.asJson();
        }
        if(capacity<0){
            res.status(400);
            return "Invalid capacity";
        }
        if(activities1.length == 0){
            res.status(400);
            return "No activities selected";
        }
        room = new Room(name, capacity);
        for(String activity: activities1){
            Activity activity1 = activities.findActivityByName(activity);
            if(activity1 == null){
                res.status(400);
                return "Activity does not exist";
            }
            if(!activity1.state()){
                res.status(400);
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
            res.status(400);
            return "Invalid input";
        }
        Room room = rooms.findRoomByName(name);
        if(room == null){
            res.status(404);
            return "Room does not exist";
        }
        Set<Lesson> lessons2 = room.getClasses();
        for(Lesson lesson: lessons2){
            lesson.deactivate();
            lessons.persist(lesson);
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
            res.status(400);
            return "Room does not exist";
        }
        for(Activity activity: room.getActivities()){
            if(activity.state()){
                names.add(activity.getName());
            }
        }
        res.type("application/json");
        return gson.toJson(names);
    }

    public String getRoomCapacity(Request req, Response res){
        Room room = rooms.findRoomByName(req.params(":name"));
        if(room == null){
            res.status(400);
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
            res.status(400);
            return "No room selected";
        }
        Room room = rooms.findRoomByName(name);
        if(room == null){
            res.status(400);
            return "Room does not exist";
        }
        if(!room.state()){
            res.status(400);
            return "Room does not exist";
        }
        if (!newName.isBlank() && !newName.equalsIgnoreCase(name)) {
            Room room2 = rooms.findRoomByName(newName);
            if (room2 != null) {
                res.status(400);
                return "New room name already exists";
            }
            room.setName(newName);
        }
        return fill(res, room, capacity, activities1);
    }

    private String fill(Response res, Room room, Integer capacity, String[] activities1){
        if (capacity != null && capacity > 0) {
            room.setCapacity(capacity);
        } else {
            res.status(400);
            return "Invalid capacity";
        }

        if(activities1.length==0){
            res.status(400);
            return "Invalid activities";
        }

        room.getActivities().clear();
        for(String activityName: activities1){
            Activity activity = activities.findActivityByName(activityName);
            if (activity != null) {
                if(activity.state()){
                    room.getActivities().add(activity);
                }
                else{
                    res.status(400);
                    return "Activity named " + activityName + " does not exist";
                }
            } else {
                res.status(400);
                return "Activity named " + activityName + " does not exist";
            }
        }
        rooms.persist(room);
        res.type("application/json");
        return room.asJson();
    }

}
