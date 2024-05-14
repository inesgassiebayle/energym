package org.austral.ing.lab1.controller;

import com.google.gson.Gson;
import org.austral.ing.lab1.dto.*;
import org.austral.ing.lab1.model.*;
import org.austral.ing.lab1.queries.*;
import spark.Request;
import spark.Response;

import javax.persistence.EntityManager;
import java.time.*;
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
        ProfessorDateLessonsDto dto = new ProfessorDateLessonsDto(req.queryParams("username"), req.queryParams("date"));

        LocalDate date = dto.getDate();
        String username = dto.getName();

        if(username == null || username.isBlank()){
            res.status(400);
            return "Invalid username";
        }

        if(date == null){
            res.status(400);
            return "Invalid date";
        }

        Professor professor = professors.findProfessorByUsername(username);

        if(professor == null){
            res.status(404);
            return "Professor does not exist";
        }

        if(!professor.getUser().state()){
            res.status(404);
            return "Professor does not exist";
        }

        List<Lesson> lessons = professors.getLessons(professor);
        List<LessonDto> lessonsInfo = new ArrayList<>();
        for(Lesson lesson: lessons){
            if(lesson.getStartDate().equals(date) && lesson.getState()){
                lessonsInfo.add(new LessonDto(lesson.getId().toString(), lesson.getName(), lesson.getStartDate().toString(), lesson.getTime().toString(), lesson.getRoom().getName(), lesson.getActivity().getName(), lesson.getProfessor().getUser().getUsername()));
            }
        }
        res.type("application/json");
        return gson.toJson(lessonsInfo);
    }

    public String getFullname(Request req, Response res){
        String username = req.params(":username");

        if(username == null || username.isBlank()){
            res.status(400);
            return "Invalid input";
        }

        Professor professor = professors.findProfessorByUsername(username);

        if(professor == null){
            res.status(404);
            return "Professor not found";
        }

        User user = professor.getUser();

        if(!user.state()){
            res.status(404);
            return "Professor was deleted";
        }

        FullnameDto dto = new FullnameDto(user.getFirstName(), user.getLastName());

        res.type("application/json");
        return gson.toJson(dto);
    }

    public String getLessonsByDateAndProfessor(Request req, Response res){
        ProfessorDateDto professorDateDto = gson.fromJson(req.body(), ProfessorDateDto.class);
        LocalDate date = professorDateDto.getDate();
        String username = professorDateDto.getName();

        if(username == null || username.isBlank()){
            res.status(400);
            return "Invalid username";
        }

        if(date == null){
            res.status(400);
            return "Invalid date";
        }

        Professor professor = professors.findProfessorByUsername(username);

        if(professor == null){
            res.status(404);
            return "Professor does not exist";
        }

//        if(!professor.getUser().state()){
//            res.status(404);
//            return "Professor does not exist";
//        }

        List<Lesson> lessons = professors.findLessonByDateAndProfessor(date, professor);
        List<LessonNameTimeDateDto> lessonsInfo = new ArrayList<>();
        for(Lesson lesson: lessons){
            if(lesson.getState()){
                lessonsInfo.add(new LessonNameTimeDateDto(lesson.getName(), lesson.getStartDate().toString(), lesson.getTime().toString()));
            }
        }
        res.type("application/json");
        return gson.toJson(lessonsInfo);
    }

    public String getLessons2(Request req, Response res) {
        String username = req.queryParams("username");
        Professors professors = new Professors();
        Professor professor = professors.findProfessorByUsername(username);
        List<Lesson> lessons = professors.getLessons(professor);
        List<LessonNameTimeDateDto> lessonsInfo = new ArrayList<>();
        for(Lesson lesson: lessons){
            lessonsInfo.add(new LessonNameTimeDateDto(lesson.getName(), lesson.getStartDate().toString(), lesson.getTime().toString()));
        }
        res.type("application/json");
        return gson.toJson(lessonsInfo);

    }


}
