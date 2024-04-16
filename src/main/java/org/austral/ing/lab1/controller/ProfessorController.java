package org.austral.ing.lab1.controller;

import com.google.gson.Gson;
import org.austral.ing.lab1.model.Lesson;
import org.austral.ing.lab1.model.Professor;
import org.austral.ing.lab1.model.User;
import org.austral.ing.lab1.queries.Administrators;
import org.austral.ing.lab1.queries.Professors;
import org.austral.ing.lab1.queries.Students;
import org.austral.ing.lab1.queries.Users;
import spark.Request;
import spark.Response;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

public class ProfessorController {
    private final Users users;
    private final Professors professors;

    private final Gson gson = new Gson();

    public ProfessorController(EntityManager entityManager) {
        this.users = new Users(entityManager);
        this.professors = new Professors(entityManager);
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
        List<String> lessonsNames = new ArrayList<>();
        for(Lesson lesson: lessons){
            lessonsNames.add(lesson.getName());
        }
        res.type("application/json");
        return gson.toJson(lessonsNames);
    }

}
