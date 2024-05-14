package org.austral.ing.lab1.dto;

import java.time.*;
import java.time.format.*;

public class LessonNameTimeDateActDto {
  String name;
  String startDate;
  String time;
  String activity;
  String review;

  public LessonNameTimeDateActDto(String name, String startDate, String time , String activity, String review){
    this.name = name;
    this.startDate = startDate;
    this.time = time;
    this.activity = activity;
    this.review = review;
  }

  public String getName(){
    return name;
  }

  public LocalDate getDate(){
    return LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE);
  }

  public LocalTime getTime() { return LocalTime.parse(time, DateTimeFormatter.ISO_LOCAL_TIME);
  }

  public String getReview() {
    return review;
  }

  public String getActivity() {
    return activity;
  }
}
