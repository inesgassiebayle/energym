package org.austral.ing.lab1.model;
import com.google.gson.Gson;
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

    @Column()
    private LocalDate startDate;

    @Column()
    private LocalTime time;

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

    @Column()
    private LocalDate endDate;



    public Lesson(String name, LocalTime time, LocalDate startDate) {
        this.name = name;
        this.time = time;
        this.startDate = startDate;
    }




    public Lesson() {

    }





    public String asJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
