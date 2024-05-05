package org.austral.ing.lab1.queries;

import org.austral.ing.lab1.model.BookedLesson;
import org.austral.ing.lab1.model.Lesson;
import org.austral.ing.lab1.model.Student;

import static org.austral.ing.lab1.EntityManagerController.entityManager;

public class LessonBookings {
    public BookedLesson findBookingByLessonAndStudent(Lesson lesson, Student student){
        return entityManager().createQuery("SELECT b FROM BookedLesson b WHERE b.lesson = :lesson AND b.student = :student", BookedLesson.class)
                .setParameter("lesson", lesson)
                .setParameter("student", student)
                .getSingleResult();
    }
    public void persist(BookedLesson booking) {
        entityManager().getTransaction().begin();
        entityManager().persist(booking);
        entityManager().getTransaction().commit();
    }

    public void delete(BookedLesson booking){
        entityManager().getTransaction().begin();
        entityManager().remove(booking);
        entityManager().getTransaction().commit();
    }
}
