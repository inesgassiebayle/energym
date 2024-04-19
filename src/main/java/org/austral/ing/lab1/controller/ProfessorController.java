package org.austral.ing.lab1.controller;

import com.google.gson.Gson;
import org.austral.ing.lab1.dto.FullnameDto;
import org.austral.ing.lab1.dto.LessonDeletionDto;
import org.austral.ing.lab1.dto.ReviewDto;
import org.austral.ing.lab1.dto.SignUpDto;
import org.austral.ing.lab1.model.*;
import org.austral.ing.lab1.queries.*;
import spark.Request;
import spark.Response;

import javax.persistence.EntityManager;
import java.time.LocalDate;
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
        String username = req.params(":username");

        if(username == null || username.isBlank()){
            return "Invalid input";
        }

        Professor professor = professors.findProfessorByUsername(username);

        List<Lesson> lessons = professors.getLessons(professor);
        List<LessonDeletionDto> lessonsInfo = new ArrayList<>();
        for(Lesson lesson: lessons){
            lessonsInfo.add(new LessonDeletionDto(lesson.getName(), lesson.getStartDate().toString()));
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

        FullnameDto dto = new FullnameDto(user.getFirstName(), user.getLastName());

        res.type("application/json");
        return gson.toJson(dto);
    }

    public String getLessonReviews(Request req, Response res){
         LessonDeletionDto dto = gson.fromJson(req.body(), LessonDeletionDto.class);
         String name = dto.getName();
         LocalDate date = dto.getDate();

         if(name == null){
             return "Invalid lesson name";
         }

         Lesson lesson = lessons.findLessonByNameAndDate(name, date);

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
