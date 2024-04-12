package org.austral.ing.lab1.queries;

import org.austral.ing.lab1.model.Professor;
import org.austral.ing.lab1.model.Student;
import org.austral.ing.lab1.model.User;
import org.austral.ing.lab1.model.UserType;

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


}