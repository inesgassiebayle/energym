package org.austral.ing.lab1.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;


@Entity
public class Review {
    @Id
    @GeneratedValue(generator = "userGen", strategy = GenerationType.SEQUENCE)
    private Long reviewId;

    @Column()
    private String comment;

    public String getComment(){
        return comment;
    }

    @Column()
    private boolean state;

    public boolean state() {
        return state;
    }

    public void activate() {
        state = true;
    }

    public void deactivate() {
        state = false;
    }

    @Column()
    @Min(0)
    @Max(5)
    private Integer rating;

    public Integer getRating(){
        return rating;
    }

    @ManyToOne
    @JoinColumn(name = "studentId")
    private Student student;

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    @ManyToOne
    @JoinColumn(name = "class_id")
    private Lesson lesson;

    public Lesson getLesson() {
        return lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Review(String comment, Integer rating, Student student, Lesson lesson){
        this.comment = comment;
        this.rating = rating;
        this.student = student;
        this.lesson = lesson;
        this.state = true;
    }

    public Review(Integer rating){
        this.rating = rating;
    }

    public Review(){

    }

    public String asJson(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("reviewId", this.reviewId);
        jsonObject.addProperty("comment", this.comment);
        jsonObject.addProperty("rating", this.rating.toString());
        jsonObject.addProperty("student", this.student.getUser().getUsername());
        jsonObject.addProperty("lesson", this.lesson.getName());
        return jsonObject.toString();
    }

}
