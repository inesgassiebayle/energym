package org.austral.ing.lab1.controller;

import com.google.gson.Gson;
import org.austral.ing.lab1.dto.LessonDeletionDto;
import org.austral.ing.lab1.dto.ReviewCreationDto;
import org.austral.ing.lab1.model.*;
import org.austral.ing.lab1.queries.*;
import spark.Request;
import spark.Response;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalTime;

public class ReviewController {
    private final Users users;
    private final Professors professors;
    private final Lessons lessons;
    private final Students students;
    private final Reviews reviews;
    private final Gson gson = new Gson();
    public ReviewController() {
        this.users = new Users();
        this.lessons = new Lessons();
        this.professors = new Professors();
        this.students = new Students();
        this.reviews = new Reviews();
    }

    public String createReview(Request req, Response res){
        ReviewCreationDto reviewCreationDto = gson.fromJson(req.body(), ReviewCreationDto.class);
        String username = reviewCreationDto.getUsername();
        if(username == null){
            res.status(400);
            return "Username is not valid";
        }
        Student student = students.findStudentByUsername(username);
        if(student == null){
            res.status(400);
            return "Student does not exist";
        }
        User user = student.getUser();
        if(!user.state()){
            res.status(400);
            return "Student does not exist";
        }
        String professor = reviewCreationDto.getProfessor();
        LocalDate date = reviewCreationDto.getDate();
        LocalTime time = reviewCreationDto.getTime();
        if(professor == null){
            res.status(400);
            return "Professor is not valid";
        }
        if(date == null){
            res.status(400);
            return "Lesson date is not valid";
        }
        if(time == null){
            res.status(400);
            return "Lesson time is not valid";
        }

        Professor professor2 = professors.findProfessorByUsername(professor);
        if(professor2 == null){
            res.status(400);
            return "Invalid input";
        }
        Lesson lesson = lessons.findLessonsByProfessorDateAndTime(professor, time, date).get(0);
        if(lesson == null){
            res.status(400);
            return "Lesson does not exist";
        }
        if(!lesson.getState()){
            res.status(400);
            return "Lesson does not exist";
        }
        String comment = reviewCreationDto.getComment();
        if(comment == null){
            res.status(400);
            return "Comment is not valid";
        }
        String rating1 = reviewCreationDto.getRating();
        if(rating1 == null){
            res.status(400);
            return "Review is not valid";
        }
        Integer rating = Integer.parseInt(rating1);
        if(rating < 0 || rating > 5){
            res.status(400);
            return "Review is not valid";
        }
        Review review = new Review(comment, rating, student, lesson);
        reviews.persist(review);
        lesson.addReview(review);
        lessons.persist(lesson);
        res.type("application/json");
        return review.asJson();
    }

}
