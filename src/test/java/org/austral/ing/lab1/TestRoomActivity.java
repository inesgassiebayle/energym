package org.austral.ing.lab1;

import org.austral.ing.lab1.model.Activity;
import org.austral.ing.lab1.model.Room;
import org.austral.ing.lab1.queries.Activities;
import org.austral.ing.lab1.queries.Rooms;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class TestRoomActivity {
    final EntityManagerFactory factory = Persistence.createEntityManagerFactory("energym");
    final EntityManager entityManager = factory.createEntityManager();
    final Activities activities = new Activities(entityManager);
    final Rooms rooms = new Rooms(entityManager);

    @Test
    public void testRoomActivity(){
        // Creo activities
        Activity activity = new Activity("Pilates");
        activities.persist(activity);
        Activity activity1 = new Activity("Yoga");
        activities.persist(activity1);
        Activity activity2 = new Activity("Functional");
        activities.persist(activity2);

        // Creo un room
        Room room = new Room("Lounge", 10);
        room.setActivity(activity1);
        room.setActivity(activity);
        room.setActivity(activity2);

        rooms.persist(room);

        Room room2 = rooms.findRoomByName("Lounge");

        room2.setName("Spa");
        rooms.persist(room2);
    }
}
