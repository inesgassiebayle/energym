package org.austral.ing.lab1.controller;

import com.google.gson.Gson;
import org.austral.ing.lab1.dto.LessonDeletionDto;
import org.austral.ing.lab1.dto.RoomCreationDto;
import org.austral.ing.lab1.model.Activity;
import org.austral.ing.lab1.model.Lesson;
import org.austral.ing.lab1.model.Room;
import org.austral.ing.lab1.queries.*;
import spark.Request;
import spark.Response;

import java.time.LocalDate;

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
        LessonDeletionDto lessonDeletionDto = gson.fromJson(req.body(), LessonDeletionDto.class);
        LocalDate date = lessonDeletionDto.getDate();
        String name = lessonDeletionDto.getName();

        if(name == null || date == null){
            res.status(400); // Bad Request
            return "Invalid input";
        }

        Lesson lesson = lessons.findLessonByNameAndDate(name, date);

        if(lesson == null){
            return "Lesson was not found";
        }

        return lesson.asJson();
    }
}
