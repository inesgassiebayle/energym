package org.austral.ing.lab1.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Student extends User{
    @ManyToMany
    @JoinTable(
            name = "bookedClasses",
            joinColumns = @JoinColumn(name = "studentId"),
            inverseJoinColumns = @JoinColumn(name = "classId")
    )
    private Set<Class> classes = new HashSet<>();

    public Set<Class> getClasses() {
        return classes;
    }

    public void setClasses(Set<Class> classes) {
        this.classes = classes;
    }

    @OneToMany(mappedBy = "student")
    private Set<Review> reviews = new HashSet<>();

    public Set<Review> getReviews() {
        return reviews;
    }

    public void setReviews(Set<Review> reviews) {
        this.reviews = reviews;
    }

    @OneToOne
    private Membership membership;

    public Membership getMembership() {
        return membership;
    }

    public void setMembership(Membership membership) {
        this.membership = membership;
    }
    public Student() {
        super();
    }
    public Student(String firstName, String lastName, String email, String username, String password) {
        super(firstName, lastName, email, username, password);
    }


}
