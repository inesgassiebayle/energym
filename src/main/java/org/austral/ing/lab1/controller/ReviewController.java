package org.austral.ing.lab1.controller;

import com.google.gson.Gson;
import org.austral.ing.lab1.dto.LessonDeletionDto;
import org.austral.ing.lab1.dto.ProfessorDateTimeDto;
import org.austral.ing.lab1.dto.ReviewCreationDto;
import org.austral.ing.lab1.model.*;
import org.austral.ing.lab1.queries.*;
import spark.Request;
import spark.Response;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

public class ReviewController {
    private final Users users;
    private final Professors professors;
    private final Lessons lessons;
    private final Students students;
    private final Reviews reviews;
    private final LessonBookings lessonBookings;
    private final Gson gson = new Gson();
    public ReviewController() {
        this.users = new Users();
        this.lessons = new Lessons();
        this.professors = new Professors();
        this.students = new Students();
        this.reviews = new Reviews();
        this.lessonBookings = new LessonBookings();
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
        if (!correctDate(date, time)) {
            res.status(400);
            return "Lesson has not happened";
        }
        BookedLesson booking = lessonBookings.findBookingByLessonAndStudent(lesson, student);
        if (booking == null) {
            res.status(400);
            return "Cannot rate a class a student did not assist";
        }
        if (!booking.state() || !booking.hasAssisted()) {
            res.status(400);
            return "Cannot rate a class a student did not assist";
        }
        Review review = reviews.findReviewByLessonAndStudent(lesson, student);
        if (review != null) {
            res.status(400);
            return "Review already exists";
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
        Review review2 = new Review(comment, rating, student, lesson);
        reviews.persist(review2);
        lesson.addReview(review2);
        lessons.persist(lesson);
        res.type("application/json");
        return review2.asJson();
    }

    public String getReview(Request req, Response res){
        String username = req.queryParams("username");
        if(username == null || username.isEmpty()) {
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
        String professor = req.queryParams("professor");
        String date = req.queryParams("date");
        String time = req.queryParams("time");
        if (professor == null || professor.isEmpty()) {
            res.status(400);
            return "Professor is not valid";
        }
        if (date == null || date.isEmpty()) {
            res.status(400);
            return "Lesson date is not valid";
        }
        if (time == null || time.isEmpty()) {
            res.status(400);
            return "Lesson time is not valid";
        }
        Professor professor2 = professors.findProfessorByUsername(professor);
        if (professor2 == null) {
            res.status(400);
            return "Invalid input";
        }
        if (!professor2.getUser().state()) {
            res.status(400);
            return "Invalid input";
        }
        ProfessorDateTimeDto dto = new ProfessorDateTimeDto(professor, date, time);
        Lesson lesson = lessons.findLessonsByProfessorDateAndTime(professor, dto.getTime(), dto.getDate()).get(0);
        if (lesson == null) {
            res.status(400);
            return "Lesson does not exist";
        }
        if (!lesson.getState()) {
            res.status(400);
            return "Lesson does not exist";
        }
        if (!correctDate(dto.getDate(), dto.getTime())) {
            res.status(400);
            return "Lesson has not happened";
        }
        Review review = reviews.findReviewByLessonAndStudent(lesson, student);
        res.type("application/json");
        return review.asJson();
    }

    public boolean correctDate(LocalDate date, LocalTime time){
        if (date.isAfter(LocalDate.now())) {
            return false;
        }
        else if (date.isBefore(LocalDate.now())) {
            return true;
        }
        else{
            if (time.isAfter(LocalTime.now())) {
                return false;
            }
            else {
                return true;
            }
        }
    }

}
