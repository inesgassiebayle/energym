package org.austral.ing.lab1.queries;
import org.austral.ing.lab1.model.Lesson;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.util.List;

public class Lessons {
    private final EntityManager entityManager;

    public Lessons(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Lessons findLessonById(Long id) {
        return entityManager.find(Lessons.class, id);
    }

    public List<Lessons> findAllLessons(){
        TypedQuery<Lessons> query = entityManager.createQuery("SELECT l FROM Lesson l", Lessons.class);
        return query.getResultList();
    }

    public Lessons findLessonByName(String name) {
        TypedQuery<Lessons> query = entityManager.createQuery("SELECT l " +
                "FROM Lesson l " +
                "WHERE l.name LIKE :name", Lessons.class);
        query.setParameter("name", name);
        List<Lessons> lessons = query.getResultList();
        if (lessons.isEmpty()) {
            return null;
        }
        return lessons.get(0);
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

