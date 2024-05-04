package org.austral.ing.lab1.queries;

import org.austral.ing.lab1.model.BookedLesson;
import org.austral.ing.lab1.model.Lesson;

import static org.austral.ing.lab1.EntityManagerController.entityManager;

public class LessonBookings {
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
