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
        User professor1 = new User("prof1", "prof1", "prof1@gmail.com", "prof1", "Prof123");
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

    public void createRooms(){
        Room room = new Room("Closed Swimming Pool", 20);
        Activity swimming = activities.findActivityByName("Swimming");
        Activity aquagym = activities.findActivityByName("Aquagym");
        room.setActivity(swimming);
        room.setActivity(aquagym);
        rooms.persist(room);
        Room room2 = new Room("Open Swimming Pool", 30);
        room2.setActivity(swimming);
        rooms.persist(room2);
        Room room3 = new Room("Lounge", 15);
        Activity yoga = activities.findActivityByName("Yoga");
        Activity pilates = activities.findActivityByName("Pilates");
        room3.setActivity(yoga);
        room3.setActivity(pilates);
        rooms.persist(room3);
    }

    public void createLessons(){
        LocalTime time = LocalTime.parse("20:00:00", DateTimeFormatter.ISO_LOCAL_TIME);
        LocalDate date = LocalDate.parse("2024-05-05", DateTimeFormatter.ISO_LOCAL_DATE);
        Lesson lesson = new Lesson("Relax and Stretch", time, date);
        lesson.setRoom(rooms.findRoomByName("Lounge"));
        lesson.setActivity(activities.findActivityByName("Yoga"));
        lesson.setProfessor(professors.findProfessorByUsername("prof1"));
        lessons.persist(lesson);
        Lesson lesson2 = new Lesson("Workout", LocalTime.parse("22:00:00", DateTimeFormatter.ISO_LOCAL_TIME), LocalDate.parse("2024-05-04", DateTimeFormatter.ISO_LOCAL_DATE));
        lesson2.setRoom(rooms.findRoomByName("Lounge"));
        lesson2.setActivity(activities.findActivityByName("Yoga"));
        lesson2.setProfessor(professors.findProfessorByUsername("prof1"));
        lessons.persist(lesson2);
    }

    public void createStudents(){
        User student1 = new User("stud1", "stud1", "stud1@gmail.com", "stud1", "Stud123");
        student1.setType(UserType.STUDENT);
        users.persist(student1);
        Student stud1 = new Student();
        stud1.setUser(student1);
        students.persist(stud1);
        User student2 = new User("stud2", "stud2", "stud2@gmail.com", "stud2", "Stud123");
        student2.setType(UserType.STUDENT);
        users.persist(student2);
        Student stud2 = new Student();
        stud2.setUser(student2);
        students.persist(stud2);
    }

    public void bookClass(){
        Student student = students.findStudentByUsername("stud1");
        Lesson lesson = lessons.findLessonByName("Relax and Stretch");
        BookedLesson booking = new BookedLesson(student, lesson);
        booking.assisted();
        lessonBookings.persist(booking);
        Student student2 = students.findStudentByUsername("stud2");
        Lesson lesson2 = lessons.findLessonByName("Workout");
        BookedLesson booking2 = new BookedLesson(student2, lesson2);
        lessonBookings.persist(booking2);
    }

    public void createInitialDataBase(){
        createAdministrator();
        createProfessors();
        createActivities();
        createRooms();
        createLessons();
        createStudents();
        bookClass();
    }
}
