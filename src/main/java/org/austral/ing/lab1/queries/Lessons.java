package org.austral.ing.lab1.queries;
import org.austral.ing.lab1.model.Lesson;

import javax.persistence.TypedQuery;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.austral.ing.lab1.EntityManagerController.entityManager;

public class Lessons {

    public Lessons() {

    }

    public Lesson findLessonById(Long id) {
        return entityManager().find(Lesson.class, id);
    }

    public List<Lesson> findAllLessons(){
        TypedQuery<Lesson> query = entityManager().createQuery("SELECT l FROM Lesson l", Lesson.class);
        return query.getResultList();
    }

    public Lesson findLessonByName(String name) {
        TypedQuery<Lesson> query = entityManager().createQuery("SELECT l FROM Lesson l WHERE l.name = :name", Lesson.class);
        query.setParameter("name", name);
        List<Lesson> lessons = query.getResultList();
        return lessons.isEmpty() ? null : lessons.get(0);
    }

    public List<Lesson> findLessonsByProfessorDateAndTime(String professorUsername, LocalTime time, LocalDate date) {
        TypedQuery<Lesson> query = entityManager().createQuery(
                "SELECT l FROM Lesson l WHERE l.professor.user.username = :username AND l.time = :time AND l.startDate = :date AND l.state = :state", Lesson.class);
        query.setParameter("username", professorUsername);
        query.setParameter("time", time);
        query.setParameter("date", date);
        query.setParameter("state", true);
        List<Lesson> results = query.getResultList();
        return results;
    }

    public List<Lesson> findLessonsByRoomAndTime(String roomName, LocalTime time, LocalDate date) {
        TypedQuery<Lesson> query = entityManager().createQuery(
                "SELECT l FROM Lesson l WHERE l.room.name = :roomName AND l.time = :time AND l.startDate = :date", Lesson.class);
        query.setParameter("roomName", roomName);
        query.setParameter("time", time);
        query.setParameter("date", date);
        return query.getResultList();
    }

    public boolean isTimeSlotAvailable(LocalTime startTime, LocalDate date) {
        TypedQuery<Long> query = entityManager().createQuery(
                "SELECT COUNT(l) FROM Lesson l WHERE l.time = :time AND l.startDate = :date", Long.class);
        query.setParameter("time", startTime);
        query.setParameter("date", date);
        return query.getSingleResult() == 0;  // true if no lessons at the same time and date
    }

    public Lesson findLessonByNameAndDate(String lessonName, LocalDate lessonDate){
        TypedQuery<Lesson> query = entityManager().createQuery("SELECT l FROM Lesson l WHERE l.name = :lessonName AND l.startDate = :lessonDate", Lesson.class);
        query.setParameter("lessonName", lessonName);
        query.setParameter("lessonDate", lessonDate);
        List<Lesson> lessons = query.getResultList();
        return lessons.isEmpty() ? null : lessons.get(0);
    }

    public Lesson findLessonByNameDateAndTime(String lessonName, LocalDate lessonDate, LocalTime lessonTime){
        TypedQuery<Lesson> query = entityManager().createQuery("SELECT l FROM Lesson l WHERE l.name = :lessonName AND l.startDate = :lessonDate AND l.time = :lessonTime", Lesson.class);
        query.setParameter("lessonName", lessonName);
        query.setParameter("lessonDate", lessonDate);
        query.setParameter("lessonTime", lessonTime);
        if(query.getSingleResult() == null) return null;
        return query.getSingleResult();
    }

    public List<Lesson> findLessonByActivity(String activity){
        TypedQuery<Lesson> query = entityManager().createQuery("SELECT l FROM Lesson l WHERE l.activity.name = :activity", Lesson.class);
        query.setParameter("activity", activity);
        return query.getResultList();
    }

    public List<Lesson> findLessonsByDate(LocalDate lessonDate){
        TypedQuery<Lesson> query = entityManager().createQuery("SELECT l FROM Lesson l WHERE l.startDate = :lessonDate", Lesson.class);
        query.setParameter("lessonDate", lessonDate);
        return query.getResultList();
    }



    public void persist(Lesson lesson) {
        entityManager().getTransaction().begin();
        entityManager().persist(lesson);
        entityManager().getTransaction().commit();
    }

    public void delete(Lesson lesson){
        entityManager().getTransaction().begin();
        entityManager().remove(lesson);
        entityManager().getTransaction().commit();
    }

    public List<Lesson> findConcurrentLessons(LocalTime time, String professorUsername, String name, String roomName, String activity, DayOfWeek dayOfWeek){
        TypedQuery<Lesson> query = entityManager().createQuery("SELECT l FROM Lesson l WHERE l.time = :time AND l.professor.user.username = :professorUsername AND l.name = :name AND l.room.name = :roomName AND l.activity.name = :activity", Lesson.class);
        query.setParameter("time", time);
        query.setParameter("professorUsername", professorUsername);
        query.setParameter("name", name);
        query.setParameter("roomName", roomName);
        query.setParameter("activity", activity);
        List<Lesson> lessons = query.getResultList();
        List<Lesson> concurrentLessons = new ArrayList<>();
        for (Lesson lesson: lessons){
            if (lesson.getStartDate().getDayOfWeek() == dayOfWeek && lesson.getState()){
                concurrentLessons.add(lesson);
            }
        }
        return concurrentLessons;
    }


}

