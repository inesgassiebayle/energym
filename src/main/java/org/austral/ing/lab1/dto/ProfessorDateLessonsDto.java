package org.austral.ing.lab1.dto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ProfessorDateLessonsDto {
    private String username;
    private String date;

    public ProfessorDateLessonsDto(String username, String date) {
        this.username = username;
        this.date = date;
    }

    public String getName() {
        return username;
    }

    public LocalDate getDate() {
        return LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
    }
}
