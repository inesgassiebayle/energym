package org.austral.ing.lab1.model;
import com.google.gson.JsonObject;
import org.austral.ing.lab1.controller.EmailSender;
import org.austral.ing.lab1.controller.ReminderService;

import javax.persistence.*;
import java.util.concurrent.CompletableFuture;

@Entity
public class BookedLesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;

    public Long getId() {
        return reservationId;
    }

    @ManyToOne
    private Student student;

    public Student getStudent() {
        return student;
    }

    @ManyToOne
    private Lesson lesson;

    public Lesson getLesson() {
        return lesson;
    }

    @Column
    private boolean assistance = false;

    @Transient
    private EmailSender emailSender;

    @Transient
    private ReminderService reminderService;

    public BookedLesson() {

    }

    public boolean hasAssisted() {
        return assistance;
    }

    public void assisted() {
        assistance = true;
    }
    public void notAssisted() {
        assistance = false;
    }

    @Column
    private boolean state = true;

    public boolean state() {
        return state;
    }

    public void deactivate() {
        state = false;
    }

    public void activate() {
        state = true;
    }

    public BookedLesson(Student student, Lesson lesson) {
        this.student = student;
        this.lesson = lesson;
        student.addBooking(this);
        lesson.addBooking(this);

    }

    public String asJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("student", this.student.getUser().getUsername());
        jsonObject.addProperty("time", this.lesson.getTime().toString());
        jsonObject.addProperty("date", this.lesson.getStartDate().toString());
        jsonObject.addProperty("professor", this.lesson.getProfessor().getUser().getUsername());
        return jsonObject.toString();
    }
}
