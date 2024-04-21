package org.austral.ing.lab1.controller;

import com.google.gson.Gson;
import org.austral.ing.lab1.dto.LessonDeletionDto;
import org.austral.ing.lab1.dto.LessonNameTimeDateDto;
import org.austral.ing.lab1.dto.RoomCreationDto;
import org.austral.ing.lab1.model.Activity;
import org.austral.ing.lab1.model.Lesson;
import org.austral.ing.lab1.model.Room;
import org.austral.ing.lab1.queries.*;
import spark.Request;
import spark.Response;

import java.time.LocalDate;
import java.time.LocalTime;

public class LessonController2 {
    private final Lessons lessons;
    private final Activities activities;
    private final Professors professors;
    private final Rooms rooms;
    private final Gson gson = new Gson();

    public LessonController2() {
        this.lessons = new Lessons();
        this.activities = new Activities();
        this.professors = new Professors();
        this.rooms = new Rooms();
    }

    public String getLesson(Request req, Response res){
        LessonNameTimeDateDto dto = gson.fromJson(req.body(), LessonNameTimeDateDto.class);
        LocalDate date = dto.getDate();
        String name = dto.getName();
        LocalTime time = dto.getTime();

        if(name == null || date == null || time == null){
            res.status(400); // Bad Request
            return "Invalid input";
        }

        Lesson lesson = lessons.findLessonByNameDateAndTime(name, date, time);

        if(lesson == null){
            return "Lesson was not found";
        }

        return lesson.asJson();
    }
}
