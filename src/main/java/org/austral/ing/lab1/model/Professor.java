package org.austral.ing.lab1.model;

import javax.persistence.*;

@Entity
public class Professor {
    @Id
    @GeneratedValue(generator = "userGen", strategy = GenerationType.SEQUENCE)
    private Long professorId;

    @OneToOne
    @JoinColumn(name = "userId")
    private User userId;
}

