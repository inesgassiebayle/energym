package org.austral.ing.lab1.queries;

import org.austral.ing.lab1.model.*;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.util.List;

public class Professors {
    private final EntityManager entityManager;

    public Professors(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Professor findProfessorById(Long id) {
        return entityManager.find(Professor.class, id);
    }

    public Professor findProfessorByUsername(String username) {
        Users users = new Users(entityManager);

        User user = users.findUserByUsername(username);

        // Comprobar si el usuario existe
        if (user == null) {
            return null;
        }

        if(!user.getType().equals(UserType.PROFESSOR)){
            return null;
        }

        TypedQuery<Professor> query = entityManager.createQuery(
                "SELECT p FROM Professor p WHERE p.user.id = :userId", Professor.class);
        query.setParameter("userId", user.getId());

        if (query.getResultList().isEmpty()) {
            return null;
        }
        return query.getSingleResult();
    }


    public void persist(Professor professor) {
        entityManager.getTransaction().begin();
        entityManager.persist(professor);
        entityManager.getTransaction().commit();
    }

    public void delete(Professor professor){
        entityManager.getTransaction().begin();
        entityManager.remove(professor);
        entityManager.getTransaction().commit();
    }

    public List<Professor> findAllProfessors() {
        TypedQuery<Professor> query = entityManager.createQuery("SELECT p FROM Professor p", Professor.class);
        return query.getResultList();
    }

    public List<Lesson> getLessons(Professor professor) {
        Long professorId = professor.getProfessorId();
        TypedQuery<Lesson> query = entityManager.createQuery(
                "SELECT l FROM Lesson l WHERE l.professor.professorId = :id", Lesson.class);
        query.setParameter("id", professorId);
        return query.getResultList();
    }




}