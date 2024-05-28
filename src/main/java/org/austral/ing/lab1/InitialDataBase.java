package org.austral.ing.lab1;

import org.austral.ing.lab1.model.*;
import org.austral.ing.lab1.queries.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class InitialDataBase {
    private final Administrators administrators;
    private final Users users;
    private final Professors professors;
    private final Activities activities;
    private final Rooms rooms;
    private final Lessons lessons;
    private final Students students;
    private final Reviews reviews;
    private final LessonBookings lessonBookings;

    public InitialDataBase(){
        this.administrators = new Administrators();
        this.users = new Users();
        this.professors = new Professors();
        this.activities = new Activities();
        this.rooms = new Rooms();
        this.lessons = new Lessons();
        this.students = new Students();
        this.reviews = new Reviews();
        this.lessonBookings = new LessonBookings();
    }
    public void createAdministrator(){
        User user = new User("admin", "admin", "admin@gmail.com", "admin", "Admin123");
        user.setType(UserType.ADMINISTRATOR);
        users.persist(user);
        Administrator administrator = new Administrator();
        administrator.setUser(user);
        administrators.persist(administrator);
    }

    public void createProfessors() {
        User professor1 = new User("prof1", "prof1", "lucitalaura@gmail.com", "prof1", "Prof123");
        professor1.setType(UserType.PROFESSOR);
        users.persist(professor1);
        Professor prof1 = new Professor();
        prof1.setUser(professor1);
        professors.persist(prof1);
        User professor2 = new User("prof2", "prof2", "prof2@gmail.com", "prof2", "Prof123");
        professor2.setType(UserType.PROFESSOR);
        users.persist(professor2);
        Professor prof2 = new Professor();
        prof2.setUser(professor2);
        professors.persist(prof2);
    }

    public void createActivities(){
        Activity activity = new Activity("Yoga");
        activities.persist(activity);
        Activity activity2 = new Activity("Pilates");
        activities.persist(activity2);
        Activity activity3 = new Activity("Functional");
        activities.persist(activity3);
        Activity activity4 = new Activity("Swimming");
        activities.persist(activity4);
        Activity activity5 = new Activity("Aquagym");
        activities.persist(activity5);
    }


    public void createInitialDataBase(){
        createAdministrator();
        createProfessors();
        createActivities();

    }

}
