package org.austral.ing.lab1.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Student{

    @Id
    @GeneratedValue(generator = "userGen", strategy = GenerationType.SEQUENCE)
    private Long studentId;
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private Set<BookedLesson> bookings = new HashSet<>();

    public Set<BookedLesson> getBookings() {
        return bookings;
    }

    public void addBooking(BookedLesson booking) {
        this.bookings.add(booking);
    }

    @OneToMany(mappedBy = "student")
    private Set<Review> reviews = new HashSet<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "userId", referencedColumnName = "userId")
    private User user;

    public void setUser(User user){
        this.user=user;
    }
    public User getUser(){return user;}

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
