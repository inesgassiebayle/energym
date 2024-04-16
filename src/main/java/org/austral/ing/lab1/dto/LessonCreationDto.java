package org.austral.ing.lab1.dto;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LessonCreationDto {
    private String name;
    private String time;  // Should match the ISO_LOCAL_TIME format (HH:mm:ss)
    private String activity;

    private String professor;
    private String startDate; // Should match the ISO_LOCAL_DATE format (yyyy-MM-dd)

    private String roomName;



    public LessonCreationDto(String name, String time, String activity, String professor, String roomName, String startDate) {
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



}