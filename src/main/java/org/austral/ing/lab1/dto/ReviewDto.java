package org.austral.ing.lab1.dto;

public class ReviewDto {
    private String username;
    private String comment;
    private String rating;
    private String lessonName;
    private String lessonDate;
    private String lessonTime;
    public ReviewDto(String username, String comment, String rating, String lessonName, String lessonDate, String lessonTime){
        this.username = username;
        this.comment = comment;
        this.lessonDate = lessonDate;
        this.lessonName = lessonName;
        this.lessonTime = lessonTime;
        this.rating = rating;
    }

    public String getUsername(){
        return username;
    }

    public String getComment(){
        return comment;
    }

    public String getRating(){
        return rating;
    }
    public String getLessonName() {
        return lessonName;
    }
    public String getLessonTime() {
        return lessonTime;
    }
    public String getLessonDate() {
        return lessonDate;
    }

}
