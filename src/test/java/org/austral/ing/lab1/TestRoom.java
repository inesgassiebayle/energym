package org.austral.ing.lab1;

import org.austral.ing.lab1.model.Room;
import org.austral.ing.lab1.queries.Rooms;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestRoom {
    private EntityManagerFactory factory;
    private EntityManager entityManager;
    private Rooms rooms;

    @Before
    public void setUp() {
        factory = Persistence.createEntityManagerFactory("energymdb");
        entityManager = factory.createEntityManager();
        rooms = new Rooms(entityManager);
    }

    @Test
    public void testFindRoomByName() {
        // Start transaction
        entityManager.getTransaction().begin();

        // Create and persist a test room
        Room room = new Room("Conference", 15);
        entityManager.persist(room);  // Use entityManager directly for clarity in test

        // Commit transaction to save the room
        entityManager.getTransaction().commit();

        // Start a new transaction for fetching the room
        entityManager.getTransaction().begin();

        // Act: Retrieve the room by name
        Room foundRoom = rooms.findRoomByName("Conference");

        // Assert: Check that the room is correctly retrieved
        assertNotNull("The room should exist", foundRoom);
        assertEquals("The name of the room should match", "Conference", foundRoom.getName());
        assertEquals("The capacity of the room should match", Integer.valueOf(15), foundRoom.getCapacity());

        // End transaction
        entityManager.getTransaction().commit();
    }

    @Test
    public void testFindRoomByName_NotFound() {
        // Start transaction
        entityManager.getTransaction().begin();

        // Act: Try to retrieve a room that does not exist
        Room foundRoom = rooms.findRoomByName("Nonexistent");

        // Assert: Check that no room is retrieved
        assertNull("The room should not exist", foundRoom);

        // End transaction
        entityManager.getTransaction().commit();
    }
}
