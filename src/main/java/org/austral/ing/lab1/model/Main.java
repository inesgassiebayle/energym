package org.austral.ing.lab1.model;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class Main {

    public static void main(String[] args) {
        final EntityManagerFactory factory = Persistence.createEntityManagerFactory("energym");
        final EntityManager entityManager = factory.createEntityManager();

        sample1(entityManager);

        entityManager.close();
    }

    private static void sample1(EntityManager entityManager){
        User user = new User("Ines", "Gassiebayle", "inegassiebayle@gmail.com", "inegassiebayle", "123456789");
        entityManager.getTransaction().begin();
        entityManager.persist(user);
        entityManager.getTransaction().commit();
    }

}
