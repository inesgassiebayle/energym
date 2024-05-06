package org.austral.ing.lab1.controller;

import com.google.gson.Gson;
import org.austral.ing.lab1.Result;
import org.austral.ing.lab1.dto.BookingDto;
import org.austral.ing.lab1.dto.ConcurrentBookingDto;
import org.austral.ing.lab1.dto.ProfessorDateTimeDto;
import org.austral.ing.lab1.model.*;
import org.austral.ing.lab1.queries.*;
import spark.Request;
import spark.Response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
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
        LocalDate startDate = dto.getStartDate();
        LocalDate endDate = dto.getEndDate();
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
        if(endDate==null || startDate == null|| time == null){
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
        if (endDate.equals(startDate)) {
            BookedLesson booking = bookSingleClass(professor1, startDate, time, student1);
            if (booking == null) {
                res.status(400);
                return "Invalid input";
            }
            lessonBookings.persist(booking);
            return booking.asJson();
        }
        else {
            if(endDate.isBefore(startDate)) {
                res.status(400);
                return "Invalid input";
            }
            List<BookedLesson> bookings = new ArrayList<>();
            for (LocalDate date = startDate; date.isBefore(endDate.plusDays(1)); date = date.plusDays(7)) {
                BookedLesson booking = bookSingleClass(professor1, date, time, student1);
                if (booking == null) {
                    res.status(400);
                    return "Invalid input";
                }
                bookings.add(booking);
            }
            for (BookedLesson booking: bookings) {
                lessonBookings.persist(booking);
            }
            return "Succesfull bookings";
        }
    }


    public BookedLesson bookSingleClass(Professor professor, LocalDate date, LocalTime time, Student student) {
        if (!correctDate(date, time)) {
            return null;
        }
        Lesson lesson = lessons.findLessonsByProfessorDateAndTime(professor.getUser().getUsername(), time, date).get(0);
        if(lesson == null){
            return null;
        }
        if(!lesson.getState()) {
            return null;
        }
        BookedLesson booking = lessonBookings.findBookingByDateAndTimeAndStudent(date, time, student);
        if (booking == null) {
            return new BookedLesson(student, lesson);
        }

        if (!booking.state()) {
            booking.activate();
            return booking;
        }

        if (booking.getLesson().getProfessor().equals(professor)) {
            return booking;
        }
        return null;
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
        BookingDto dto = new BookingDto(req.queryParams("professor"), req.queryParams("student"), req.queryParams("startDate"), req.queryParams("endDate"), req.queryParams("time"));
        String professor = dto.getProfessor();
        LocalDate startDate = dto.getStartDate();
        LocalDate endDate = dto.getEndDate();
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
        if(startDate==null || time == null){
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

        if(startDate.equals(endDate)) {
            Result booking = deleteSingleBooking(student1, professor1, startDate, time);
            if (booking.getBooking().isEmpty()) {
                res.status(400);
                return booking.getMessage().get();
            }
            lessonBookings.persist(booking.getBooking().get());
            return booking.getBooking().get().asJson();
        }
        else {
            if(endDate.isBefore(startDate)) {
                res.status(400);
                return "Invalid input";
            }
            List<BookedLesson> bookings = new ArrayList<>();
            for (LocalDate date = startDate; date.isBefore(endDate.plusDays(1)); date = date.plusDays(7)) {
                Result booking = deleteSingleBooking(student1, professor1, date, time);
                if (booking.getBooking().isEmpty()) {
                    res.status(400);
                    return booking.getMessage().get();
                }
                bookings.add(booking.getBooking().get());
            }
            for (BookedLesson booking: bookings) {
                lessonBookings.persist(booking);
            }
            return "Succesfull bookings";
        }

    }

    public Result deleteSingleBooking(Student student, Professor professor, LocalDate date, LocalTime time){
        if (!correctDate(date, time)) {
           return new Result("Invalid date or time");
        }

        Lesson lesson = lessons.findLessonsByProfessorDateAndTime(professor.getUser().getUsername(), time, date).get(0);

        if(lesson == null){
            return new Result("Lesson does not exist");
        }

        if(!lesson.getState()) {
            return new Result("Lesson does not exist");
        }
        BookedLesson booking = lessonBookings.findBookingByLessonAndStudent(lesson, student);
        if (booking == null) {
            return new Result("Booking does not exist");
        }
        if (!booking.state()) {
            return new Result("Booking does not exist");
        }
        booking.deactivate();
        return new Result(booking);

    }


    public String checkConcurrency(Request req, Response res) {
        String professor = req.queryParams("professor");
        String stringDate = req.queryParams("date");
        String stringTime = req.queryParams("time");
        ProfessorDateTimeDto dto = new ProfessorDateTimeDto(professor, stringDate, stringTime);
        if (professor == null || stringDate == null || stringTime == null) {
            res.status(400);
            return "Invalid input";
        }
        if (professor.isBlank() || stringDate.isBlank() || stringTime.isBlank()) {
            res.status(400);
            return "Invalid input";
        }
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

        List<Lesson> lessons2 = lessons.findConcurrentLessons(dto.getTime(), professor, lesson.getName(), lesson.getRoom().getName(), lesson.getActivity().getName(), dto.getDate().getDayOfWeek());
        if (lessons2.size() == 1) {
            return "Not concurrent lessons";
        }
        else {
            LocalDate minDate = getMinimumDate(lessons2);
            LocalDate maxDate = getMaximumDate(lessons2);
            ConcurrentBookingDto concurrentBookingDto = new ConcurrentBookingDto(lesson.getStartDate().getDayOfWeek().toString(), minDate, maxDate);
            return gson.toJson(concurrentBookingDto);
        }
    }

    private LocalDate getMaximumDate(List<Lesson> lessons) {
        LocalDate max = lessons.get(0).getStartDate();
        for (Lesson lesson: lessons) {
            if (lesson.getState() && lesson.getStartDate().isAfter(max)) {
                max = lesson.getStartDate();
            }
        }
        return max;
    }

    private LocalDate getMinimumDate(List<Lesson> lessons) {
        LocalDate min = lessons.get(0).getStartDate();
        for (Lesson lesson: lessons) {
            if (lesson.getState() && lesson.getStartDate().isBefore(min)) {
                min = lesson.getStartDate();
            }
        }
        return min;
    }
}