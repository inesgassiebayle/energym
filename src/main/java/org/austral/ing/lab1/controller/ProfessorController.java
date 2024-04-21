package org.austral.ing.lab1.controller;

import com.google.gson.Gson;
import org.austral.ing.lab1.dto.*;
import org.austral.ing.lab1.model.*;
import org.austral.ing.lab1.queries.*;
import spark.Request;
import spark.Response;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ProfessorController {
    private final Users users;
    private final Professors professors;
    private final Lessons lessons;
    private final Gson gson = new Gson();

    public ProfessorController() {
        this.users = new Users();
        this.professors = new Professors();
        this.lessons = new Lessons();
    }


    public String getProfessors(Request req, Response res){
        List<Professor> professors1 = professors.findAllProfessors();
        List<String> names = new ArrayList<>();
        for(Professor professor: professors1){
            User user = professor.getUser();
            if(user.state()){
                names.add(user.getUsername());
            }
        }
        res.type("application/json");
        return gson.toJson(names);
    }

    public String getLessons(Request req, Response res){
        ProfessorDateLessonsDto dto = gson.fromJson(req.body(), ProfessorDateLessonsDto.class);
        LocalDate date = dto.getDate();
        String username = dto.getName();

        if(username == null || username.isBlank()){
            return "Invalid username";
        }

        if(date == null){
            return "Invalid date";
        }

        Professor professor = professors.findProfessorByUsername(username);

        if(professor == null){
            return "Professor does not exist";
        }

        if(!professor.getUser().state()){
            return "Professor does not exist";
        }

        List<Lesson> lessons = professors.getLessons(professor);
        List<LessonNameTimeDateDto> lessonsInfo = new ArrayList<>();
        for(Lesson lesson: lessons){
            if(lesson.getStartDate().equals(date) && lesson.getState()){
                lessonsInfo.add(new LessonNameTimeDateDto(lesson.getName(), lesson.getStartDate().toString(), lesson.getTime().toString()));
            }
        }
        res.type("application/json");
        return gson.toJson(lessonsInfo);
    }

    public String getFullname(Request req, Response res){
        String username = req.params(":username");

        if(username == null || username.isBlank()){
            return "Invalid input";
        }

        Professor professor = professors.findProfessorByUsername(username);

        if(professor == null){
            return "Professor not found";
        }

        User user = professor.getUser();

        if(!user.state()){
            return "Professor was deleted";
        }

        FullnameDto dto = new FullnameDto(user.getFirstName(), user.getLastName());

        res.type("application/json");
        return gson.toJson(dto);
    }

    public String getLessonReviews(Request req, Response res){
         LessonNameTimeDateDto dto = gson.fromJson(req.body(), LessonNameTimeDateDto.class);
         String name = dto.getName();
         LocalDate date = dto.getDate();
         LocalTime time = dto.getTime();

         if(name == null){
             res.status(400); // Bad Request
             return "Invalid lesson name";
         }

         if(date==null || time == null){
             res.status(400); // Bad Request
             return "Invalid date or time";
         }

         Lesson lesson = lessons.findLessonByNameDateAndTime(name, date, time);

         if(lesson == null){
             return "Lesson does not exist";
         }

         Set<Review> reviews = lesson.getReviews();

         List<ReviewDto> reviewDtos = new ArrayList<>();

         for(Review review: reviews){
             Student student = review.getStudent();

             if(student==null){
                 return "Student does not exist";
             }

             User user = student.getUser();

             if(user == null){
                 return "User does not exist";
             }

             reviewDtos.add(new ReviewDto(user.getUsername(), review.getComment(), review.getRating().toString()));
         }

        res.type("application/json");
        return gson.toJson(reviewDtos);
    }

}
