package org.austral.ing.lab1;

import org.austral.ing.lab1.model.User;
import org.austral.ing.lab1.model.UserType;
import spark.Request;
import spark.Response;

import javax.persistence.EntityManager;


public class Operations {
    private final Users users;

    public Operations(EntityManager entityManager) {
        this.users = new Users(entityManager);
    }

    public String studentSignup(Request req, Response res) {
        final String firstName = req.queryParams("firstName");
        final String lastName = req.queryParams("lastName");
        final String email = req.queryParams("email");
        final String username = req.queryParams("username");
        final String password = req.queryParams("password");

        User user = new User(firstName, lastName, email, username, password);
        user.setType(UserType.STUDENT);

        users.persist(user);
        System.out.println("User fue creado");

        res.type("application/json");
        return user.asJson();
    }


    public Object login(Request req, Response res) {
        final String username = req.queryParams("username");
        final String password = req.queryParams("password");

        User user = users.signin(username, password);

        if (user != null) {
            req.session().attribute("user_id", user.getId());
            res.type("application/json");
            return user.asJson();
        } else {
            res.status(401); // Unauthorized
            return "Invalid email or password";
        }
    }

    public Object getUser(Request req, Response res) {
        final String username = req.params(":username");
        final User user = users.findUserByUsername(username);

        if (user == null) {
            res.status(404); // Not Found
            return "User not found";
        }

        res.type("application/json");
        return user.asJson();
    }
}
