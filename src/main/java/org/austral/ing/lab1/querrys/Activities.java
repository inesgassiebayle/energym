package org.austral.ing.lab1.querrys;

import org.austral.ing.lab1.model.Activity;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

public class Activities {
    private final EntityManager entityManager;

    public Activities(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Activity findActivityById(Long id) {
        return entityManager.find(Activity.class, id);
    }

    public List<Activity> findAllActivities() {
        TypedQuery<Activity> query = entityManager.createQuery("SELECT a FROM Activity a", Activity.class);
        return query.getResultList();
    }

    public Activity findActivityByName(String name) {
        TypedQuery<Activity> query = entityManager.createQuery("SELECT a " +
                "FROM Activity a " +
                "WHERE a.name LIKE :name", Activity.class);
        query.setParameter("name", name);
        List<Activity> activities = query.getResultList();
        if (activities.isEmpty()) {
            return null;
        }
        return activities.get(0);
    }

    public void persist(Activity activity) {
        entityManager.getTransaction().begin();
        entityManager.persist(activity);
        entityManager.getTransaction().commit();
    }

    public void delete(Activity activity){
        entityManager.getTransaction().begin();
        entityManager.remove(activity);
        entityManager.getTransaction().commit();
    }
}
