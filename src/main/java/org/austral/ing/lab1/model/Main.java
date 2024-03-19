package org.austral.ing.lab1.model;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class Main {

    public static void main(String[] args) {
        final EntityManagerFactory factory = Persistence.createEntityManagerFactory("energym");
        final EntityManager entityManager = factory.createEntityManager();

        users(entityManager);
        roomsActivities(entityManager);


        entityManager.close();
    }

    private static void users(EntityManager entityManager){
        Administrator user = new Administrator("Ines", "Gassiebayle", "inegassiebayle@gmail.com", "inegassiebayle", "hola123456");
        entityManager.getTransaction().begin();
        entityManager.persist(user);
        entityManager.getTransaction().commit();

        User user2 = new User("Luz", "Laura", "luzlaura@gmail.com", "luzlaura", "s1s12345");
        entityManager.getTransaction().begin();
        entityManager.persist(user2);
        entityManager.getTransaction().commit();

        Professor user3 = new Professor("Clara", "Lopez", "mclaralopezz@gmail.com", "claralopez", "micumpleaños12");
        entityManager.getTransaction().begin();
        entityManager.persist(user3);
        entityManager.getTransaction().commit();
    }
    private static void roomsActivities(EntityManager entityManager){
        Room room = new Room("Pileta", 20);
        entityManager.getTransaction().begin();
        entityManager.persist(room);
        entityManager.getTransaction().commit();

        Activity activity = new Activity("Natación");
        entityManager.getTransaction().begin();
        entityManager.persist(activity);
        entityManager.getTransaction().commit();
    }



}
