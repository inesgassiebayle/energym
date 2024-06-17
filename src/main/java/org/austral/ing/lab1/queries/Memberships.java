package org.austral.ing.lab1.queries;

import org.austral.ing.lab1.model.Membership;
import org.austral.ing.lab1.model.Professor;
import org.austral.ing.lab1.model.Student;

import javax.persistence.TypedQuery;
import java.util.Set;

import static org.austral.ing.lab1.EntityManagerController.entityManager;

public class Memberships {
  public Membership findMembershipById(Long id) {
    return entityManager().find(Membership.class, id);
  }

  public Membership findMembershipByStudent(Student student) {
     TypedQuery<Membership> membershipSet = entityManager().createQuery("SELECT m FROM Membership m WHERE m.student = :student", Membership.class)
      .setParameter("student", student);
     if (membershipSet.getResultList().isEmpty()) {
       return null;
     }
      return membershipSet.getSingleResult();
  }

  public void persist(Membership membership) {
    entityManager().getTransaction().begin();
    entityManager().persist(membership);
    entityManager().getTransaction().commit();
  }



}
