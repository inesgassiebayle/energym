package org.austral.ing.lab1.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class BookingDto {
    private final String professor;
    private final String student;
    private final String date;
    private final String time;
    public BookingDto(String professor, String student, String date, String time) {
        this.professor = professor;
        this.student = student;
        this.date = date;
        this.time = time;
    }
    public String getProfessor(){
        return professor;
    }
    public String getStudent(){
        return student;
    }
    public LocalDate getDate(){
        return LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
    }
    public LocalTime getTime(){
        return LocalTime.parse(time, DateTimeFormatter.ISO_LOCAL_TIME);
    }
}
