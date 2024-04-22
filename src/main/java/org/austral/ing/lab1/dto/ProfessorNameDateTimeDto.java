package org.austral.ing.lab1.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ProfessorNameDateTimeDto {
    String name;
    String startDate;
    String time;
    String professor;

    public ProfessorNameDateTimeDto(String name, String startDate, String time, String professor){
        this.name = name;
        this.startDate = startDate;
        this.time = time;
        this.professor = professor;
    }

    public String getName(){
        return name;
    }

    public LocalDate getDate(){
        return LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public LocalTime getTime() { return LocalTime.parse(time, DateTimeFormatter.ISO_LOCAL_TIME);
    }
    public String getProfessor(){ return professor; }
}
