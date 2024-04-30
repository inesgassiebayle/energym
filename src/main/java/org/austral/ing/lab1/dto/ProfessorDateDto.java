package org.austral.ing.lab1.dto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ProfessorDateDto {
    String username;
    String startDate;

    public ProfessorDateDto(String username, String startDate){
        this.username = username;
        this.startDate = startDate;
    }

    public String getName(){
        return username;
    }
    public LocalDate getDate() {
        return LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE);
    }

}
