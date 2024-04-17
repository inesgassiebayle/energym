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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TestLessons {
    final EntityManagerFactory factory = Persistence.createEntityManagerFactory("energymdb");
    final EntityManager entityManager = factory.createEntityManager();
    final Lessons lessons = new Lessons(entityManager);

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
}



