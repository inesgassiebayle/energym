package org.austral.ing.lab1;

import org.austral.ing.lab1.model.Student;
import javax.persistence.EntityManager;

public class Students {
    private final EntityManager entityManager;

    public Students(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void persist(Student student) {
        entityManager.getTransaction().begin();
        entityManager.persist(student);
        entityManager.getTransaction().commit();
    }

    public void delete(Student student){
        entityManager.getTransaction().begin();
        entityManager.remove(student);
        entityManager.getTransaction().commit();
    }

}
