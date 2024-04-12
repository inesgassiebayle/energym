package org.austral.ing.lab1.controller;
import com.google.gson.Gson;
import org.austral.ing.lab1.dto.ConcurrentLessonDto;
import org.austral.ing.lab1.dto.LessonDto;
import org.austral.ing.lab1.model.Lesson;
import org.austral.ing.lab1.model.Activity;
import org.austral.ing.lab1.model.Professor;
import org.austral.ing.lab1.model.Room;
import org.austral.ing.lab1.queries.*;

import spark.Request;
import spark.Response;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class LessonController {
    private final Lessons lessons;
    private final Activities activities;
    private final Professors professors;
    private final Rooms rooms;
    private final Gson gson = new Gson();

    public LessonController(EntityManager entityManager) {
        this.lessons = new Lessons(entityManager);
        this.activities = new Activities(entityManager);
        this.professors = new Professors(entityManager);
        this.rooms = new Rooms(entityManager);
    }
    public String addSingleLesson(Request req, Response res) {
        LessonDto lessonDto = gson.fromJson(req.body(), LessonDto.class);

        Lesson lesson = new Lesson(
                lessonDto.getName(),
                lessonDto.getTime(),
                lessonDto.getStartDate()
        );

        //get activity
        Activity activity = getActivityByName(lessonDto.getActivity());
        if (activity != null) {
            lesson.setActivity(activity);
        } else {
            // Handle case where activity is not found
            res.status(404);
            return "Activity not found";
        }

        //get professor
        Professor professor = getProfessorByUsername(lessonDto.getProfessor());
        if (professor != null) {
            lesson.setProfessor(professor);
        } else {
            // Handle case where professor is not found
            res.status(404);
            return "Professor not found";
        }

        //get room
        Room room = rooms.findRoomByName(lessonDto.getRoomName());

        if (room != null) {
            lesson.setRoom(room);
        } else {
            // Handle case where professor is not found
            res.status(404);
            return "Room not found";
        }


        lessons.persist(lesson);
        res.type("application/json");
        return lesson.asJson();
    }

    private Activity getActivityByName(String name) {
        return activities.findActivityByName(name);
    }

    private Professor getProfessorByUsername(String username) {
        return professors.findProfessorByUsername(username);
    }
}
