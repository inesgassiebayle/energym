package org.austral.ing.lab1;

import org.austral.ing.lab1.model.Room;
import org.austral.ing.lab1.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

public class Rooms {
    private EntityManager entityManager;

    public Rooms(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Room findRoomById(Long id) {
        return entityManager.find(Room.class, id);
    }

    public Room findRoomByName(String name) {
        TypedQuery<Room> query = entityManager.createQuery("SELECT r " +
                "FROM Room r " +
                "WHERE r.name LIKE :name", Room.class);
        query.setParameter("name", name);
        return query.getSingleResult();
    }

    public List<Room> findAllRooms() {
        TypedQuery<Room> query = entityManager.createQuery("SELECT r FROM Room r", Room.class);
        return query.getResultList();
    }
    public Room persist(Room room) {
        entityManager.getTransaction().begin();
        entityManager.persist(room);
        entityManager.getTransaction().commit();
        return room;
    }

    public void delete(Room room) {
        entityManager.getTransaction().begin();
        entityManager.remove(room);
        entityManager.getTransaction().commit();
    }

    public void update(Room room) {
        entityManager.getTransaction().begin();
        entityManager.merge(room);
        entityManager.getTransaction().commit();
    }

    public List<Room> findRoomsByCapacity(int capacity) {
        TypedQuery<Room> query = entityManager.createQuery("SELECT r " +
                "FROM Room r " +
                "WHERE r.capacity = :capacity", Room.class);
        query.setParameter("capacity", capacity);
        return query.getResultList();
    }
}


