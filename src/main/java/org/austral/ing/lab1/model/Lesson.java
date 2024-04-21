package org.austral.ing.lab1.model;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Lesson {

    @Id
    @GeneratedValue(generator = "userGen", strategy = GenerationType.SEQUENCE)
    private Long classId;

    @Column()
    private String name;
    public void setName(String name) {
        this.name = name;
    }


    public String getName(){
        return name;
    }

    @Column()
    private LocalDate startDate;

    public LocalDate getStartDate(){return startDate;}
    public void setStartDate(LocalDate date){this.startDate = date; }

    @Column()
    private LocalTime time;
    public LocalTime getTime(){return time;}
    public void setTime(LocalTime time){this.time = time; }

    @ManyToOne
    @JoinColumn(name = "activityId")
    private Activity activity;

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    @ManyToOne
    @JoinColumn(name = "roomId")
    private Room room;

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    @ManyToMany(mappedBy = "classes")
    private Set<Student> students = new HashSet<>();

    public Set<Student> getStudents() {
        return students;
    }

    public void setStudents(Set<Student> students) {
        this.students = students;
    }

    @ManyToOne
    @JoinColumn(name = "professorId")
    private Professor professor;

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }

    @OneToMany(mappedBy = "lesson")
    private Set<Review> reviews = new HashSet<>();

    public Set<Review> getReviews() {
        return reviews;
    }

    public void setReviews(Set<Review> reviews) {
        this.reviews = reviews;
    }
    public void addReview(Review review){
        reviews.add(review);
    }

    @Column()
    private LocalDate endDate;

    @Column()
    private boolean state;

    public boolean getState(){
        return state;
    }

    public void activate(){
        this.state = true;
    }

    public void deactivate(){
        this.state = false;
    }


    public Lesson(String name, LocalTime time, LocalDate startDate) {
        this.name = name;
        this.time = time;
        this.startDate = startDate;
        this.reviews = new HashSet<>();
        activate();
    }

    public Lesson() {
    }


    public String asJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", this.name);
        jsonObject.addProperty("time", String.valueOf(this.time));
        jsonObject.addProperty("date", String.valueOf(this.startDate));
        jsonObject.addProperty("professor", this.getProfessor().getUser().getUsername());
        jsonObject.addProperty("room", this.getRoom().getName());
        jsonObject.addProperty("activity", this.getActivity().getName());
        return jsonObject.toString();
    }
}
