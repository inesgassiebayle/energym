package org.austral.ing.lab1.controller;
import com.auth0.jwt.JWT;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.austral.ing.lab1.dto.PasswordChangeDto;
import org.austral.ing.lab1.dto.SignUpDto;
import org.austral.ing.lab1.model.*;
import org.austral.ing.lab1.queries.*;
import org.austral.ing.lab1.service.BookingService;
import org.austral.ing.lab1.service.LessonService;
import spark.Request;
import spark.Response;
import javax.persistence.EntityManager;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class UserController {
    private final Users users;
    private final Students students;
    private final Professors professsors;
    private final Administrators administrators;
    private final Lessons lessons;
    private final LessonBookings lessonBookings;
    private final BookingService bookingService;
    private final LessonService lessonService;

    private final Gson gson = new Gson();

    public UserController(ReminderService reminderService, EmailSender emailSender) {
        this.users = new Users();
        this.students = new Students();
        this.professsors = new Professors();
        this.administrators = new Administrators();
        this.lessons = new Lessons();
        this.lessonBookings = new LessonBookings();
        this.lessonService = new LessonService(emailSender, reminderService);
        this.bookingService = new BookingService(emailSender, reminderService);
    }

    public String studentSignup(Request req, Response res) {
        SignUpDto signUpDto = gson.fromJson(req.body(), SignUpDto.class);
        String firstName = signUpDto.getFirstName();
        String lastName = signUpDto.getLastName();
        String email = signUpDto.getEmail();
        String username = signUpDto.getUsername();
        String password = signUpDto.getPassword();
        if(!isValidEmailFormat(email)){
            res.status(400);
            return "Invalid email";
        }
        if(users.findUserByUsernameOrEmail(username, email)!=null){
            res.status(400);
            return "Username or email already registered";
        }
        if(!isValidPassword(password)){
            res.status(400);
            return "Invalid password";
        }
        User user = new User(firstName, lastName, email, username, password);
        user.setType(UserType.STUDENT);
        users.persist(user);
        Student student = new Student();
        student.setUser(user);
        students.persist(student);
        res.type("application/json");
        return user.asJson();
    }

    public String professorSignup(Request req, Response res) {
        SignUpDto signUpDto = gson.fromJson(req.body(), SignUpDto.class);
        String firstName = signUpDto.getFirstName();
        String lastName = signUpDto.getLastName();
        String email = signUpDto.getEmail();
        String username = signUpDto.getUsername();
        String password = signUpDto.getPassword();

        if(!isValidEmailFormat(email)){
            res.status(400);
            return "Invalid email";
        }
        if(users.findUserByUsernameOrEmail(username, email)!=null){
            res.status(400);
            return "Username or email already registered";
        }
        if(!isValidPassword(password)){
            res.status(400);
            return "Invalid password";
        }
        User user = new User(firstName, lastName, email, username, password);
        user.setType(UserType.PROFESSOR);
        users.persist(user);
        Professor professor = new Professor();
        professor.setUser(user);
        professsors.persist(professor);
        res.type("application/json");
        return user.asJson();
    }

    public String administratorSignup(Request req, Response res) {
        SignUpDto signUpDto = gson.fromJson(req.body(), SignUpDto.class);
        String firstName = signUpDto.getFirstName();
        String lastName = signUpDto.getLastName();
        String email = signUpDto.getEmail();
        String username = signUpDto.getUsername();
        String password = signUpDto.getPassword();
        if(!isValidEmailFormat(email)){
            res.status(400);
            return "Invalid email";
        }
        if(users.findUserByUsernameOrEmail(username, email)!=null){
            res.status(400);
            return "Username or email already registered";
        }
        if(!isValidPassword(password)){
            res.status(400);
            return "Invalid password";
        }
        User user = new User(firstName, lastName, email, username, password);
        user.setType(UserType.ADMINISTRATOR);
        users.persist(user);
        Administrator administrator = new Administrator();
        administrator.setUser(user);
        administrators.persist(administrator);
        res.type("application/json");
        return user.asJson();
    }

    public boolean isValidEmailFormat(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pat = Pattern.compile(emailRegex);
        return pat.matcher(email).matches();
    }

    public boolean isValidPassword(String password) {
        String numberRegex = ".*[0-9].*";
        String uppercaseLetterRegex = ".*[A-Z].*";
        Pattern numberPattern = Pattern.compile(numberRegex);
        Pattern uppercaseLetterPattern = Pattern.compile(uppercaseLetterRegex);
        return numberPattern.matcher(password).matches() && uppercaseLetterPattern.matcher(password).matches();
    }

    public String deleteUser(Request req, Response res){
        String username = req.params(":username");
        if(username == null || username.isBlank()){
            return "Invalid input";
        }
        User user = users.findUserByUsername(username);
        if(user == null) {
            return "User does not exist";
        }
        user.deactivate();
        users.persist(user);
        if(user.getType().equals(UserType.PROFESSOR)){
            Professor professor = professsors.findProfessorByUsername(username);
            Set<Lesson> lessons2 = professor.getClasses();
            for(Lesson lesson: lessons2){
                lessonService.deleteLesson(lesson);
            }
        }
        if (user.getType().equals(UserType.STUDENT)) {
            Student student = students.findStudentByUsername(username);
            Set<BookedLesson> bookings = student.getBookings();
            for (BookedLesson booking: bookings) {
                if (LocalDate.now().isBefore(booking.getLesson().getStartDate())) {
                    if (booking.state()) {
                        bookingService.deleteBooking(booking);
                    }
                }
            }
        }
        res.type("application/json");
        return user.asJson();
    }

    public String changePassword(Request req, Response res){
        PasswordChangeDto dto = gson.fromJson(req.body(), PasswordChangeDto.class);
        String username = dto.getUsername();
        String password = dto.getPassword();
        String confirmationPassword = dto.getPasswordConfirmation();

        if(username == null || username.isBlank()){
            res.status(400);
            return "Invalid input";
        }
        User user = users.findUserByUsername(username);
        if(user == null) {
            res.status(400);
            return "User does not exist";
        }
        if(!user.state()){
            res.status(400);
            return "User does not exist";
        }
        if(!password.equals(confirmationPassword)){
            res.status(400);
            return "Passwords do not match";
        }
        if(!isValidPassword(password)){
            res.status(400);
            return "Invalid password";
        }
        user.setPassword(password);
        users.persist(user);
        res.type("application/json");
        return user.asJson();
    }

    public String sendChangePasswordEmail(Request req, Response res){
        JsonObject requestBody = gson.fromJson(req.body(), JsonObject.class);
        String mail = requestBody.get("mail").getAsString();
        if (mail == null || mail.isBlank()) {
            res.status(400);
            return "Invalid input";
        }
        User user = users.findUserByEmail(mail);
        if (user == null) {
            res.status(400);
            return "User does not exist";
        }
        if (!user.state()) {
            res.status(400);
            return "User is deactivated";
        }
        EmailSender emailSender = new EmailSender();
        String temporaryPassword = generateTemporaryPassword();

        emailSender.sendEmail(mail, "Password change", "This is your temporary password: "+temporaryPassword);
        user.setPassword(temporaryPassword);
        users.persist(user);

        return users.toString();
    }

    private String generateTemporaryPassword() {
        StringBuilder password = new StringBuilder(20);
        do {
            String UPPERCASE_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
            String DIGITS = "0123456789";
            String ALL_CHARACTERS = UPPERCASE_LETTERS + DIGITS;
            SecureRandom RANDOM = new SecureRandom();

            password = new StringBuilder(20);

            for (int i = 0; i < 20; i++) {
                int index = RANDOM.nextInt(ALL_CHARACTERS.length());
                password.append(ALL_CHARACTERS.charAt(index));
            }
        } while (!isValidPassword(password.toString()));
        return password.toString();
    }


}
