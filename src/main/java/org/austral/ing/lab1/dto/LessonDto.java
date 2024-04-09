package org.austral.ing.lab1.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LessonDto {
    private String name;
    private String time;  // Should match the ISO_LOCAL_TIME format (HH:mm:ss)

    private String activity;
    private String professor;
    private String startDate; // Should match the ISO_LOCAL_DATE format (yyyy-MM-dd)

    public LessonDto() {}

    public LessonDto(String name, String time, String activity, String professor, String startDate) {
        this.name = name;
        this.time = time;
        this.activity = activity;
        this.professor = professor;
        this.startDate = startDate;
    }

    // Getters and setters for each field
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public LocalTime getTime() {
        return LocalTime.parse(time, DateTimeFormatter.ISO_LOCAL_TIME);
    }

    public void setTime(String time) {
        this.time = time;
    }

    public LocalDate getStartDate() {
        return LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }


    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity){
        this.activity = activity;
    }
    public String getProfessor() {
        return professor;
    }

    public void setProfessor(String professor){
        this.professor = professor;
    }


}
