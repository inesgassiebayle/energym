package org.austral.ing.lab1.queries;

import org.austral.ing.lab1.model.Professor;
import org.austral.ing.lab1.model.User;

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
        TypedQuery<Professor> query = entityManager.createQuery("SELECT p " +
                "FROM Professor p " +
                "WHERE p.user.username LIKE :username", Professor.class);
        query.setParameter("username", username);
        List<Professor> professors = query.getResultList();
        if (professors.isEmpty()) {
            return null;
        }
        return professors.get(0);
    }


}
