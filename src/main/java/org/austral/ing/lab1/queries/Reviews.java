package org.austral.ing.lab1.queries;

import org.austral.ing.lab1.model.Review;
import org.austral.ing.lab1.model.User;

import javax.persistence.EntityManager;
import java.util.List;

public class Reviews {
    private final EntityManager entityManager;

    public Reviews(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void createReview(Review review) {
        entityManager.getTransaction().begin();
        entityManager.persist(review);
        entityManager.getTransaction().commit();
    }

    public Review getReviewById(Long reviewId) {
        return entityManager.find(Review.class, reviewId);
    }

    public void updateReview(Review review) {
        entityManager.getTransaction().begin();
        entityManager.merge(review);
        entityManager.getTransaction().commit();
    }

    public void deleteReview(Long reviewId) {
        Review review = getReviewById(reviewId);
        if (review != null) {
            entityManager.getTransaction().begin();
            entityManager.remove(review);
            entityManager.getTransaction().commit();
        }
    }

    public List<Review> getAllReviews() {
        return entityManager.createQuery("SELECT r FROM Review r", Review.class).getResultList();
    }

    public List<Review> getReviewsByStudent(Long studentId) {
        return entityManager.createQuery("SELECT r FROM Review r WHERE r.student.studentId = :studentId", Review.class)
                .setParameter("studentId", studentId)
                .getResultList();
    }

    public List<Review> getReviewsByLesson(Long classId) {
        return entityManager.createQuery("SELECT r FROM Review r WHERE r.lesson.classId = :classId", Review.class)
                .setParameter("classId", classId)
                .getResultList();
    }

    public void persist(Review review) {
        entityManager.getTransaction().begin();
        entityManager.persist(review);
        entityManager.getTransaction().commit();
    }

    public void delete(Review review){
        entityManager.getTransaction().begin();
        entityManager.remove(review);
        entityManager.getTransaction().commit();
    }

}
