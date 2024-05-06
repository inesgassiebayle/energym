package org.austral.ing.lab1.dto;

import java.time.LocalDate;

public class ConcurrentBookingDto {
    private String day;
    private LocalDate startDay;
    private LocalDate endDay;
    public ConcurrentBookingDto(String day, LocalDate startDay, LocalDate endDay) {
        this.day = day;
        this.startDay = startDay;
        this.endDay = endDay;
    }

    public String getDay() {
        return day;
    }

    public LocalDate getStartDay() {
        return startDay;
    }

    public LocalDate getEndDay() {
        return endDay;
    }
}
