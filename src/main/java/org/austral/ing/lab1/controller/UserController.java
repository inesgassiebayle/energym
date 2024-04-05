package org.austral.ing.lab1.controller;
import com.google.gson.Gson;
import org.austral.ing.lab1.Students;
import org.austral.ing.lab1.Users;
import org.austral.ing.lab1.dto.SignupUserDto;
import org.austral.ing.lab1.model.Student;
import org.austral.ing.lab1.model.User;
import org.austral.ing.lab1.model.UserType;
import spark.Request;
import spark.Response;
import javax.persistence.EntityManager;
import java.util.regex.Pattern;

public class UserController {
    private final Users users;
    private final Students students;
    private final Gson gson = new Gson();

    public UserController(EntityManager entityManager) {
        this.users = new Users(entityManager);
        this.students =new Students(entityManager);
    }

    public String studentSignup(Request req, Response res) {
        SignupUserDto userDto = gson.fromJson(req.body(), SignupUserDto.class);
        if(!isValidEmailFormat(userDto.getEmail())){
            return "Invalid email";
        }


        if(users.findUserByUsername(userDto.getUsername())!=null){
            return "Username already exists";
        }

        User user = userSignup(userDto);
        Student student = new Student();
        student.setUser(user);
        students.persist(student);

        res.type("application/json");
        return user.asJson();
    }


    private User userSignup(SignupUserDto userDto){
        User user = new User(userDto.getFirstName(), userDto.getLastName(), userDto.getEmail(), userDto.getUsername(), userDto.getPassword());
        user.setType(UserType.STUDENT);
        users.persist(user);
        return user;
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
