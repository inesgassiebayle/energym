package org.austral.ing.lab1.model;

import javax.persistence.*;

@Entity
public class Administrator{
    @Id
    @GeneratedValue(generator = "userGen", strategy = GenerationType.SEQUENCE)
    private Long administratorId;

    @OneToOne
    @JoinColumn(name = "userId")
    private User userId;
}
