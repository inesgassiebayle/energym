package org.austral.ing.lab1.controller;

import com.google.gson.Gson;
import org.austral.ing.lab1.dto.ActivityDto;
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

public class ActivityController {
    private final Activities activities;
    private final Rooms rooms;
    private final Lessons lessons;
    private final Gson gson = new Gson();

    public ActivityController() {
        this.activities = new Activities();
        this.rooms = new Rooms();
        this.lessons = new Lessons();
    }

    public String addActivity(Request req, Response res){
        ActivityDto activityDto = gson.fromJson(req.body(), ActivityDto.class);
        String name = activityDto.getName();
        if(name == null){
            res.status(400);
            return "Name is required";
        }
        Activity activity = activities.findActivityByName(name);
        if(activity != null){
            if(activity.state()){
                res.status(400);
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
            res.status(404);
            return "Activity does not exist";
        }
        if(!activity.state()){
            res.status(400);
            return "Activity was already deleted";
        }
        activity.deactivate();
        activities.persist(activity);

        List<Room> rooms2 = rooms.findRoomsByActivity(name);
        for(Room room: rooms2){
            boolean state = false;
            Set<Activity> activities1 = room.getActivities();
            for(Activity activity1: activities1){
                if(activity1.state()){
                    state = true;
                }
            }
            if(!state){
                room.deactivate();
                rooms.persist(room);
                Set<Lesson> lessons1 = room.getClasses();
                for(Lesson lesson1: lessons1){
                    lesson1.deactivate();
                    lessons.persist(lesson1);
                }
            }
        }
        List<Lesson> lesson2 = lessons.findLessonByActivity(name);
        for(Lesson lesson: lesson2){
            lesson.deactivate();
            lessons.persist(lesson);
        }
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
