package org.austral.ing.lab1.controller;

import com.google.gson.Gson;
import org.austral.ing.lab1.dto.BookingDto;
import org.austral.ing.lab1.dto.ProfessorDateTimeDto;
import org.austral.ing.lab1.model.BookedLesson;
import org.austral.ing.lab1.model.Lesson;
import org.austral.ing.lab1.model.Professor;
import org.austral.ing.lab1.model.Student;
import org.austral.ing.lab1.queries.*;
import spark.Request;
import spark.Response;

import java.time.LocalDate;
import java.time.LocalTime;

public class StudentController {
    private final Users users;
    private final Students students;
    private final Lessons lessons;
    private final Professors professors;
    private final LessonBookings lessonBookings;
    private final Gson gson = new Gson();

    public StudentController() {
        this.users = new Users();
        this.students = new Students();
        this.lessons = new Lessons();
        this.professors = new Professors();
        this.lessonBookings = new LessonBookings();
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

        BookedLesson booking = new BookedLesson(student1, lesson);
        lessonBookings.persist(booking);
        return booking.asJson();
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
}
