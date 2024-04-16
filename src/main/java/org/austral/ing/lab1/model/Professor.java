package org.austral.ing.lab1.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Professor {

    @Id
    @GeneratedValue(generator = "userGen", strategy = GenerationType.SEQUENCE)
    private Long professorId;
    @OneToMany(mappedBy = "professor", cascade = CascadeType.ALL)
    private Set<Lesson> classes = new HashSet<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "userId", referencedColumnName = "userId")
    private User user;

    public void setUser(User user){
        this.user=user;
    }
    public Long getProfessorId(){
        return professorId;
    }
    public Set<Lesson> getClasses() {
        return classes;
    }

    public User getUser(){ return user; }

    public void addClass(Lesson lesson) {
        classes.add(lesson);
        lesson.setProfessor(this);
    }

    public void removeClass(Lesson lesson) {
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

