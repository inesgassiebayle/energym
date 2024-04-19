package org.austral.ing.lab1.controller;

import com.google.gson.Gson;
import org.austral.ing.lab1.dto.LessonDeletionDto;
import org.austral.ing.lab1.dto.ReviewCreationDto;
import org.austral.ing.lab1.model.Lesson;
import org.austral.ing.lab1.model.Review;
import org.austral.ing.lab1.model.Student;
import org.austral.ing.lab1.model.User;
import org.austral.ing.lab1.queries.*;
import spark.Request;
import spark.Response;

import javax.persistence.EntityManager;
import java.time.LocalDate;

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
            return "Username is not valid";
        }

        Student student = students.findStudentByUsername(username);

        if(student == null){
            return "Student does not exist";
        }

        String lessonName = reviewCreationDto.getLessonName();
        LocalDate date = reviewCreationDto.getDate();
        if(lessonName == null){
            return "Lesson name is not valid";
        }

        Lesson lesson = lessons.findLessonByNameAndDate(lessonName, date);

        if(lesson == null){
            return "Lesson does not exist";
        }

        String comment = reviewCreationDto.getComment();
        if(comment == null){
            return "Comment is not valid";
        }
        String rating1 = reviewCreationDto.getRating();
        if(rating1 == null){
            return "Review is not valid";
        }
        Integer rating = Integer.parseInt(rating1);
        if(rating < 0 || rating > 5){
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
