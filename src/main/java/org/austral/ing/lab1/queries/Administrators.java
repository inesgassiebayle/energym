package org.austral.ing.lab1.queries;

import org.austral.ing.lab1.model.Administrator;
import org.austral.ing.lab1.model.Student;

import javax.persistence.EntityManager;

public class Administrators {
    private final EntityManager entityManager;

    public Administrators(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    public void persist(Administrator administrator) {
        entityManager.getTransaction().begin();
        entityManager.persist(administrator);
        entityManager.getTransaction().commit();
    }

}
