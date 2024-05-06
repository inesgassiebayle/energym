package org.austral.ing.lab1.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class BookingDto {
    private final String professor;
    private final String student;
    private final String startDate;
    private final String endDate;

    private final String time;
    public BookingDto(String professor, String student, String startDate, String endDate, String time) {
        this.professor = professor;
        this.student = student;
        this.startDate = startDate;
        this.endDate = endDate;
        this.time = time;
    }
    public String getProfessor(){
        return professor;
    }
    public String getStudent(){
        return student;
    }
    public LocalDate getStartDate(){
        return LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE);
    }
    public LocalDate getEndDate(){
        return LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE);
    }
    public LocalTime getTime(){
        return LocalTime.parse(time, DateTimeFormatter.ISO_LOCAL_TIME);
    }
}
