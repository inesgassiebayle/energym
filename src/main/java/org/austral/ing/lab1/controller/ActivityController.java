package org.austral.ing.lab1.controller;

import com.google.gson.Gson;
import org.austral.ing.lab1.dto.ActivityDto;
import org.austral.ing.lab1.model.Activity;
import org.austral.ing.lab1.model.Lesson;
import org.austral.ing.lab1.model.Room;
import org.austral.ing.lab1.queries.Activities;
import org.austral.ing.lab1.queries.Lessons;
import org.austral.ing.lab1.queries.Rooms;
import org.austral.ing.lab1.service.LessonService;
import spark.Request;
import spark.Response;

import javax.persistence.EntityManager;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.time.LocalTime.*;

public class ActivityController {
    private final Activities activities;
    private final Rooms rooms;
    private final Lessons lessons;
    private final Gson gson = new Gson();
    private final LessonService lessonService;

    public ActivityController(EmailSender emailSender, ReminderService reminderService) {
        this.activities = new Activities();
        this.rooms = new Rooms();
        this.lessons = new Lessons();
        this.lessonService = new LessonService(emailSender, reminderService);
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
        String name = req.params(":name");
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

        LocalDate now = LocalDate.now();

        // Deactivate future lessons only
        List<Lesson> lessonsAssociated = lessons.findLessonByActivity(name);
        for(Lesson lesson: lessonsAssociated){
            if(!lesson.getStartDate().isBefore(now)) {
                lessonService.deleteLesson(lesson);
            }
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
