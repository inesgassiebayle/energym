package org.austral.ing.lab1;
import org.austral.ing.lab1.controller.*;

import spark.Spark;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import static org.austral.ing.lab1.EntityManagerController.closeEntityManager;
import static org.austral.ing.lab1.EntityManagerController.setFactory;
import static spark.Spark.*;

public class Application {
    public static void main(String[] args) {

        final EntityManagerFactory factory = Persistence.createEntityManagerFactory("energymdb");
        setFactory(factory);
        final EmailSender emailSender = new EmailSender();
        final ReminderService reminderService = new ReminderService(emailSender);
        final ActivityController activityController = new ActivityController(emailSender, reminderService);
        final UserController userController = new UserController(reminderService, emailSender);
        final ProfessorController professorController = new ProfessorController();
        final AuthenticationController authenticationController = new AuthenticationController();
        final ReviewController reviewController = new ReviewController();
        final LessonController lessonController = new LessonController(reminderService, emailSender);
        final StudentController studentController = new StudentController(reminderService, emailSender);
        final RoomController roomController = new RoomController(emailSender, reminderService);
        final InitialDataBase initialDataBase = new InitialDataBase();
        final MercadoPagoController mercadoPagoController = new MercadoPagoController();
        initialDataBase.createInitialDataBase();

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
        Spark.patch("/user/change-password", userController::changePassword);
        Spark.patch("/user/forgot-password", userController::sendChangePasswordEmail);

        Spark.post("/activity/add", activityController::addActivity);
        Spark.delete("/activity/:name/delete", activityController::deleteActivity);
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
        Spark.get("/professor/:username/fullname", professorController::getFullname);
        Spark.get("/lesson/reviews", lessonController::getLessonReviews);
        Spark.patch("/lesson/modify", lessonController::lessonModify);
        Spark.get("/lesson", lessonController::getLesson);
        Spark.delete("/lesson", lessonController::deleteLesson);
        Spark.get("/getLessonById/:id", lessonController::getLessonById);
        Spark.get("/lesson/students", lessonController::getStudents);
        Spark.post("lesson/assistance", lessonController::assistanceCheck);
        Spark.get("/lesson/activity", lessonController::getActivity);
        Spark.get("/lesson/reviewsByActivity", lessonController::getReviewsByActivity);
        Spark.get("/lessons", lessonController::getAllLessons);

        Spark.get("/professor/get", professorController::getProfessors);
        Spark.get("/professor/lessons", professorController::getLessons);
        Spark.get("/professor/getAllLessons/:username", professorController::getAllLessons);

        Spark.get("/professor/lessons2", professorController::getProfessorAverageRating);
        Spark.get("/professor/lessonsByUser", professorController::getLessonsOnlyUsername);

        Spark.post("/review", reviewController::createReview);
        Spark.get("/review", reviewController::getReview);
        Spark.patch("/review", reviewController::modifyReview);
        Spark.delete("/review", reviewController::deleteReview);


        Spark.get("/compare-date", lessonController::compareDate);
        Spark.get("/compare-date/:id", lessonController::compareDateSpecificClass);

        Spark.get("/compareInitialDate", lessonController::compareTodayDate);

        Spark.get("/student/membership", studentController::getAllMemberships);
        Spark.post("/student/book-lesson", studentController::bookClass);
        Spark.delete("/student/booking", studentController::deleteBooking);
        Spark.get("/student/classify-lessons", studentController::classifyLessons);
        Spark.get("/lesson/classify/:id/:username", studentController::classifyLesson);
        Spark.get("/lesson/concurrent", studentController::checkConcurrency);
        Spark.get("/student/lessons/:username", studentController::getStudentLessons);

        Spark.get("/booking/concurrent", studentController::checkConcurrentBookings);

        Spark.post("/mp/payment", mercadoPagoController::getList);
        Spark.post("/api/mp/notifications/:id", mercadoPagoController::paymentNotifications);
        Spark.get("membership/:username", mercadoPagoController::getMembership);

        after((request, response) -> closeEntityManager());

    }
}
