package org.austral.ing.lab1.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class AssistanceDto {
    private LocalDate date;
    private LocalTime time;
    private String professor;
    private String students;

    public AssistanceDto(String date, String time, String professor, String students){
        this.date = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
        this.time = LocalTime.parse(time, DateTimeFormatter.ISO_LOCAL_TIME);
        this.professor = professor;
        this.students = students;
    }

    public AssistanceDto(){}

    public String getProfessor(){
        return professor;
    }

    public LocalTime getTime(){
        return time;
    }

    public LocalDate getDate(){
        return date;
    }

    public String[] getStudents(){
        return students.split(",");
    }
}
