package org.austral.ing.lab1.queries;

import org.austral.ing.lab1.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

import static org.austral.ing.lab1.EntityManagerController.entityManager;

public class Users {

    public Users() {

    }

    public User findUserById(Long id) {
        return entityManager().find(User.class, id);
    }

    public User findUserByEmail(String mail) {
        return entityManager().createQuery("SELECT u FROM User u WHERE u.email LIKE :mail", User.class)
                .setParameter("mail", mail)
                .getSingleResult();
    }

    public List<User> findAllUsers() {
        TypedQuery<User> query = entityManager().createQuery("SELECT u FROM User u", User.class);
        return query.getResultList();
    }

    public User login(String username, String password) {
        TypedQuery<User> query = entityManager().createQuery("SELECT u " +
                "FROM User u " +
                "WHERE u.password LIKE :password AND u.username LIKE: username", User.class);
        query.setParameter("username", username);
        query.setParameter("password", password);
        return query.getSingleResult();
    }


    public User findUserByUsername(String username) {
        TypedQuery<User> query = entityManager().createQuery("SELECT u " +
                "FROM User u " +
                "WHERE u.username LIKE :username", User.class);
        query.setParameter("username", username);
        List<User> users = query.getResultList();
        if (users.isEmpty()) {
            return null;
        }
        return users.get(0);
    }

    public User findUserByUsernameOrEmail(String username, String email) {
        TypedQuery<User> query = entityManager().createQuery("SELECT u " +
                "FROM User u " +
                "WHERE u.username LIKE :username OR u.email LIKE :email", User.class);
        query.setParameter("username", username);
        query.setParameter("email", email);
        List<User> users = query.getResultList();
        if (users.isEmpty()) {
            return null;
        }
        return users.get(0);
    }

    public void persist(User user) {
        entityManager().getTransaction().begin();
        entityManager().persist(user);
        entityManager().getTransaction().commit();
    }

    public void delete(User user){
        entityManager().getTransaction().begin();
        entityManager().remove(user);
        entityManager().getTransaction().commit();
    }
}
