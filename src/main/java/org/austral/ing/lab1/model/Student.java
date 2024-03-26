package org.austral.ing.lab1.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Student{

    @Id
    @GeneratedValue(generator = "userGen", strategy = GenerationType.SEQUENCE)
    private Long studentId;
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

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "userId", referencedColumnName = "userId")
    private User user;

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
    public Student() {}

}
