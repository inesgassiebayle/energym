package org.austral.ing.lab1.controller;
import com.google.gson.Gson;
import org.austral.ing.lab1.dto.LessonDto;
import org.austral.ing.lab1.dto.ConcurrentLessonDto;
import org.austral.ing.lab1.model.Lesson;
import org.austral.ing.lab1.model.Activity;
import org.austral.ing.lab1.model.Professor;
import org.austral.ing.lab1.queries.Lessons;
import org.austral.ing.lab1.queries.Professors;
import org.austral.ing.lab1.queries.Users;
import org.austral.ing.lab1.queries.Activities;

import spark.Request;
import spark.Response;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class LessonController {
    private final Lessons lessons;
    private final Gson gson = new Gson();
    private final Activities activities;
    private final Professors professors;

    public LessonController(EntityManager entityManager) {
        this.lessons = new Lessons(entityManager);
        this.activities = new Activities(entityManager);
        this.professors = new Professors(entityManager);
    }
    public String addSingleLessonWithActivityAndProfessor(Request req, Response res) {
        LessonDto lessonDto = gson.fromJson(req.body(), LessonDto.class);

        Lesson lesson = new Lesson(
                lessonDto.getName(),
                lessonDto.getTime(),
                lessonDto.getStartDate()
        );

        // Fetch and set Activity
        Activity activity = getActivityByName(lessonDto.getActivity());
        if (activity != null) {
            lesson.setActivity(activity);
        } else {
            // Handle case where activity is not found
            res.status(404);
            return "Activity not found";
        }

        // Fetch and set Professor
        Professor professor = getProfessorByUsername(lessonDto.getProfessor());
        if (professor != null) {
            lesson.setProfessor(professor);
        } else {
            // Handle case where professor is not found
            res.status(404);
            return "Professor not found";
        }

        lessons.persist(lesson);
        res.type("application/json");
        return lesson.asJson();
    }

    public String addConcurrentLessonsWithActivityAndProfessor(Request req, Response res) {
        // Parse the JSON body to the ConcurrentLessonDto
        ConcurrentLessonDto lessonDto = gson.fromJson(req.body(), ConcurrentLessonDto.class);

        // Fetch and set Activity
        Activity activity = getActivityByName(lessonDto.getActivity());
        if (activity == null) {
            res.status(404);
            return "Activity not found";
        }

        // Fetch and set Professor
        Professor professor = getProfessorByUsername(lessonDto.getProfessor());
        if (professor == null) {
            res.status(404);
            return "Professor not found";
        }

        // Create multiple lesson instances
        Set<Lesson> lessonsToAdd = new HashSet<>();
        LocalDate startDate = lessonDto.getStartDate();
        LocalDate endDate = lessonDto.getEndDate();

        while (!startDate.isAfter(endDate)) {
            Lesson lesson = new Lesson(
                    lessonDto.getName(),
                    lessonDto.getTime(),
                    lessonDto.getStartDate()
            );

            // Set the fetched Activity and Professor to each Lesson
            lesson.setActivity(activity);
            lesson.setProfessor(professor);

            lessonsToAdd.add(lesson);
            startDate = startDate.plusWeeks(1); // Increment by one week
        }

        // Persist all the lessons
        lessonsToAdd.forEach(lesson -> lessons.persist(lesson));

        res.type("application/json");
        return gson.toJson(lessonsToAdd.stream().map(Lesson::asJson).collect(Collectors.toList()));
    }

    //method that fetch from queries
    private Activity getActivityByName(String name) {
        return activities.findActivityByName(name);
    }
    private Professor getProfessorByUsername(String username) {
        return professors.findProfessorByUsername(username);
    }

}
