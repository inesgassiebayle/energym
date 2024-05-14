package org.austral.ing.lab1.controller;
import com.google.gson.Gson;
import org.austral.ing.lab1.dto.*;
import org.austral.ing.lab1.model.*;
import org.austral.ing.lab1.queries.*;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LessonController{
    private final Lessons lessons;
    private final Activities activities;
    private final Professors professors;
    private final Rooms rooms;
    private final Students students4;
    private final LessonBookings lessonBookings;
    private final Gson gson = new Gson();

    public LessonController() {
        this.lessons = new Lessons();
        this.activities = new Activities();
        this.professors = new Professors();
        this.rooms = new Rooms();
        this.students4 = new Students();
        this.lessonBookings = new LessonBookings();
    }
    public String addSingleLesson(Request req, Response res) {
        LessonCreationDto lessonDto = gson.fromJson(req.body(), LessonCreationDto.class);

        String name = lessonDto.getName();
        LocalDate date = lessonDto.getStartDate();
        LocalTime time = lessonDto.getTime();

        if(name == null || date == null || time == null){
            res.status(400);
            return "Invalid input";
        }

        Lesson lesson = new Lesson(name, time, date);

        //get activity
        Activity activity = getActivityByName(lessonDto.getActivity());
        if (activity != null) {
            if(activity.state()){
                lesson.setActivity(activity);
            }
            else{
                res.status(404);
                return "Activity not found";
            }
        } else {
            // Handle case where activity is not found
            res.status(404);
            return "Activity not found";
        }

        //get professor
        Professor professor = getProfessorByUsername(lessonDto.getProfessor());
        if (professor != null) {
            if(professor.getUser().state()){
                lesson.setProfessor(professor);
            }
            else{
                // Handle case where professor is not found
                res.status(404);
                return "Professor not found";
            }
        } else {
            // Handle case where professor is not found
            res.status(404);
            return "Professor not found";
        }

        //get room
        Room room = rooms.findRoomByName(lessonDto.getRoomName());

        if (room != null) {
            if(room.state()){
                lesson.setRoom(room);
            }
            else{
                // Handle case where professor is not found
                res.status(404);
                return "Room not found";
            }
        } else {
            // Handle case where professor is not found
            res.status(404);
            return "Room not found";
        }


        //fijarme que la activity de pueda realizar en el room especificado
        if (!room.getActivities().contains(activity)) {
            res.status(409);
            return "Activity not supported in the selected room";
        }

        //fijarme que el profesor no este ocupado en el horiario definido
        if (!isProfessorAvailable(lessonDto.getProfessor(), lessonDto.getTime(), lessonDto.getStartDate())) {
            res.status(409);
            return "Professor is not available at the specified time";
        }
        //fijarme que el room no este en uso por otra actividad a la hora de la lesson
        if (!isRoomAvailable(lessonDto.getRoomName(), lessonDto.getTime(), lessonDto.getStartDate())) {
            res.status(409);
            return "Room is not available at the specified time";
        }

        lessons.persist(lesson);
        res.type("application/json");
        return lesson.asJson();
    }

    public String addConcurrentLessons(Request req, Response res) {
        // Parse the JSON body to the ConcurrentLessonDto
        ConcurrentLessonCreationDto lessonDto = gson.fromJson(req.body(), ConcurrentLessonCreationDto.class);

        // Fetch and set Activity
        Activity activity = getActivityByName(lessonDto.getActivity());
        if (activity == null) {
            res.status(404);
            return "Activity not found";
        }

        if(!activity.state()){
            res.status(404);
            return "Activity not found";
        }

        // Fetch and set Professor
        Professor professor = getProfessorByUsername(lessonDto.getProfessor());
        if (professor == null) {
            res.status(404);
            return "Professor not found";
        }

        if(!professor.getUser().state()){
            res.status(404);
            return "Professor not found";
        }

        //get room
        Room room = rooms.findRoomByName(lessonDto.getRoomName());
        if (room == null) {
            res.status(404);
            return "Room not found";
        }
        if(!room.state()){
            res.status(404);
            return "Room not found";
        }

        if (!room.getActivities().contains(activity)) {
            res.status(409);
            return "Activity not supported in the selected room";
        }

        // Create multiple lesson instances
        Set<Lesson> lessonsToAdd = new HashSet<>();
        LocalDate startDate = lessonDto.getStartDate();
        LocalDate endDate = lessonDto.getEndDate();

        while (!startDate.isAfter(endDate)) {

            if (!isProfessorAvailable(lessonDto.getProfessor(), lessonDto.getTime(), startDate)) {
                res.status(409);
                return "Professor is not available at " + startDate.toString();
            }
            if (!isRoomAvailable(lessonDto.getRoomName(), lessonDto.getTime(), startDate)) {
                res.status(409);
                return "Room is not available at " + startDate.toString();
            }



            Lesson lesson = new Lesson(
                    lessonDto.getName(),
                    lessonDto.getTime(),
                    startDate
            );

            // Set the fetched Activity and Professor to each Lesson
            lesson.setActivity(activity);
            lesson.setProfessor(professor);
            lesson.setRoom(room);


            lessonsToAdd.add(lesson);
            startDate = startDate.plusWeeks(1);
        }

        // Persist all the lessons
        lessonsToAdd.forEach(lesson -> lessons.persist(lesson));

        res.type("application/json");
        return "lesson.asJson()";
    }


    private Activity getActivityByName(String name) {
        return activities.findActivityByName(name);
    }

    private Professor getProfessorByUsername(String username) {
        return professors.findProfessorByUsername(username);
    }

    public boolean isProfessorAvailable(String professorUsername, LocalTime time, LocalDate date) {
        List<Lesson> conflictingLessons = lessons.findLessonsByProfessorDateAndTime(professorUsername, time, date);
        if (conflictingLessons == null) return true;
        List<Lesson> aliveLessons = new ArrayList<>();
        for(Lesson lesson: conflictingLessons){
            if(lesson.getState()){
                aliveLessons.add(lesson);
            }
        }
        return aliveLessons.isEmpty();
    }

    public boolean isRoomAvailable(String roomName, LocalTime time, LocalDate date) {
        List<Lesson> conflictingLessons = lessons.findLessonsByRoomAndTime(roomName, time, date);
        if (conflictingLessons == null) return true;
        List<Lesson> aliveLessons = new ArrayList<>();
        for(Lesson lesson: conflictingLessons){
            if(lesson.getState()){
                aliveLessons.add(lesson);
            }
        }
        return aliveLessons.isEmpty();
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
        if(professor == null){
            res.status(400);
            return "Professor does not exist";
        }

        Lesson lesson = lessons.findLessonsByProfessorDateAndTime(username, lessonTime, lessonDate).get(0);

        if (lesson == null ) {
            res.status(404);
            return "Lesson not found";
        }

        if (!lesson.getState()){
            res.status(404);
            return "Lesson state false";
        }

        lesson.deactivate();
        this.lessons.persist(lesson);

        res.type("application/json");
        return gson.toJson("Deactivated lesson(s) by: " + username + " and date: " + lessonDate + " and time: " + lessonTime);
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
        String oldName = modifyDto.getOldName();
        LocalDate oldDate = modifyDto.getOldDate();
        LocalTime oldTime = modifyDto.getOldTime();
        Lesson oldLesson = lessons.findLessonsByProfessorDateAndTime(oldProfessor, oldTime, oldDate).get(0);
        String newName = modifyDto.getName();
        LocalTime newTime = modifyDto.getTime();
        LocalDate newDate = modifyDto.getStartDate();
        Activity newActivity = getActivityByName(modifyDto.getActivity());
        Professor newProfessor = getProfessorByUsername(modifyDto.getProfessor());
        Room newRoom = rooms.findRoomByName(modifyDto.getRoomName());

        if(oldLesson == null){
            res.status(404);
            return "Lesson not found";
        }
        if(!oldLesson.getState()){
            res.status(404);
            return "Lesson not found";
        }

        if(newActivity == null || newRoom == null || newProfessor == null){
            res.status(404);
            return "Activity, Professor or Room not found";
        }

        if(!newActivity.state() || !newRoom.state() || !newProfessor.getUser().state()){
            res.status(404);
            return "Activity, Professor or Room not found";
        }

        boolean professorChanged = !oldLesson.getProfessor().equals(newProfessor);
        boolean roomChanged = !oldLesson.getRoom().equals(newRoom);
        boolean activityChanged = !oldLesson.getActivity().equals(newActivity);
        boolean dateChanged = !oldLesson.getStartDate().equals(newDate);
        boolean timeChanged = !oldLesson.getTime().equals(newTime);


        if (professorChanged && !isProfessorAvailable(newProfessor.getUser().getUsername(), newTime, newDate)) {
            res.status(409);
            return "Professor is not available";
        }

        if (roomChanged && (!isRoomAvailable(newRoom.getName(), newTime, newDate) ||
                !newRoom.getActivities().contains(newActivity))) {
            res.status(409);
            return "Room is not available or does not support the activity";
        }

        if (activityChanged && !newRoom.getActivities().contains(newActivity)) {
            res.status(409);
            return "New activity not supported in the selected room";
        }

        if (dateChanged || timeChanged) {

            // Si el profesor no cambió pero la fecha/hora sí, verificar la disponibilidad del profesor en el nuevo horario
            if (!professorChanged && !isProfessorAvailable(oldLesson.getProfessor().getUser().getUsername(), newTime, newDate)) {
                res.status(409);
                return "Professor is not available at the new time/date";
            }

            // Si la sala no cambió pero la fecha/hora sí, verificar la disponibilidad de la sala en el nuevo horario
            if (!roomChanged && !isRoomAvailable(oldLesson.getRoom().getName(), newTime, newDate)) {
                res.status(409);
                return "Room is not available at the new time/date";
            }

            // Si el profesor cambió, y también la fecha/hora, verificar la disponibilidad del nuevo profesor
            if (professorChanged && !isProfessorAvailable(newProfessor.getUser().getUsername(), newTime, newDate)) {
                res.status(409);
                return "New professor is not available at the new time/date";
            }

            // Si la sala cambió, y también la fecha/hora, verificar la disponibilidad de la nueva sala
            if (roomChanged && (!isRoomAvailable(newRoom.getName(), newTime, newDate) || !newRoom.getActivities().contains(newActivity))) {
                res.status(409);
                return "New room is not available or does not support the activity at the new time/date";
            }
        }

        oldLesson.setName(newName);
        oldLesson.setRoom(newRoom);
        oldLesson.setActivity(newActivity);
        oldLesson.setProfessor(newProfessor);
        oldLesson.setTime(newTime);
        oldLesson.setStartDate(newDate);

        lessons.persist(oldLesson);

        return "exito! " +  oldLesson.asJson();

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
            reviewDtos.add(new ReviewDto(user.getUsername(), review.getComment(), review.getRating().toString()));
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
        LocalDate date = LocalDate.parse(dateString);
        LocalDate nowDate = LocalDate.now();
        if (date.isBefore(nowDate)) {
            return "Past";
        }
        else if (date.isAfter(nowDate)) {
            return "Future";
        }
        else if (date.equals(nowDate)) {
            return "Present";
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
}

