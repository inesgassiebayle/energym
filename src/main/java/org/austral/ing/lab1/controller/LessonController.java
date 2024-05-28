package org.austral.ing.lab1.controller;
import com.google.gson.Gson;
import org.austral.ing.lab1.ServiceResult;
import org.austral.ing.lab1.dto.*;
import org.austral.ing.lab1.model.*;
import org.austral.ing.lab1.queries.*;
import org.austral.ing.lab1.service.LessonService;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class LessonController{
    private final Lessons lessons;
    private final Activities activities;
    private final Professors professors;
    private final Rooms rooms;
    private final Students students4;
    private final LessonBookings lessonBookings;
    private final Gson gson = new Gson();
    private final ReminderService reminderService;
    private final EmailSender emailSender;
    private final LessonService lessonService;

    public LessonController(ReminderService reminderService, EmailSender emailSender) {
        this.lessons = new Lessons();
        this.activities = new Activities();
        this.professors = new Professors();
        this.rooms = new Rooms();
        this.students4 = new Students();
        this.lessonBookings = new LessonBookings();
        this.reminderService = reminderService;
        this.emailSender = emailSender;
        this.lessonService = new LessonService(emailSender, reminderService);
    }

    public String addSingleLesson(Request req, Response res) {
        LessonCreationDto lessonDto = gson.fromJson(req.body(), LessonCreationDto.class);

        String name = lessonDto.getName();
        LocalDate date = lessonDto.getStartDate();
        LocalTime time = lessonDto.getTime();
        Professor professor = professors.findProfessorByUsername(lessonDto.getProfessor());
        Activity activity = activities.findActivityByName(lessonDto.getActivity());
        Room room = rooms.findRoomByName(lessonDto.getRoomName());
        ServiceResult serviceResult = lessonService.addLesson(name, date, time, room, activity, professor);
        if (serviceResult.isValid()) {
            res.type("application/json");
            return serviceResult.getMessage();
        }
        else {
            res.status(400);
            return serviceResult.getMessage();
        }
    }

    public String addConcurrentLessons(Request req, Response res) {
        // Parse the JSON body to the ConcurrentLessonDto
        ConcurrentLessonCreationDto lessonDto = gson.fromJson(req.body(), ConcurrentLessonCreationDto.class);
        LocalDate startDate = lessonDto.getStartDate();
        LocalDate endDate = lessonDto.getEndDate();
        LocalTime time = lessonDto.getTime();
        String name = lessonDto.getName();
        Room room = rooms.findRoomByName(lessonDto.getRoomName());
        Professor professor = professors.findProfessorByUsername(lessonDto.getProfessor());
        Activity activity = activities.findActivityByName(lessonDto.getActivity());
        ServiceResult serviceResult = lessonService.addConcurrentLessons(startDate, endDate, time, name, professor, room, activity);
        if (serviceResult.isValid()) {
            res.type("application/json");
            return serviceResult.getMessage();
        }
        else {
            res.status(400);
            return serviceResult.getMessage();
        }

    }


    private Activity getActivityByName(String name) {
        return activities.findActivityByName(name);
    }

    private Professor getProfessorByUsername(String username) {
        return professors.findProfessorByUsername(username);
    }


    public String deleteLesson(Request req, Response res) {
        ProfessorDateTimeDto deletionDto = gson.fromJson(req.body(), ProfessorDateTimeDto.class);
        String username = deletionDto.getName();
        LocalDate lessonDate = deletionDto.getDate();
        LocalTime lessonTime = deletionDto.getTime();
        if(username == null || lessonDate == null || lessonTime == null){
            res.status(400);
            return "Invalid input";
        }
        Professor professor = professors.findProfessorByUsername(username);
        Lesson lesson = lessons.findLessonsByProfessorDateAndTime(username, lessonTime, lessonDate).get(0);
        ServiceResult result = lessonService.deleteLesson(lesson);
        if (result.isValid()) {
            res.type("application/json");
            return result.getMessage();
        }
        else {
            res.status(400);
            return result.getMessage();
        }
    }


    public String getLessonsByDate(Request req, Response res) {
        String dateString = req.queryParams("startDate");
        if(dateString == null){
            res.status(400);
            return "Invalid date";
        }
        LocalDate lessonDate = LocalDate.parse(dateString);
        List<Lesson> lessons1 = lessons.findLessonsByDate(lessonDate);
        List<ProfessorNameDateTimeDto> lessonInfo = new ArrayList<>();
        for (Lesson lesson: lessons1) {
            if (lesson.getState()) {
                lessonInfo.add(new ProfessorNameDateTimeDto(lesson.getName(), lesson.getStartDate().toString(), lesson.getTime().toString(), lesson.getProfessor().getUser().getUsername()));
            }
        }
        res.type("application/json");
        return gson.toJson(lessonInfo);
    }

    public String lessonModify(Request req, Response res) {
        LessonModifyDto modifyDto =  gson.fromJson(req.body(), LessonModifyDto.class);
        String oldProfessor = modifyDto.getOldProfessor();
        LocalDate oldDate = modifyDto.getOldDate();
        LocalTime oldTime = modifyDto.getOldTime();
        Lesson oldLesson = lessons.findLessonsByProfessorDateAndTime(oldProfessor, oldTime, oldDate).get(0);
        String newName = modifyDto.getName();
        LocalTime newTime = modifyDto.getTime();
        LocalDate newDate = modifyDto.getStartDate();
        Activity newActivity = getActivityByName(modifyDto.getActivity());
        Professor newProfessor = getProfessorByUsername(modifyDto.getProfessor());
        Room newRoom = rooms.findRoomByName(modifyDto.getRoomName());

        ServiceResult result = lessonService.modifyLesson(oldLesson, newActivity, newRoom, newProfessor, newName, newTime, newDate);

        if (result.isValid()) {
            res.type("application/json");
            return result.getMessage();
        }
        else {
            res.status(400);
            return result.getMessage();
        }
    }

    public String getLessonReviews(Request req, Response res){
        String id = req.queryParams("lessonId");
        if (id == null || id.isBlank()) {
            res.status(400);
            return "Invalid input";
        }
        Lesson lesson = lessons.findLessonById(Long.parseLong(id));
        if(lesson == null){
            res.status(400);
            return "Lesson does not exist";
        }
        if(!lesson.getState()){
            res.status(400);
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
            reviewDtos.add(new ReviewDto(user.getUsername(), review.getComment(), review.getRating().toString(), lesson.getName(), lesson.getStartDate().toString(), lesson.getTime().toString()));
        }
        res.type("application/json");
        return gson.toJson(reviewDtos);
    }

    public String getLessonReviewsByActivity(Request req, Response res){
        String activityName = req.queryParams("activity");
        if(activityName == null){
            res.status(400);
            return "Invalid activity name";
        }
        Activity activity = activities.findActivityByName(activityName);
        if(activity == null){
            res.status(400);
            return "Activity does not exist";
        }
        List<Lesson> lessons1 = lessons.findLessonByActivity(activityName);
        List<ReviewDto> reviewDtos = new ArrayList<>();
        for(Lesson lesson: lessons1){
            if(lesson.getState()){
                Set<Review> reviews = lesson.getReviews();
                for(Review review: reviews){
                    Student student = review.getStudent();
                    if(student==null){
                        return "Student does not exist";
                    }
                    User user = student.getUser();
                    if(user == null){
                        return "User does not exist";
                    }
                    reviewDtos.add(new ReviewDto(user.getUsername(), review.getComment(), review.getRating().toString(), lesson.getName(), lesson.getStartDate().toString(), lesson.getTime().toString()));
                }
            }
        }
        res.type("application/json");
        return gson.toJson(reviewDtos);
    }

    public String getLesson(Request req, Response res){
        String dateParam = req.queryParams("startDate");
        String usernameParam = req.queryParams("username");
        String timeParam = req.queryParams("time");

        if (usernameParam == null || timeParam == null) {
            return getLessonsByDate(req, res);
        }

        ProfessorDateTimeDto dto = new ProfessorDateTimeDto(usernameParam, dateParam, timeParam);

        LocalDate date = dto.getDate();
        String username = dto.getName();
        LocalTime time = dto.getTime();

        if(username == null || date == null || time == null){
            res.status(400);
            return "Invalid input";
        }

        Professor professor = professors.findProfessorByUsername(username);

        if(professor==null){
            res.status(400);
            return "Invalid input";
        }

        Lesson lesson = lessons.findLessonsByProfessorDateAndTime(username, time, date).get(0);

        if(lesson == null){
            res.status(400);
            return "Lesson was not found";
        }

        if(!lesson.getState()) {
            res.status(400);
            return "Lesson was not found";
        }

        return lesson.asJson();

    }



    public String getStudents(Request req, Response res) {
        ProfessorDateTimeDto dto = new ProfessorDateTimeDto(req.queryParams("username"), req.queryParams("startDate"), req.queryParams("time"));
        String username = dto.getName();
        LocalDate date = dto.getDate();
        LocalTime time = dto.getTime();
        if(username == null){
            res.status(400);
            return "Invalid lesson name";
        }
        if(date==null || time == null){
            res.status(400);
            return "Invalid date or time";
        }
        Professor professor = professors.findProfessorByUsername(username);
        if(professor == null){
            res.status(400);
            return "Invalid input";
        }
        Lesson lesson = lessons.findLessonsByProfessorDateAndTime(username, time, date).get(0);
        if(lesson == null){
            res.status(400);
            return "Lesson does not exist";
        }
        if(!lesson.getState()){
            res.status(400);
            return "Lesson does not exist";
        }
        Set<BookedLesson> bookings = lesson.getBookings();
        List<String> studentUsernames = new ArrayList<>();
        for(BookedLesson booking: bookings){
            if(booking.state()){
                User user = booking.getStudent().getUser();
                if(user.state()) {
                    studentUsernames.add(user.getUsername());
                }
            }
        }
        return gson.toJson(studentUsernames);
    }

    public String assistanceCheck(Request req, Response res) {
        AssistanceDto assistanceDto = gson.fromJson(req.body(), AssistanceDto.class);
        LocalDate date = assistanceDto.getDate();
        LocalTime time = assistanceDto.getTime();
        String professor = assistanceDto.getProfessor();
        String[] students = assistanceDto.getStudents();
        if (date == null || time == null || professor == null) {
            res.status(400);
            return "Invalid input";
        }
        if (!correctDateAndTime(date, time)) {
            res.status(400);
            return "Invalid date or time";
        }
        Professor professor1 = professors.findProfessorByUsername(professor);
        if (professor1 == null) {
            res.status(400);
            return "Invalid professor";
        }
        if (!professor1.getUser().state()) {
            res.status(400);
            return "Invalid professor";
        }
        Lesson lesson = lessons.findLessonsByProfessorDateAndTime(professor, time, date).get(0);
        if (lesson == null) {
            res.status(400);
            return "Lesson does not exist";
        }
        if (!lesson.getState()) {
            res.status(400);
            return "Lesson does not exist";
        }
        Set<BookedLesson> bookings = lesson.getBookings();
        List<Student> students1 = new ArrayList<>();
        for (String student: students){
            Student student1 = students4.findStudentByUsername(student);
            if (student1 == null) {
                res.status(400);
                return "Invalid student";
            }
            if (!student1.getUser().state()) {
                res.status(400);
                return "Invalid student";
            }
            students1.add(student1);
        }
        for (BookedLesson booking: bookings) {
            if (booking.state()) {
                if (!students1.contains(booking.getStudent())) {
                    booking.notAssisted();
                    lessonBookings.persist(booking);
                }
            }
        }
        for (Student student: students1) {
            BookedLesson booking = lessonBookings.findBookingByLessonAndStudent(lesson, student);
            if (!booking.state()) {
                res.status(400);
                return "Invalid student";
            }
            booking.assisted();
            lessonBookings.persist(booking);
        }

        return "Assistance checked";
    }

    public boolean correctDateAndTime(LocalDate date, LocalTime time) {
        LocalDate nowDate = LocalDate.now();
        if (date.isBefore(nowDate)) {
            return false;
        }
        else if (date.isAfter(nowDate)) {
            return false;
        }
        else {
            LocalTime nowTime = LocalTime.now();

            if ((nowTime.equals(time) || nowTime.isAfter(time)) && nowTime.isBefore(time.plusHours(1))) {
                return true;
            }
            else if (nowTime.isBefore(time)) {
                return false;
            }
            return false;
        }
    }

    public String compareTodayDate(Request req, Response res) {
        String dateString = req.queryParams("date");
        String timeString = req.queryParams("time");
        LocalDate date = LocalDate.parse(dateString);
        LocalDate nowDate = LocalDate.now();
        if (date.isBefore(nowDate)) {
            return "Past";
        }
        else if (date.isAfter(nowDate)) {
            return "Future";
        }
        else if (date.equals(nowDate)) {
            LocalTime time = LocalTime.parse(timeString);
            LocalTime nowTime = LocalTime.now();

            if ((nowTime.equals(time) || nowTime.isAfter(time)) && nowTime.isBefore(time.plusHours(1))) {
                return "Present";
            }
            else if (nowTime.isBefore(time)) {
                return "Future";
            }
            return "Past";
        }
        else {
            return "Error";
        }
    }
    public String compareDate(Request req, Response res) {
        String dateString = req.queryParams("date");
        String timeString = req.queryParams("time");
        LocalDate date = LocalDate.parse(dateString);
        LocalDate nowDate = LocalDate.now();
        if (date.isBefore(nowDate)) {
            return "Past";
        }
        else if (date.isAfter(nowDate)) {
            return "Future";
        }
        else {
            LocalTime time = LocalTime.parse(timeString);
            LocalTime nowTime = LocalTime.now();

            if ((nowTime.equals(time) || nowTime.isAfter(time)) && nowTime.isBefore(time.plusHours(1))) {
                return "Present";
            }
            else if (nowTime.isBefore(time)) {
                return "Future";
            }
            return "Past";
        }
    }

    public String getActivity(Request req, Response res) {
        String dateParam = req.queryParams("startDate");
        String usernameParam = req.queryParams("username");
        String timeParam = req.queryParams("time");

        List<Lesson> lessons1 = lessons.findLessonsByProfessorDateAndTime(usernameParam, LocalTime.parse(timeParam), LocalDate.parse(dateParam));
        if (lessons1.isEmpty()) {
            res.status(404);  // Set appropriate status code
            return "Activity not found";
        }
        ActivityDto activityDto = new ActivityDto(lessons1.get(0).getActivity().getName());
        res.type("application/json");
        return gson.toJson(activityDto);
    }

    public String getReviewsByActivity(Request request, Response response) {
        String username = request.queryParams("username");
        String activity = request.queryParams("activity");
        List<Lesson> lessons1 = lessons.findLessonByActivityProfessor(activity, username);
        List<ReviewDto> reviewDtos = new ArrayList<>();
        for(Lesson lesson: lessons1){
            if(lesson.getState()){
                Set<Review> reviews = lesson.getReviews();
                for(Review review: reviews){
                    Student student = review.getStudent();
                    if(student==null){
                        return "Student does not exist";
                    }
                    User user = student.getUser();
                    if(user == null){
                        return "User does not exist";
                    }
                    reviewDtos.add(new ReviewDto(user.getUsername(), review.getComment(), review.getRating().toString(), review.getLesson().getName(), review.getLesson().getStartDate().toString(), review.getLesson().getTime().toString()));
                }
            }
        }
        response.type("application/json");
        return gson.toJson(reviewDtos);
    }
}

