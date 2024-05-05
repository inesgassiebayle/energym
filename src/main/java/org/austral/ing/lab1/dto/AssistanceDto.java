package org.austral.ing.lab1.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class AssistanceDto {
    private String date;
    private String time;
    private String professor;
    private String students;

    public AssistanceDto(String date, String time, String professor, String students){
        this.date = date;
        this.time = time;
        this.professor = professor;
        this.students = students;
    }

    public AssistanceDto(){}

    public String getProfessor(){
        return professor;
    }

    public LocalTime getTime(){
        return  LocalTime.parse(time, DateTimeFormatter.ISO_LOCAL_TIME);
    }

    public LocalDate getDate(){
        return LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public String[] getStudents(){
        if (students == null | students.isEmpty()) {
            return new String[0];
        }
        return students.split(",");
    }
}
