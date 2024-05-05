package org.austral.ing.lab1.controller;

import com.google.gson.Gson;
import org.austral.ing.lab1.dto.BookingDto;
import org.austral.ing.lab1.dto.ProfessorDateTimeDto;
import org.austral.ing.lab1.model.*;
import org.austral.ing.lab1.queries.*;
import spark.Request;
import spark.Response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class StudentController {
    private final Users users;
    private final Students students;
    private final Lessons lessons;
    private final Professors professors;
    private final LessonBookings lessonBookings;
    private final Reviews reviews;
    private final Gson gson = new Gson();

    public StudentController() {
        this.users = new Users();
        this.students = new Students();
        this.lessons = new Lessons();
        this.professors = new Professors();
        this.lessonBookings = new LessonBookings();
        this.reviews = new Reviews();
    }

    public String bookClass(Request req, Response res) {
        BookingDto dto = gson.fromJson(req.body(), BookingDto.class);
        String professor = dto.getProfessor();
        LocalDate date = dto.getDate();
        LocalTime time = dto.getTime();
        String student = dto.getStudent();

        if (student == null) {
            res.status(400);
            return "Invalid input";
        }
        Student student1 = students.findStudentByUsername(student);
        if (student1 == null) {
            res.status(400);
            return "Student not found";
        }
        if (!student1.getUser().state()) {
            res.status(400);
            return "Student not active";
        }
        if(professor == null){
            res.status(400);
            return "Invalid lesson name";
        }
        if(date==null || time == null){
            res.status(400);
            return "Invalid date or time";
        }
        if (!correctDate(date, time)) {
            res.status(400);
            return "Invalid date or time";
        }
        Professor professor1 = professors.findProfessorByUsername(professor);
        if(professor1 == null){
            res.status(400);
            return "Invalid input";
        }
        if (!professor1.getUser().state()) {
            res.status(400);
            return "Professor not active";
        }

        Lesson lesson = lessons.findLessonsByProfessorDateAndTime(professor, time, date).get(0);

        if(lesson == null){
            res.status(400);
            return "Lesson was not found";
        }

        if(!lesson.getState()) {
            res.status(400);
            return "Lesson was not found";
        }

        BookedLesson booking = lessonBookings.findBookingByDateAndTimeAndStudent(date, time, student1);
        if (booking == null) {
            booking = new BookedLesson(student1, lesson);
            lessonBookings.persist(booking);
            return booking.asJson();
        }

        if (!booking.state()) {
            booking.activate();
            lessonBookings.persist(booking);
            return booking.asJson();
        }

        if (booking.getLesson().getProfessor().equals(professor1)) {
            res.status(400);
            return "Booking already exists";
        }
        res.status(400);
        return "Student already has a class at that time";
    }

    public boolean correctDate(LocalDate date, LocalTime time) {
        LocalDate nowDate = LocalDate.now();
        if (date.isBefore(nowDate)) {
            return false;
        }
        else if (date.isAfter(nowDate)) {
            return true;
        }
        else {
            LocalTime nowTime = LocalTime.now();

            if ((nowTime.equals(time) || nowTime.isAfter(time)) && nowTime.isBefore(time.plusHours(1))) {
                return false;
            }

            else if (nowTime.isBefore(time)) {
                return true;
            }
            return false;
        }
    }

    public String classifyLessons(Request req, Response res) {
        String username = req.queryParams("username");
        String date = req.queryParams("date");
        String time = req.queryParams("time");
        String professor = req.queryParams("professor");
        if (date == null || date.isBlank() || time == null || time.isBlank()|| professor == null || professor.isBlank() || username == null || username.isBlank()) {
            res.status(400);
            return "Invalid input";
        }
        Student student = students.findStudentByUsername(username);
        if (student == null) {
            res.status(400);
            return "Student not found";
        }
        if (!student.getUser().state()) {
            res.status(400);
            return "Student not active";
        }
        ProfessorDateTimeDto dto = new ProfessorDateTimeDto(professor, date, time);
        Professor professor1 = professors.findProfessorByUsername(professor);
        if (professor1 == null) {
            res.status(400);
            return "Professor not found";
        }
        if (!professor1.getUser().state()) {
            res.status(400);
            return "Professor not active";
        }
        Lesson lesson = lessons.findLessonsByProfessorDateAndTime(professor, dto.getTime(), dto.getDate()).get(0);
        if (lesson == null) {
            res.status(400);
            return "Lesson not found";
        }
        if (!lesson.getState()) {
            res.status(400);
            return "Lesson not active";
        }
        if (correctDate(lesson.getStartDate(), lesson.getTime())) {
            List<BookedLesson> bookings =new ArrayList<>();
            for (BookedLesson booking: lesson.getBookings()) {
                if(booking.state()) {
                    if (booking.getStudent().equals(student)) {
                        return "Future class booked";
                    }
                    bookings.add(booking);
                }
            }
            if (lesson.getRoom().getCapacity() <= bookings.size()) {
                return "Future class full";
            }
            return "Future class available";
        }
        else {
            List<BookedLesson> bookings =new ArrayList<>();
            for (BookedLesson booking: lesson.getBookings()) {
                if(booking.state()) {
                    if (booking.getStudent().equals(student)) {
                        Review review = reviews.findReviewByLessonAndStudent(lesson, student);
                        if(review != null) {
                            return "Past class booked and reviewed";
                        }
                        return "Past class booked";
                    }
                    bookings.add(booking);
                }
            }
            return "Past class not booked";
        }
    }

    public String deleteBooking(Request req, Response res) {
        BookingDto dto = new BookingDto(req.queryParams("professor"), req.queryParams("student"), req.queryParams("date"), req.queryParams("time"));
        String professor = dto.getProfessor();
        LocalDate date = dto.getDate();
        LocalTime time = dto.getTime();
        String student = dto.getStudent();

        if (student == null) {
            res.status(400);
            return "Invalid input";
        }
        Student student1 = students.findStudentByUsername(student);
        if (student1 == null) {
            res.status(400);
            return "Student not found";
        }
        if (!student1.getUser().state()) {
            res.status(400);
            return "Student not active";
        }
        if(professor == null){
            res.status(400);
            return "Invalid lesson name";
        }
        if(date==null || time == null){
            res.status(400);
            return "Invalid date or time";
        }
        if (!correctDate(date, time)) {
            res.status(400);
            return "Invalid date or time";
        }
        Professor professor1 = professors.findProfessorByUsername(professor);
        if(professor1 == null){
            res.status(400);
            return "Invalid input";
        }
        if (!professor1.getUser().state()) {
            res.status(400);
            return "Professor not active";
        }

        Lesson lesson = lessons.findLessonsByProfessorDateAndTime(professor, time, date).get(0);

        if(lesson == null){
            res.status(400);
            return "Lesson was not found";
        }

        if(!lesson.getState()) {
            res.status(400);
            return "Lesson was not found";
        }
        BookedLesson booking = lessonBookings.findBookingByLessonAndStudent(lesson, student1);
        if (booking == null) {
            res.status(400);
            return "Booking not found";
        }
        if (!booking.state()) {
            res.status(400);
            return "Booking not active";
        }
        booking.deactivate();
        lessonBookings.persist(booking);
        return booking.asJson();
    }
}
