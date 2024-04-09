package org.austral.ing.lab1.controller;

import com.google.gson.Gson;
import org.austral.ing.lab1.dto.ActivityDto;
import org.austral.ing.lab1.model.Activity;
import org.austral.ing.lab1.model.Room;
import org.austral.ing.lab1.querrys.Activities;
import org.austral.ing.lab1.querrys.Rooms;
import spark.Request;
import spark.Response;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

public class ActivityController {
    private final Activities activities;
    private final Rooms rooms;

    private final Gson gson = new Gson();

    public ActivityController(EntityManager entityManager) {
        this.activities = new Activities(entityManager);
        this.rooms = new Rooms(entityManager);
    }

    public String addActivity(Request req, Response res){
        ActivityDto activityDto = gson.fromJson(req.body(), ActivityDto.class);
        String name = activityDto.getName();
        if(activities.findActivityByName(name) != null){
            return "Activity already exists";
        }
        Activity activity = new Activity(name);
        activities.persist(activity);
        res.type("application/json");
        return activity.asJson();
    }

    public String deleteActivity(Request req, Response res){
        ActivityDto activityDto = gson.fromJson(req.body(), ActivityDto.class);
        String name = activityDto.getName();
        Activity activity = activities.findActivityByName(name);
        if(activity == null){
            return "Activity does not exist";
        }

        // Eliminar la actividad de todas las habitaciones que la contienen
        List<Room> roomsWithActivity = rooms.findRoomsByActivity(name);
        for(Room room: roomsWithActivity){
            room.removeActivity(activity);
            if(room.getActivities().isEmpty()){
                rooms.delete(room);
            }
            else{
                rooms.persist(room);
            }
        }
        activities.delete(activity);

        res.type("application/json");
        return activity.asJson();
    }

    public String getActivities(Request req, Response res){
        List<Activity> activities1 = activities.findAllActivities();
        List<String> names = new ArrayList<>();
        for(Activity activity: activities1){
            names.add(activity.getName());
        }
        res.type("application/json");
        return gson.toJson(names);
    }
}