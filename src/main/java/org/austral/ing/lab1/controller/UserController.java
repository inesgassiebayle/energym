package org.austral.ing.lab1.controller;
import com.auth0.jwt.JWT;
import com.google.gson.Gson;
import org.austral.ing.lab1.dto.SignUpDto;
import org.austral.ing.lab1.model.Professor;
import org.austral.ing.lab1.model.Student;
import org.austral.ing.lab1.queries.Professors;
import org.austral.ing.lab1.queries.Students;
import org.austral.ing.lab1.queries.Users;
import org.austral.ing.lab1.model.User;
import org.austral.ing.lab1.model.UserType;
import spark.Request;
import spark.Response;
import javax.persistence.EntityManager;
import java.util.Calendar;
import java.util.regex.Pattern;

public class UserController {
    private final Users users;
    private final Students students;
    private final Professors professsors;
    private final Gson gson = new Gson();

    public UserController(EntityManager entityManager) {
        this.users = new Users(entityManager);
        this.students = new Students(entityManager);
        this.professsors = new Professors(entityManager);
    }

    public String studentSignup(Request req, Response res) {
        SignUpDto signUpDto = gson.fromJson(req.body(), SignUpDto.class);

        String firstName = signUpDto.getFirstName();
        String lastName = signUpDto.getLastName();
        String email = signUpDto.getEmail();
        String username = signUpDto.getUsername();
        String password = signUpDto.getPassword();

        if(!isValidEmailFormat(email)){
            return "Invalid email";
        }

        if(users.findUserByUsernameOrEmail(username, email)!=null){
            return "Username or email already registered";
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
            return "Invalid email";
        }

        if(users.findUserByUsernameOrEmail(username, email)!=null){
            return "Username or email already registered";
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


    public String login(Request req, Response res) {
        // Get the parameters from the request (username and password)
        final String username = req.queryParams("username");
        final String password = req.queryParams("password");

        // Find the user based on the provided username
        User user = users.findUserByUsername(username);

        // Check if the user is found and if the password matches
        if (user == null || !user.getPassword().equals(password)) {
            return "Invalid username or password";
        }

        Calendar expiration = Calendar.getInstance();
        expiration.add(Calendar.MINUTE, 30);

        //String token = JWT.create().withClaim("userId", user.getId()).withExpiresAt(expiration.getTime()).sign();


        // Check if the user already has a session started
        if (req.session().attribute("userId") != null) {
            return "Already signed in";
        }

        // Set the user's identification in the session to mark them as authenticated
        req.session().attribute("userId", user.getId());

        // Set the response type as JSON
        res.type("application/json");

        // Return the JSON representation of the user object
        return user.asJson();
    }

    public boolean isValidEmailFormat(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pat = Pattern.compile(emailRegex);
        return pat.matcher(email).matches();
    }

}
