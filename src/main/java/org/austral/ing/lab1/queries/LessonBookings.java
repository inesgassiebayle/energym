package org.austral.ing.lab1.queries;

import org.austral.ing.lab1.model.BookedLesson;
import org.austral.ing.lab1.model.Lesson;
import org.austral.ing.lab1.model.Student;

import javax.persistence.TypedQuery;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
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

    public List<BookedLesson> findBookingByDateAndTimeAndStudent(LocalDate date, LocalTime time, Student student){
        List<BookedLesson> results = entityManager().createQuery("SELECT b FROM BookedLesson b WHERE b.lesson.startDate = :date AND b.student = :student AND b.lesson.time = :time", BookedLesson.class)
                .setParameter("date", date)
                .setParameter("time", time)
                .setParameter("student", student)
                .getResultList();
        return results.isEmpty() ? null : results;
    }
    public void persist(BookedLesson booking) {
        entityManager().getTransaction().begin();
        entityManager().persist(booking);
        entityManager().getTransaction().commit();
    }

    public List<BookedLesson> findConcurrentBookings(LocalTime time, String professorUsername, String name, String roomName, String activity, DayOfWeek dayOfWeek, Student student){
        List<Lesson> concurrentLessons = (new Lessons()).findConcurrentLessons(time, professorUsername, name, roomName, activity, dayOfWeek);
        List<BookedLesson> bookings = new ArrayList<>();
        for (Lesson lesson: concurrentLessons) {
             List<BookedLesson> booking = entityManager().createQuery("SELECT b FROM BookedLesson b WHERE b.lesson.id = :lesson AND b.student.user.id = :student AND b.state = :state", BookedLesson.class)
                    .setParameter("lesson", lesson.getId())
                    .setParameter("state", true)
                    .setParameter("student", student.getUser().getId())
                    .getResultList();
            if (!booking.isEmpty()) {
                bookings.add(booking.get(0));
            }
        }
        return bookings;
    }

    public void delete(BookedLesson booking){
        entityManager().getTransaction().begin();
        entityManager().remove(booking);
        entityManager().getTransaction().commit();
    }
}
