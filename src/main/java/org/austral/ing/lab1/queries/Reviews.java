package org.austral.ing.lab1.queries;

import org.austral.ing.lab1.model.Lesson;
import org.austral.ing.lab1.model.Review;
import org.austral.ing.lab1.model.Student;
import org.austral.ing.lab1.model.User;

import javax.persistence.EntityManager;
import java.util.List;

import static org.austral.ing.lab1.EntityManagerController.entityManager;

public class Reviews {

    public Reviews() {

    }

    public void createReview(Review review) {
        entityManager().getTransaction().begin();
        entityManager().persist(review);
        entityManager().getTransaction().commit();
    }

    public Review getReviewById(Long reviewId) {
        return entityManager().find(Review.class, reviewId);
    }

    public void updateReview(Review review) {
        entityManager().getTransaction().begin();
        entityManager().merge(review);
        entityManager().getTransaction().commit();
    }

    public void deleteReview(Long reviewId) {
        Review review = getReviewById(reviewId);
        if (review != null) {
            entityManager().getTransaction().begin();
            entityManager().remove(review);
            entityManager().getTransaction().commit();
        }
    }

    public List<Review> getAllReviews() {
        return entityManager().createQuery("SELECT r FROM Review r", Review.class).getResultList();
    }

    public List<Review> getReviewsByStudent(Long studentId) {
        return entityManager().createQuery("SELECT r FROM Review r WHERE r.student.studentId = :studentId", Review.class)
                .setParameter("studentId", studentId)
                .getResultList();
    }

    public List<Review> getReviewsByLesson(Long classId) {
        return entityManager().createQuery("SELECT r FROM Review r WHERE r.lesson.classId = :classId", Review.class)
                .setParameter("classId", classId)
                .getResultList();
    }

    public Review findReviewByLessonAndStudent(Lesson lesson, Student student) {
        List<Review> reviews = entityManager().createQuery("SELECT r FROM Review r WHERE r.lesson = :lesson AND r.student = :student AND r.state = :state", Review.class)
                .setParameter("student", student)
                .setParameter("lesson", lesson)
                .setParameter("state", true)
                .getResultList();
        if (reviews.isEmpty()) {
            return null;
        }
        return reviews.get(0);
    }
    public Integer sumOfRatingsByLesson(Lesson lesson){
        List<Review> reviews = entityManager().createQuery("SELECT r FROM Review r WHERE r.lesson = :lesson", Review.class)
                .setParameter("lesson", lesson)
                .getResultList();
        Integer total = 0;
        for (Review review: reviews){
            total += review.getRating();
        }
        return total;
    }

    public void persist(Review review) {
        entityManager().getTransaction().begin();
        entityManager().persist(review);
        entityManager().getTransaction().commit();
    }

    public void delete(Review review){
        entityManager().getTransaction().begin();
        entityManager().remove(review);
        entityManager().getTransaction().commit();
    }

}
