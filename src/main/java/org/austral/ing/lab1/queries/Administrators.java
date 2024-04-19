package org.austral.ing.lab1.queries;

import org.austral.ing.lab1.model.*;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.austral.ing.lab1.EntityManagerController.entityManager;

public class Administrators {

    public Administrators() {

    }

    public void persist(Administrator administrator) {
        entityManager().getTransaction().begin();
        entityManager().persist(administrator);
        entityManager().getTransaction().commit();
    }

    public Administrator findAdministratorByUsername(String username) {
        Users users = new Users();

        User user = users.findUserByUsername(username);

        // Comprobar si el usuario existe
        if (user == null) {
            return null;
        }

        if(!user.getType().equals(UserType.ADMINISTRATOR)){
            return null;
        }

        TypedQuery<Administrator> query = entityManager().createQuery(
                "SELECT a FROM Administrator a WHERE a.user.id = :userId", Administrator.class);
        query.setParameter("userId", user.getId());

        if (query.getResultList().isEmpty()) {
            return null;
        }

        return query.getSingleResult();
    }

    public void delete(Administrator administrator){
        entityManager().getTransaction().begin();
        entityManager().remove(administrator);
        entityManager().getTransaction().commit();
    }


}
