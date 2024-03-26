package org.austral.ing.lab1;

import org.austral.ing.lab1.model.*;
import org.austral.ing.lab1.model.Class;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Main {

    public static void main(String[] args) {
        final EntityManagerFactory factory = Persistence.createEntityManagerFactory("energym");
        final EntityManager entityManager = factory.createEntityManager();

        users(entityManager);

        entityManager.close();
    }

    private static void users(EntityManager entityManager){
        Administrator user = new Administrator("Ines", "Gassiebayle", "inegassiebayle@gmail.com", "inegassiebayle", "hola123456");
        entityManager.getTransaction().begin();
        entityManager.persist(user);

        Student student = new Student("Luz", "Laura", "llaura@gmail.com", "luzlaura", "2j2j2j2j2j2j2j");
        entityManager.persist(student);

        Professor user3 = new Professor("Clara", "Lopez", "mclaralopezz@gmail.com", "claralopez", "micumpleaños12");
        entityManager.persist(user3);

        Room room = new Room("Pileta", 20);
        room.setAdministrator(user);
        user.getRooms().add(room);
        entityManager.persist(room);

        Activity activity = new Activity("Natación");
        entityManager.persist(activity);

        Room persistedRoom = entityManager.find(Room.class, room.getRoomId());
        Activity persistedActivity = entityManager.find(Activity.class, activity.getActivityId());
        persistedRoom.getActivities().add(persistedActivity);
        persistedActivity.getRooms().add(persistedRoom);

        entityManager.merge(persistedRoom);
        entityManager.merge(persistedActivity);

        org.austral.ing.lab1.model.Class clase = new Class("Aquagym", LocalDate.of(2024, 4, 9), LocalTime.of(8, 30));
        clase.setActivity(activity);
        clase.setRoom(room);
        clase.setProfessor(user3);
        entityManager.persist(clase);

        student.getClasses().add(clase);
        clase.getStudents().add(student);

        Review review = new Review("Very good class!", 4);
        review.setStudent(student);
        review.setLesson(clase);
        entityManager.persist(review);

        Membership membership = new Membership(LocalDate.of(2026, 5, 10), "11111111111", "000");
        entityManager.persist(membership);
        membership.setStudent(student);
        student.setMembership(membership);

        entityManager.getTransaction().commit();
    }



}
