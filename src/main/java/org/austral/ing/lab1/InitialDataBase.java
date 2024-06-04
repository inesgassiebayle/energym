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

    public void createStudent() {
        User user = new User("stud1", "stud1", "inegassiebayle@gmail.com", "stud1", "Stud123");
        Student student = new Student();
        user.setType(UserType.STUDENT);
        users.persist(user);
        student.setUser(user);
        students.persist(student);
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

    public void createRoom() {
        Room room = new Room("Room1", 10);
        room.setActivity(activities.findActivityByName("Yoga"));
        room.setActivity(activities.findActivityByName("Pilates"));
        room.setActivity(activities.findActivityByName("Functional"));
        rooms.persist(room);
    }

    public void createLesson() {
        LocalTime time = LocalTime.parse("10:00", DateTimeFormatter.ofPattern("HH:mm"));
        LocalDate date = LocalDate.parse("2024-06-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        Lesson lesson = new Lesson("Cardio Workout", time, date);
        lesson.setActivity(activities.findActivityByName("Functional"));
        lesson.setProfessor(professors.findProfessorByUsername("prof1"));
        lesson.setRoom(rooms.findRoomByName("Room1"));
        lessons.persist(lesson);

        LocalTime time1 = LocalTime.parse("09:00", DateTimeFormatter.ofPattern("HH:mm"));
        LocalDate date1 = LocalDate.parse("2024-06-04", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        Lesson lesson1 = new Lesson("Workout", time1, date1);
        lesson1.setActivity(activities.findActivityByName("Functional"));
        lesson1.setProfessor(professors.findProfessorByUsername("prof1"));
        lesson1.setRoom(rooms.findRoomByName("Room1"));
        lessons.persist(lesson1);
    }

    public void createBooking() {
        Lesson lesson = lessons.findLessonByName("Cardio Workout");
        Student student = students.findStudentByUsername("stud1");
        BookedLesson booking = new BookedLesson(student, lesson);
        booking.assisted();
        lessonBookings.persist(booking);

        Lesson lesson1 = lessons.findLessonByName("Workout");
        Student student1 = students.findStudentByUsername("stud1");
        BookedLesson booking1 = new BookedLesson(student1, lesson1);
        lessonBookings.persist(booking1);
    }


    public void createInitialDataBase(){
        createAdministrator();
        createProfessors();
        createActivities();
        createRoom();
        createLesson();
        createStudent();
        createBooking();
    }

}
