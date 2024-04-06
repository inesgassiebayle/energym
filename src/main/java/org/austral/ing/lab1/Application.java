package org.austral.ing.lab1;
import org.austral.ing.lab1.controller.ActivityController;
import org.austral.ing.lab1.controller.UserController;
import spark.Spark;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import static spark.Spark.before;
import static spark.Spark.options;

public class Application {
    public static void main(String[] args) {

        final EntityManagerFactory factory = Persistence.createEntityManagerFactory("energym");
        final EntityManager entityManager = factory.createEntityManager();
        final UserController userController = new UserController(entityManager);
        final ActivityController activityController = new ActivityController(entityManager);


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

        Spark.post("/user/student-signup", userController::studentSignup);
        Spark.get("/user/login", userController::login);

        Spark.post("/activity/add", activityController::addActivity);
        Spark.post("/activity/delete", activityController::deleteActivity);
        Spark.get("/activity/get", activityController::getActivities);

    }
}
