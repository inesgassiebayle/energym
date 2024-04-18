package org.austral.ing.lab1.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LessonNameTimeDto {
    String name;
    String time;

    public LessonNameTimeDto(String name, String time){
        this.name = name;
        this.time = time;
    }

    public String getName(){
        return name;
    }

    public LocalTime getTime(){
        return LocalTime.parse(time, DateTimeFormatter.ISO_DATE_TIME);
    }
}



