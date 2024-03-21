package org.austral.ing.lab1.model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class Membership {
    @Id
    @GeneratedValue(generator = "userGen", strategy = GenerationType.SEQUENCE)
    private Long classId;

    @Column()
    private LocalDate expiration;

    @Column
    private String cardNumber;

    @Column String code;

    @OneToOne
    private Student student;

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Membership(LocalDate expiration, String cardNumber, String code){
        this.expiration = expiration;
        this.cardNumber = cardNumber;
        this.code = code;
    }

    public Membership(){

    }
}
