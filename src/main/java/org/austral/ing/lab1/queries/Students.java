package org.austral.ing.lab1.queries;
import org.austral.ing.lab1.model.Professor;
import org.austral.ing.lab1.model.Student;
import org.austral.ing.lab1.model.User;
import org.austral.ing.lab1.model.UserType;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

import static org.austral.ing.lab1.EntityManagerController.entityManager;

public class Students {
    public Students(){}

    public Student findStudentById(Long id) {
        return entityManager().find(Student.class, id);
    }

    public List<Student> findAllStudents() {
        TypedQuery<Student> query = entityManager().createQuery("SELECT s FROM Student s", Student.class);
        return query.getResultList();
    }


    public void persist(Student student) {
        entityManager().getTransaction().begin();
        entityManager().persist(student);
        entityManager().getTransaction().commit();
    }

    public void delete(Student student){
        entityManager().getTransaction().begin();
        entityManager().remove(student);
        entityManager().getTransaction().commit();
    }

    public Student findStudentByUsername(String username) {
        Users users = new Users();

        User user = users.findUserByUsername(username);

        // Comprobar si el usuario existe
        if (user == null) {
            return null;
        }

        if(!user.getType().equals(UserType.STUDENT)){
            return null;
        }

        TypedQuery<Student> query = entityManager().createQuery(
                "SELECT s FROM Student s WHERE s.user.id = :userId", Student.class);
        query.setParameter("userId", user.getId());

        if (query.getResultList().isEmpty()) {
            return null;
        }
        return query.getSingleResult();
    }
}
