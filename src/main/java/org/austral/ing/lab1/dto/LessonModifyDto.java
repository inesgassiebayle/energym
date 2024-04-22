package org.austral.ing.lab1.dto;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LessonModifyDto {
    private String oldName;
    private String oldTime;  // Should match the ISO_LOCAL_TIME format (HH:mm:ss)
    private String oldDate;
    private String name;
    private String time;
    private String activity;
    private String oldProfessor;
    private String professor;
    private String startDate;
    private String roomName;



    public LessonModifyDto(String oldName, String oldTime, String oldDate, String name, String time, String activity, String professor, String oldProfessor, String roomName, String startDate) {
        this.oldName = oldName;
        this.oldTime = oldTime;
        this.oldDate = oldDate;
        this.oldProfessor = oldProfessor;
        this.name = name;
        this.time = time;
        this.activity = activity;
        this.professor = professor;
        this.roomName = roomName;
        this.startDate = startDate;

    }


    // Getters and setters for each field
    public String getName() {
        return name;
    }

    public LocalTime getTime() {
        return LocalTime.parse(time, DateTimeFormatter.ISO_LOCAL_TIME);
    }

    public LocalDate getStartDate() {
        return LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public String getActivity() {
        return activity;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getProfessor() {
        return professor;
    }
    public String getOldName(){return oldName;}
    public LocalTime getOldTime() {
        return LocalTime.parse(oldTime, DateTimeFormatter.ISO_LOCAL_TIME);
    }

    public LocalDate getOldDate() {
        return LocalDate.parse(oldDate, DateTimeFormatter.ISO_LOCAL_DATE);
    }
    public String getOldProfessor(){return oldProfessor;}


}