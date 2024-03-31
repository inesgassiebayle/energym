package org.austral.ing.lab1;

import com.google.gson.Gson;
import org.austral.ing.lab1.model.User;
import spark.Request;
import spark.Response;
import spark.Spark;
import com.google.common.base.Strings;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class Application {
    private static final Gson gson = new Gson();
    public static void main(String[] args) {
        final EntityManagerFactory factory = Persistence.createEntityManagerFactory("energym");
        final EntityManager entityManager = factory.createEntityManager();

        final Operations operations = new Operations(entityManager);

        Spark.port(3333);

        Spark.post("/user/studentSignup", operations::studentSignup);
        Spark.get("/user/login", operations::login);
        Spark.get("/user/:username", operations::getUser);
    }

    private static String capitalized(String name) {
        return Strings.isNullOrEmpty(name) ? name : name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }



}
