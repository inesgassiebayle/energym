package org.austral.ing.lab1.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LessonNameTimeDateDto {
    String name;
    String startDate;
    String time;

    public LessonNameTimeDateDto(String name, String startDate, String time){
        this.name = name;
        this.startDate = startDate;
        this.time = time;
    }

    public String getName(){
        return name;
    }

    public LocalDate getDate(){
        return LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public LocalTime getTime() { return LocalTime.parse(time, DateTimeFormatter.ISO_LOCAL_TIME);
    }
}
