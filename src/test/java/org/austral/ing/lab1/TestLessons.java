package org.austral.ing.lab1;

import org.austral.ing.lab1.model.Lesson;
import org.austral.ing.lab1.model.Professor;
import org.austral.ing.lab1.model.User;
import org.austral.ing.lab1.model.UserType;
import org.austral.ing.lab1.queries.Lessons;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.Assert.*;

public class TestLessons {
    final EntityManagerFactory factory = Persistence.createEntityManagerFactory("energymdb");
    final EntityManager entityManager = factory.createEntityManager();
    final Lessons lessons = new Lessons();

    @Test
    public void findLessonbyNameAndDate(){
        // Inicia una transacción para crear un contexto de prueba
        entityManager.getTransaction().begin();

        // Crear y persistir un usuario
        Lesson lessson = new Lesson("pilates1", LocalTime.of(12,30), LocalDate.now());
        entityManager.persist(lessson);

        // Confirma la transacción para asegurar que los datos están disponibles en la base de datos
        entityManager.getTransaction().commit();

        // Buscar el profesor por el nombre de usuario
        Lesson foundLesson = lessons.findLessonByNameAndDate("pilates1", LocalDate.now());

        // Comprobar que el profesor fue encontrado
        assertEquals(foundLesson.getName() , "pilates1" );
        assertEquals(foundLesson.getStartDate() ,LocalDate.now()  );
        assertEquals(foundLesson.getTime(), LocalTime.of(12,30));


        lessons.delete(lessson);


    }

    @Test
    public void findLessonsByProfessorAndTime(){
        entityManager.getTransaction().begin();

        User user = new User("clara", "lopez", "email@example.com", "Profesora1", "clara100");
        user.setType(UserType.PROFESSOR);
        entityManager.persist(user);

        Professor professor = new Professor();
        professor.setUser(user);
        entityManager.persist(professor);

        Lesson lesson1 = new Lesson("Morning Yoga", LocalTime.of(9, 0), LocalDate.now());
        lesson1.setProfessor(professor);
        entityManager.persist(lesson1);

        entityManager.getTransaction().commit();

        List<Lesson> foundLessons = lessons.findLessonsByProfessorDateAndTime("professor1", LocalTime.of(9, 0), LocalDate.now());

        assertFalse(foundLessons.isEmpty());
        assertEquals(1, foundLessons.size());
        assertEquals("Morning Yoga", foundLessons.get(0).getName());
        assertEquals(LocalDate.now(), foundLessons.get(0).getStartDate());
        assertEquals(LocalTime.of(9, 0), foundLessons.get(0).getTime());


        entityManager.getTransaction().begin();
        entityManager.createQuery("DELETE FROM Lesson").executeUpdate();
        entityManager.createQuery("DELETE FROM Professor").executeUpdate();
        entityManager.createQuery("DELETE FROM User").executeUpdate();
        entityManager.getTransaction().commit();
    }
    @Test
    public void findLessonsByProfessorAndTimeVacia(){
        entityManager.getTransaction().begin();

        User user = new User("clara", "lopez", "email@example.com", "Profesora1", "clara100");
        user.setType(UserType.PROFESSOR);
        entityManager.persist(user);

        Professor professor = new Professor();
        professor.setUser(user);
        entityManager.persist(professor);


        List<Lesson> foundLessons = lessons.findLessonsByProfessorDateAndTime("professor1", LocalTime.of(9, 0), LocalDate.now());

        assertTrue(foundLessons.isEmpty());
        assertEquals(0, foundLessons.size());


    }

}



