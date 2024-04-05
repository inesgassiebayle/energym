package org.austral.ing.lab1.controller;

import com.google.gson.Gson;
import org.austral.ing.lab1.querrys.Activities;
import org.austral.ing.lab1.querrys.Rooms;

import org.austral.ing.lab1.dto.CreateRoomDto;
import org.austral.ing.lab1.model.*;
import spark.Request;
import spark.Response;

import javax.persistence.EntityManager;

public class RoomsController {
    private final Rooms rooms;
    private final Gson gson = new Gson();
    private final Activities activities;

    public RoomsController(EntityManager entityManager) {
        this.rooms = new Rooms(entityManager);
        this.activities = new Activities(entityManager);
    }

    public String roomCreation(Request req, Response res) {

        CreateRoomDto roomDto = gson.fromJson(req.body(), CreateRoomDto.class);
        if(rooms.findRoomByName(roomDto.getName())!=null){
            return "Room already exists";
        }
        String[] activities2 = roomDto.getActivities();
        Room room = new Room(roomDto.getName(), roomDto.getCapacity());
        for(String activity: activities2){
            if(activities.findActivityByName(activity)!=null){
                Activity activity1 = activities.findActivityByName(activity);
                room.setActivity(activity1);
                activity1.setRoom(room);
            }
        }
        rooms.persist(room);
        res.type("application/json");
        return room.asJson();
    }

}
