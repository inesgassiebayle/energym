package org.austral.ing.lab1;
import org.austral.ing.lab1.controller.*;

import org.austral.ing.lab1.model.Room;
import spark.Spark;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import static org.austral.ing.lab1.EntityManagerController.closeEntityManager;
import static org.austral.ing.lab1.EntityManagerController.setFactory;
import static spark.Spark.*;

public class Application {
    public static void main(String[] args) {

        final EntityManagerFactory factory = Persistence.createEntityManagerFactory("energym");
        setFactory(factory);
        final UserController userController = new UserController();
        final ActivityController activityController = new ActivityController();
        final RoomController roomController = new RoomController();
        final LessonController lessonController = new LessonController();
        final ProfessorController professorController = new ProfessorController();
        final AuthenticationController authenticationController = new AuthenticationController();
        final ReviewController reviewController = new ReviewController();
        final LessonController2 lessonController2 = new LessonController2();

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
        Spark.delete("/user/:username/delete", userController::deleteUser);
        Spark.delete("/user/delete", authenticationController::deleteAccount);

        Spark.post("/activity/add", activityController::addActivity);
        Spark.post("/activity/delete", activityController::deleteActivity);
        Spark.get("/activity/get", activityController::getActivities);

        Spark.post("/room/create", roomController::addRoom);
        Spark.delete("/room/:name/delete", roomController::deleteRoom);
        Spark.get("/room/get", roomController::getRooms);
        Spark.patch("/room", roomController::getRooms);
        Spark.get("/room/:name/getActivities", roomController::getRoomActivities);
        Spark.get("/room/:name/getCapacity", roomController::getRoomCapacity);
        Spark.patch("room/modify", roomController::modifyRoom);

        Spark.post("/lesson/addSingle", lessonController:: addSingleLesson);
        Spark.post("/lesson/addConcurrent", lessonController:: addConcurrentLessons);
        Spark.post("/lesson/deleteLesson", lessonController:: deleteLesson);
        Spark.get("/lesson/:date/getLessons", lessonController::getLessonsByDate);
        Spark.delete("/lesson/delete", lessonController::deleteLesson);

        Spark.get("/professor/get", professorController::getProfessors);
        Spark.post("/professor/getLessons", professorController::getLessons);
        Spark.get("/professor/:username/fullname", professorController::getFullname);
        Spark.post("/lesson/reviews", professorController::getLessonReviews);

        Spark.post("/lesson/get", lessonController2::getLesson);

        Spark.post("/review/create", reviewController::createReview);

        after((request, response) -> closeEntityManager());

    }
}
