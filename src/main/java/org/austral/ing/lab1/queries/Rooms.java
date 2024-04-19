package org.austral.ing.lab1.queries;
import org.austral.ing.lab1.model.Activity;
import org.austral.ing.lab1.model.Room;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

import static org.austral.ing.lab1.EntityManagerController.entityManager;

public class Rooms {

    public Rooms() {
    }

    public Room findRoomById(Long id) {
        return entityManager().find(Room.class, id);
    }

    public List<Room> findAllRooms() {
        TypedQuery<Room> query = entityManager().createQuery("SELECT r FROM Room r", Room.class);
        return query.getResultList();
    }

    public Room findRoomByName(String name) {
        TypedQuery<Room> query = entityManager().createQuery("SELECT r " +
                "FROM Room r " +
                "WHERE r.name LIKE :name", Room.class);
        query.setParameter("name", name);
        List<Room> rooms = query.getResultList();
        if (rooms.isEmpty()) {
            return null;
        }
        return rooms.get(0);
    }

    public List<Room> findRoomsByActivity(String activityName) {
        TypedQuery<Room> query = entityManager().createQuery(
                "SELECT r " +
                        "FROM Room r " +
                        "JOIN r.activities a " +  // Realizar un join con la relación de actividades
                        "WHERE a.name = :activityName", Room.class);  // Filtrar por el nombre de la actividad
        query.setParameter("activityName", activityName);
        return query.getResultList();
    }



    public void persist(Room room) {
        entityManager().getTransaction().begin();
        entityManager().persist(room);
        entityManager().getTransaction().commit();
    }

    public void delete(Room room){
        entityManager().getTransaction().begin();
        entityManager().remove(room);
        entityManager().getTransaction().commit();
    }
}
