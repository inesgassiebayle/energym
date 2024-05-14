package org.austral.ing.lab1.controller;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import org.austral.ing.lab1.dto.AuthenticationDto;
import org.austral.ing.lab1.dto.LoginDto;
import org.austral.ing.lab1.model.User;
import org.austral.ing.lab1.queries.Users;
import spark.Request;
import spark.Response;

import javax.persistence.EntityManager;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static java.util.concurrent.TimeUnit.MINUTES;

public class AuthenticationController {
    private final Gson gson = new Gson();
    private final Users users;
    private final Cache<String, String> usernameByToken = CacheBuilder.newBuilder().expireAfterWrite(30, MINUTES).build();

    public AuthenticationController() {
        this.users = new Users();
    }

    public String createAuthentication(Request req, Response res) {
        LoginDto loginDto = gson.fromJson(req.body(), LoginDto.class);

        if (loginDto.getUsername() == null || loginDto.getPassword() == null) {
            res.status(400);
            return gson.toJson("Invalid username or password");
        }

        User user = users.findUserByUsername(loginDto.getUsername());

        if(user == null){
            res.status(400);
            return "User does not exist";
        }

        if(!user.state()){
            res.status(400);
            return "User does not exist";
        }

        if (user.getPassword().equals(loginDto.getPassword())) {
            Optional<String> existingToken = usernameByToken.asMap().entrySet().stream()
                    .filter(entry -> entry.getValue().equals(user.getUsername()))
                    .map(Map.Entry::getKey)
                    .findFirst();

            if (existingToken.isPresent()) {
                usernameByToken.invalidate(existingToken.get());
            }

            final String token = UUID.randomUUID().toString();
            usernameByToken.put(token, user.getUsername());

            AuthenticationDto authenticationDto = new AuthenticationDto(user, token);
            res.status(200);
            return gson.toJson(authenticationDto);

        } else {
            res.status(404);
            return gson.toJson("User not found");
        }
    }


    public String deleteAuthentication(Request req, Response res){
        getToken(req)
                .ifPresentOrElse(token -> {
                    usernameByToken.invalidate(token);
                    res.status(204);
                }, () -> {
                    res.status(404);
                });

        return "";
    }

    private static Optional<String> getToken(Request request) {
        return Optional.ofNullable(request.headers("Authorization")).map(AuthenticationController::getTokenFromHeader);
    }

    private static String getTokenFromHeader(String authorizationHeader) {
        return authorizationHeader.replace("Bearer ", "");
    }

    private boolean isAuthenticated(String token) {
        return usernameByToken.getIfPresent(token) != null;
    }

    public String getCurrentUser(Request req, Response res) {
        Optional<String> tokenOpt = getToken(req);

        if (!tokenOpt.isPresent()) {
            res.status(401);
            return "Not signed in";
        }

        String token = tokenOpt.get();
        String username = usernameByToken.getIfPresent(token);

        if (username == null) {
            res.status(403);
            return "Invalid token";
        }

        User user = users.findUserByUsername(username);
        if (user == null) {
            res.status(404);
            return "User not found";
        }
        if(!user.state()){
            res.status(404);
            return "User not found";
        }

        res.type("application/json");
        return user.asJson();
    }

    public String deleteAccount(Request req, Response res){
        Optional<String> tokenOpt = getToken(req);

        if (!tokenOpt.isPresent()) {
            res.status(401);
            return "Not signed in";
        }

        String token = tokenOpt.get();
        String username = usernameByToken.getIfPresent(token);

        if (username == null) {
            res.status(403);
            return "Invalid token";
        }

        User user = users.findUserByUsername(username);

        if (user == null) {
            res.status(404);
            return "User not found";
        }

        if(!user.state()){
            res.status(404);
            return "User was already deleted";
        }

        user.deactivate();
        users.persist(user);

        usernameByToken.invalidate(token);

        res.status(204);
        return "";
    }
}
