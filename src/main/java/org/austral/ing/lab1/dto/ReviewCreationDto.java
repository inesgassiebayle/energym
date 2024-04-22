package org.austral.ing.lab1.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ReviewCreationDto {
    private String comment;
    private String rating;
    private String username;
    private String professor;
    private String lessonDate;

    private String lessonTime;
    public ReviewCreationDto(String comment, String rating, String username, String professor, String lessonDate, String lessonTime){
        this.comment = comment;
        this.rating = rating;
        this.username = username;
        this.professor = professor;
        this.lessonDate = lessonDate;
        this.lessonTime = lessonTime;
    }

    public String getRating() {
        return rating;
    }

    public String getProfessor() {
        return professor;
    }

    public String getComment() {
        return comment;
    }

    public String getUsername() {
        return username;
    }
    public LocalDate getDate(){
        return LocalDate.parse(lessonDate, DateTimeFormatter.ISO_LOCAL_DATE);
    }
    public LocalTime getTime() { return LocalTime.parse(lessonTime, DateTimeFormatter.ISO_LOCAL_TIME);}

}
