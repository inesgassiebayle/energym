package org.austral.ing.lab1.controller;

import com.google.gson.Gson;
import org.austral.ing.lab1.dto.ActivityDto;
import org.austral.ing.lab1.model.Activity;
import org.austral.ing.lab1.model.Room;
import org.austral.ing.lab1.queries.Activities;
import org.austral.ing.lab1.queries.Rooms;
import spark.Request;
import spark.Response;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

public class ActivityController {
    private final Activities activities;
    private final Rooms rooms;

    private final Gson gson = new Gson();

    public ActivityController() {
        this.activities = new Activities();
        this.rooms = new Rooms();
    }

    public String addActivity(Request req, Response res){
        ActivityDto activityDto = gson.fromJson(req.body(), ActivityDto.class);
        String name = activityDto.getName();
        Activity activity = activities.findActivityByName(name);
        if(activity != null){
            if(activity.state()){
                return "Activity already exists";
            }
            activity.activate();
        }
        else{
            activity = new Activity(name);
        }
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
        if(!activity.state()){
            return "Activity was already deleted";
        }
        /*
        List<Room> roomsWithActivity = rooms.findRoomsByActivity(name);
        for(Room room: roomsWithActivity){
            if(room.state()){
                room.removeActivity(activity);
                if(room.getActivities().isEmpty()){
                    room.deactivate();
                }
                rooms.persist(room);
            }
        }

         */
        activity.deactivate();
        activities.persist(activity);

        res.type("application/json");
        return activity.asJson();
    }

    public String getActivities(Request req, Response res){
        List<Activity> activities1 = activities.findAllActivities();
        List<String> names = new ArrayList<>();
        for(Activity activity: activities1){
            if(activity.state()) {
                names.add(activity.getName());
            }
        }
        res.type("application/json");
        return gson.toJson(names);
    }
}
