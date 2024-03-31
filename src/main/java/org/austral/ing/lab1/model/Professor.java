package org.austral.ing.lab1.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Professor {

    @Id
    @GeneratedValue(generator = "userGen", strategy = GenerationType.SEQUENCE)
    private Long professorid;
    @OneToMany(mappedBy = "professor", cascade = CascadeType.ALL)
    private Set<Class> classes = new HashSet<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "professorid", referencedColumnName = "professorid")
    private User user;

    public void setUser(User user){
        this.user=user;
    }

    public Set<Class> getClasses() {
        return classes;
    }

    public void addClass(Class lesson) {
        classes.add(lesson);
        lesson.setProfessor(this);
    }

    public void removeClass(Class lesson) {
        classes.remove(lesson);
        lesson.setProfessor(null);
    }

    public Professor() {
        super();
    }

}


//@OneToMany(mappedBy = "professor", cascade = CascadeType.ALL)
//private Set<Class> classes = new HashSet<>();
//
//public Set<Class> getClasses() {
//    return classes;
//}
//
//public void addClass(Class lesson) {
//    classes.add(lesson);
//    lesson.setProfessor(this);
//}
//
//public void removeClass(Class lesson) {
//    classes.remove(lesson);
//    lesson.setProfessor(null);
//}
//
//public Professor() {
//    super();
//}
//
//public Professor(String firstName, String lastName, String email, String username, String password) {
//    super(firstName, lastName, email, username, password);
//}

