package org.austral.ing.lab1;

import org.austral.ing.lab1.model.*;
import org.austral.ing.lab1.queries.*;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.austral.ing.lab1.EntityManagerController.entityManager;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestFindLesson {
    @Test
    public void testFindLessonByNameDateAndTime() {
        Lessons lessons = new Lessons();
        // Start a transaction to create a test context
        entityManager().getTransaction().begin();

        // Create and persist a lesson
        Lesson lesson = new Lesson("Test Lesson", LocalTime.of(10, 0), LocalDate.now());
        entityManager().persist(lesson);

        // Commit the transaction to ensure the data is available in the database
        entityManager().getTransaction().commit();

        // Use the method to find the lesson
        Lesson foundLesson = lessons.findLessonByNameDateAndTime("Test Lesson", LocalDate.now(), LocalTime.of(10, 0));

        // Check that the lesson was found and the data matches
        assertNotNull(foundLesson);
        assertEquals("Test Lesson", foundLesson.getName());
        assertEquals(LocalDate.now(), foundLesson.getStartDate());
        assertEquals(LocalTime.of(10, 0), foundLesson.getTime());

        // Clean up the test data
        entityManager().getTransaction().begin();
        entityManager().remove(foundLesson);
        entityManager().getTransaction().commit();
    }
//    public void testFindLessonsByProfessorAfterDate() {
//        Lessons lessons = new Lessons();
//        Professors professors = new Professors();
//
//        Professor professor = professors.findProfessorByUsername("prof1");
//
//        // Method under test
//        List<Lesson> foundLessons = lessons.findLessonsByProfessorAfterDate(professor, LocalDate.now());
//
//        // Assertions
//        assertNotNull(foundLessons);
//        assertEquals(4, foundLessons.size());
//
//    }
}
