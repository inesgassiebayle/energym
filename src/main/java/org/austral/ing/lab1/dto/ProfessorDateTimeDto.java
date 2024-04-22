package org.austral.ing.lab1.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ProfessorDateTimeDto {
    String username;
    String startDate;
    String time;

    public ProfessorDateTimeDto(String username, String startDate, String time){
        this.username = username;
        this.startDate = startDate;
        this.time = time;
    }

    public String getName(){
        return username;
    }

    public LocalDate getDate(){
        return LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public LocalTime getTime() { return LocalTime.parse(time, DateTimeFormatter.ISO_LOCAL_TIME);
    }
}
