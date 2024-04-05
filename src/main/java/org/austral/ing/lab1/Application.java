package org.austral.ing.lab1;

import com.google.gson.Gson;
import org.austral.ing.lab1.controller.UserController;
import spark.Spark;
import com.google.common.base.Strings;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import static spark.Spark.before;
import static spark.Spark.options;

public class Application {
    private static final Gson gson = new Gson();
    public static void main(String[] args) {
        final EntityManagerFactory factory = Persistence.createEntityManagerFactory("energym");
        final EntityManager entityManager = factory.createEntityManager();

        final UserController userController = new UserController(entityManager);

        Spark.port(3333);

        options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }

            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }

            return "OK";
        });

        before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));

        Spark.post("/user/studentSignup", userController::studentSignup);
        Spark.get("/user/login", userController::login);
    }

    private static String capitalized(String name) {
        return Strings.isNullOrEmpty(name) ? name : name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }
}
