package org.austral.ing.lab1;

import org.austral.ing.lab1.model.Professor;
import org.austral.ing.lab1.model.User;
import org.austral.ing.lab1.model.UserType;
import org.austral.ing.lab1.queries.Professors;
import org.austral.ing.lab1.queries.Users;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TestProfessor {
    final EntityManagerFactory factory = Persistence.createEntityManagerFactory("energym");
    final EntityManager entityManager = factory.createEntityManager();
    final Professors professors = new Professors(entityManager);
    final Users users = new Users(entityManager);

    @Test
    public void testFindProfessorByUsername_NonExistingUser() {
        // Inicia una transacción para crear un contexto de prueba
        entityManager.getTransaction().begin();

        // Crear y persistir un usuario
        User user = new User("John", "Doe", "john.doe@example.com", "johndoe", "pass1234");
        entityManager.persist(user);

        // Crear y persistir un profesor asociado a este usuario
        Professor professor = new Professor();
        professor.setUser(user);
        user.setType(UserType.PROFESSOR);
        entityManager.persist(user);

        entityManager.persist(professor);

        // Confirma la transacción para asegurar que los datos están disponibles en la base de datos
        entityManager.getTransaction().commit();

        // Buscar el profesor por el nombre de usuario
        Professor foundProfessor = professors.findProfessorByUsername("johndoe");

        // Comprobar que el profesor fue encontrado
        assertEquals("John", foundProfessor.getUser().getFirstName());

        User user2 = users.findUserByUsername("johndoe");

        assertEquals("John", user2.getFirstName());
        assertEquals("Doe", user2.getLastName());

        professors.delete(professor);

        User user3 = users.findUserByUsername("johndoe");
        assertNull(user3);
        Professor professor1 = professors.findProfessorByUsername("johndoe");
        assertNull(professor1);
    }
}
