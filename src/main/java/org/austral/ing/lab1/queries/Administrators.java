package org.austral.ing.lab1.queries;

import org.austral.ing.lab1.model.*;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

public class Administrators {
    private final EntityManager entityManager;

    public Administrators(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void persist(Administrator administrator) {
        entityManager.getTransaction().begin();
        entityManager.persist(administrator);
        entityManager.getTransaction().commit();
    }

    public Administrator findAdministratorByUsername(String username) {
        Users users = new Users(entityManager);

        User user = users.findUserByUsername(username);

        // Comprobar si el usuario existe
        if (user == null) {
            return null;
        }

        if(!user.getType().equals(UserType.ADMINISTRATOR)){
            return null;
        }

        TypedQuery<Administrator> query = entityManager.createQuery(
                "SELECT a FROM Administrator a WHERE a.user.id = :userId", Administrator.class);
        query.setParameter("userId", user.getId());

        if (query.getResultList().isEmpty()) {
            return null;
        }

        return query.getSingleResult();
    }

    public void delete(Administrator administrator){
        entityManager.getTransaction().begin();
        entityManager.remove(administrator);
        entityManager.getTransaction().commit();
    }


}
