package org.austral.ing.lab1.controller;
import com.google.gson.Gson;
import org.austral.ing.lab1.dto.*;
import org.austral.ing.lab1.model.*;
import org.austral.ing.lab1.queries.*;

import spark.Request;
import spark.Response;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
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
        //fijarme que no se solapen horarios
        if (!lessons.isTimeSlotAvailable(lessonDto.getTime(), lessonDto.getStartDate())) {
            res.status(409);
            return "Time slot is not available";
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

            if (!lessons.isTimeSlotAvailable(lessonDto.getTime(), startDate)) {
                res.status(409);
                return "Time slot is not available";
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
        if(conflictingLessons == null) return true;
        return conflictingLessons.isEmpty();
    }

    public boolean isRoomAvailable(String roomName, LocalTime time, LocalDate date) {
        List<Lesson> conflictingLessons = lessons.findLessonsByRoomAndTime(roomName, time, date);
        if(conflictingLessons == null) return true;
        return conflictingLessons.isEmpty();
    }


    public String deleteLesson(Request req, Response res) {
        LessonNameTimeDateDto deletionDto = gson.fromJson(req.body(), LessonNameTimeDateDto.class);
        String lessonName = deletionDto.getName();
        LocalDate lessonDate = deletionDto.getDate();
        LocalTime lessonTime = deletionDto.getTime();

        Lesson lesson = lessons.findLessonByNameDateAndTime(lessonName, lessonDate, lessonTime);
        if (lesson == null) {
            res.status(404);
            return "Lesson not found";
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
        for (Lesson lesson: lessons1){
            lessonInfo.add(new LessonNameTimeDto(lesson.getName(), lesson.getTime().toString()));
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

        // Fetch and set Activity
        Activity newActivity = getActivityByName(modifyDto.getActivity());
        if (newActivity == null) {
            res.status(404);
            return "Activity not found";
        }

        // Fetch and set Professor
        Professor newProfessor = getProfessorByUsername(modifyDto.getProfessor());
        if (newProfessor == null) {
            res.status(404);
            return "Professor not found";
        }

        //get room
        Room newRoom = rooms.findRoomByName(modifyDto.getRoomName());
        if (newRoom == null) {
            res.status(404);
            return "Room not found";
        }

        if (!newRoom.getActivities().contains(newActivity)) {
            res.status(409);
            return "Activity not supported in the selected room";
        }

        if (!isProfessorAvailable(modifyDto.getProfessor(), modifyDto.getTime(), newDate)) {
            res.status(409);
            return "Professor is not available at " + newDate.toString();
        }
        if (!isRoomAvailable(modifyDto.getRoomName(), modifyDto.getTime(), newDate)) {
            res.status(409);
            return "Room is not available at " + newDate.toString();
        }

        if (!lessons.isTimeSlotAvailable(modifyDto.getTime(), newDate)) {
            res.status(409);
            return "Time slot is not available";
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

        return "exito!"+  oldLesson.asJson() ;

    }
}
