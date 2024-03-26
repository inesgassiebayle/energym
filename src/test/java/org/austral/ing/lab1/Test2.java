package org.austral.ing.lab1;

import org.austral.ing.lab1.model.Administrator;
import org.austral.ing.lab1.persistence.Database;
import org.junit.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class Test2 {
    private EntityManagerFactory sessionFactory;

    private final static Database database = new Database();

    @BeforeClass
    public static void beforeSuite() {
        database.startDBServer();
    }

    @AfterClass
    public static void afterSuite() {
        database.stopDBServer();
    }

    @Before
    public void beforeTest() {
        sessionFactory = Persistence.createEntityManagerFactory("energym");
    }

    @After
    public void close() {
        sessionFactory.close();
    }

    @Test
    public void persistAUser() {
        final EntityManager entityManager = sessionFactory.createEntityManager();
        final EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        //User user = new Administrator("Ines", "Gassiebayle", "inegassiebayle@gmail.com", "inegassiebayle", "hola123456");
        //entityManager.persist(user);

        entityManager.close();
    }
}

