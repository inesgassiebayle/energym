package org.austral.ing.lab1.model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class Membership {
    @Id
    @GeneratedValue(generator = "userGen", strategy = GenerationType.SEQUENCE)
    private Long membershipId;

    @Column()
    private LocalDate expiration;

    @OneToOne
    private Student student;

    public Student getStudent() {
        return student;
    }

    public void setExpiration(LocalDate expiration) {
        this.expiration = expiration;
    }

    public LocalDate getExpiration() {
        return expiration;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Membership(LocalDate expiration, Student student){
        this.expiration = expiration;
        this.student = student;
    }

    public Membership(){

    }
}
