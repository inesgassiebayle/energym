package org.austral.ing.lab1.dto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LessonDeletionDto {
    String name;
    String startDate;

    public LessonDeletionDto(String name, String startDate){
        this.name = name;
        this.startDate = startDate;
    }

    public String getName(){
        return name;
    }

    public LocalDate getDate(){
        return LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE);
    }
}
