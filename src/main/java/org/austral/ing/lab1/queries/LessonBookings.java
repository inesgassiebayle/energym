package org.austral.ing.lab1.queries;

import org.austral.ing.lab1.model.BookedLesson;
import org.austral.ing.lab1.model.Lesson;
import org.austral.ing.lab1.model.Student;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.austral.ing.lab1.EntityManagerController.entityManager;

public class LessonBookings {
    public BookedLesson findBookingByLessonAndStudent(Lesson lesson, Student student){
        List<BookedLesson> results = entityManager().createQuery("SELECT b FROM BookedLesson b WHERE b.lesson = :lesson AND b.student = :student", BookedLesson.class)
                .setParameter("lesson", lesson)
                .setParameter("student", student)
                .getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    public BookedLesson findBookingByDateAndTimeAndStudent(LocalDate date, LocalTime time, Student student){
        List<BookedLesson> results = entityManager().createQuery("SELECT b FROM BookedLesson b WHERE b.lesson.startDate = :date AND b.student = :student AND b.lesson.time = :time", BookedLesson.class)
                .setParameter("date", date)
                .setParameter("time", time)
                .setParameter("student", student)
                .getResultList();
        return results.isEmpty() ? null : results.get(0);
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
