package org.austral.ing.lab1.queries;

import org.austral.ing.lab1.model.Activity;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;

public class Lesson {
    private final EntityManager entityManager;

    public Lesson(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Lesson findLessonById(Long id) {
        return entityManager.find(Lesson.class, id);
    }

    public List<Lesson> findAllLessons(){
        TypedQuery<Lesson> query = entityManager.createQuery("SELECT l FROM Lesson l", Lesson.class);
        return query.getResultList();
    }

    public Lesson findLessonByName(String name) {
        TypedQuery<Lesson> query = entityManager.createQuery("SELECT l " +
                "FROM Lesson l " +
                "WHERE l.name LIKE :name", Lesson.class);
        query.setParameter("name", name);
        List<Lesson> lessons = query.getResultList();
        if (lessons.isEmpty()) {
            return null;
        }
        return lessons.get(0);
    }

    public List<Lesson> findLessonByDate(LocalDate date){
        TypedQuery<Lesson> query = entityManager.createQuery("SELECT l " +
                "FROM Lesson l " +
                "WHERE l.date = :date", Lesson.class);
        query.setParameter("date", date);
        return query.getResultList();
    }

    public void persist(Lesson lesson) {
        entityManager.getTransaction().begin();
        entityManager.persist(lesson);
        entityManager.getTransaction().commit();
    }

    public void delete(Lesson lesson){
        entityManager.getTransaction().begin();
        entityManager.remove(lesson);
        entityManager.getTransaction().commit();
    }


}
