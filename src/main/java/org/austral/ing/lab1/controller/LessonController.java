package org.austral.ing.lab1.controller;
import com.google.gson.Gson;
import org.austral.ing.lab1.dto.*;
import org.austral.ing.lab1.model.*;
import org.austral.ing.lab1.queries.*;
import spark.Request;
import spark.Response;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LessonController{
    private final Lessons lessons;
    private final Activities activities;
    private final Professors professors;
    private final Rooms rooms;
    private final Gson gson = new Gson();

    public LessonController() {
        this.lessons = new Lessons();
        this.activities = new Activities();
        this.professors = new Professors();
        this.rooms = new Rooms();
    }
    public String addSingleLesson(Request req, Response res) {
        LessonCreationDto lessonDto = gson.fromJson(req.body(), LessonCreationDto.class);

        Lesson lesson = new Lesson(
                lessonDto.getName(),
                lessonDto.getTime(),
                lessonDto.getStartDate()
        );

        //get activity
        Activity activity = getActivityByName(lessonDto.getActivity());
        if (activity != null) {
            lesson.setActivity(activity);
        } else {
            // Handle case where activity is not found
            res.status(404);
            return "Activity not found";
        }

        //get professor
        Professor professor = getProfessorByUsername(lessonDto.getProfessor());
        if (professor != null) {
            lesson.setProfessor(professor);
        } else {
            // Handle case where professor is not found
            res.status(404);
            return "Professor not found";
        }

        //get room
        Room room = rooms.findRoomByName(lessonDto.getRoomName());

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


        if (room != null) {
            lesson.setRoom(room);
        } else {
            // Handle case where professor is not found
            res.status(404);
            return "Room not found";
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

        // Fetch and set Professor
        Professor professor = getProfessorByUsername(lessonDto.getProfessor());
        if (professor == null) {
            res.status(404);
            return "Professor not found";
        }

        //get room
        Room room = rooms.findRoomByName(lessonDto.getRoomName());
        if (room == null) {
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
        List<Lesson> conflictingLessons = lessons.findLessonsByProfessorAndTime(professorUsername, time, date);
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
        LessonNameTimeDateDto deletionDto = gson.fromJson(req.body(), LessonNameTimeDateDto.class);
        String lessonName = deletionDto.getName();
        LocalDate lessonDate = deletionDto.getDate();
        LocalTime lessonTime = deletionDto.getTime();

        Lesson lesson = lessons.findLessonByNameDateAndTime(lessonName, lessonDate, lessonTime);

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
        return gson.toJson("Deactivated lesson(s) with name: " + lessonName + " and date: " + lessonDate + " and time: " + lessonTime);
    }


    public String getLessonsByDate(Request req, Response res) {
        String dateString = req.params(":date");
        LocalDate lessonDate = LocalDate.parse(dateString);
        List<Lesson> lessons1 = lessons.findLessonsByDate(lessonDate);
        List<LessonNameTimeDto> lessonInfo = new ArrayList<>();
        for (Lesson lesson: lessons1) {
            if (lesson.getState()) {
                lessonInfo.add(new LessonNameTimeDto(lesson.getName(), lesson.getTime().toString()));
            }
        }
        res.type("application/json");
        return gson.toJson(lessonInfo);
    }

    public String lessonModify(Request req, Response res) {
        LessonModifyDto modifyDto =  gson.fromJson(req.body(), LessonModifyDto.class);

        String oldName = modifyDto.getOldName();
        LocalDate oldDate = modifyDto.getOldDate();
        LocalTime oldTime = modifyDto.getOldTime();
        Lesson oldLesson = lessons.findLessonByNameDateAndTime(oldName,oldDate,oldTime);

        String newName = modifyDto.getName();
        LocalTime newTime = modifyDto.getTime();
        LocalDate newDate = modifyDto.getStartDate();
        Activity newActivity = getActivityByName(modifyDto.getActivity());
        Professor newProfessor = getProfessorByUsername(modifyDto.getProfessor());
        Room newRoom = rooms.findRoomByName(modifyDto.getRoomName());


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


        if (newActivity == null) {
            res.status(404);
            return "Activity not found";
        }
        if (newProfessor == null) {
            res.status(404);
            return "Professor not found";
        }
        if (newRoom == null) {
            res.status(404);
            return "Room not found";
        }


        if(oldLesson.getState()){
            oldLesson.setName(newName);
            oldLesson.setRoom(newRoom);
            oldLesson.setActivity(newActivity);
            oldLesson.setProfessor(newProfessor);
            oldLesson.setTime(newTime);
            oldLesson.setStartDate(newDate);
        }
        else return "lesson state false";

        lessons.persist(oldLesson);

        return "exito! " +  oldLesson.asJson() ;

    }

    public String getLessonReviews(Request req, Response res){
        LessonNameTimeDateDto dto = gson.fromJson(req.body(), LessonNameTimeDateDto.class);
        String name = dto.getName();
        LocalDate date = dto.getDate();
        LocalTime time = dto.getTime();
        if(name == null){
            res.status(400);
            return "Invalid lesson name";
        }
        if(date==null || time == null){
            res.status(400);
            return "Invalid date or time";
        }
        Lesson lesson = lessons.findLessonByNameDateAndTime(name, date, time);
        if(lesson == null){
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
        LessonNameTimeDateDto dto = gson.fromJson(req.body(), LessonNameTimeDateDto.class);
        LocalDate date = dto.getDate();
        String name = dto.getName();
        LocalTime time = dto.getTime();

        if(name == null || date == null || time == null){
            res.status(400); // Bad Request
            return "Invalid input";
        }

        Lesson lesson = lessons.findLessonByNameDateAndTime(name, date, time);

        if(lesson == null){
            return "Lesson was not found";
        }

        return lesson.asJson();
    }
}

