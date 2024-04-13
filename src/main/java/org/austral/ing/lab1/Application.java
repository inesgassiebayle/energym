package org.austral.ing.lab1;
import org.austral.ing.lab1.controller.*;

import org.austral.ing.lab1.model.Room;
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
        final RoomController roomController = new RoomController(entityManager);
        final LessonController lessonController = new LessonController(entityManager);
        final AuthenticationController authenticationController = new AuthenticationController(entityManager);


        Spark.port(3333);

        before((req, resp) -> {
            resp.header("Access-Control-Allow-Origin", "*");
            resp.header("Access-Control-Allow-Headers", "*");
            resp.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, PATCH, OPTIONS");
        });

        options("/*", (req, resp) -> {
            resp.status(200);
            return "ok";
        });

        Spark.post("/user/professor-signup", userController::professorSignup);
        Spark.post("/user/student-signup", userController::studentSignup);
        Spark.post("/user/administrator-signup", userController::administratorSignup);
        Spark.post("/user/login", authenticationController::createAuthentication);
        Spark.post("/user/logout", authenticationController::deleteAuthentication);
        Spark.get("/user/verify", authenticationController::getCurrentUser);


        Spark.post("/activity/add", activityController::addActivity);
        Spark.post("/activity/delete", activityController::deleteActivity);
        Spark.get("/activity/get", activityController::getActivities);

        Spark.post("/room/create", roomController::addRoom);
        Spark.delete("/room/:name/delete", roomController::deleteRoom);
        Spark.get("/room/get", roomController::getRooms);
        //Spark.patch("/room", roomController::getRooms);
        Spark.get("/room/:name/getActivities", roomController::getRoomActivities);
        Spark.get("/room/:name/getCapacity", roomController::getRoomCapacity);
        Spark.patch("room/modify", roomController::modifyRoom);

        Spark.post("/lesson/addSingle", lessonController:: addSingleLesson);
        Spark.post("/lesson/addConcurrent", lessonController:: addConcurrentLessons);


    }
}
