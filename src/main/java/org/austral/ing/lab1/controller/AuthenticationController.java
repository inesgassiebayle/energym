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

    public AuthenticationController(EntityManager entityManager) {
        this.users = new Users(entityManager);
    }

    public String createAuthentication(Request req, Response res) {
        LoginDto loginDto = gson.fromJson(req.body(), LoginDto.class);

        // Validación de credenciales nulas
        if (loginDto.getUsername() == null || loginDto.getPassword() == null) {
            res.status(400); // HTTP 400 Bad Request
            return gson.toJson("Invalid username or password");
        }

        User user = users.findUserByUsername(loginDto.getUsername());

        // Verificación de usuario y contraseña
        if (user != null && user.getPassword().equals(loginDto.getPassword())) {
            // Comprobar si ya existe un token activo
            Optional<String> existingToken = usernameByToken.asMap().entrySet().stream()
                    .filter(entry -> entry.getValue().equals(user.getUsername()))
                    .map(Map.Entry::getKey)
                    .findFirst();

            if (existingToken.isPresent()) {
                // Opción 1: Devolver el token existente (sin crear uno nuevo)
                // Opción 2: Invalidar el token antiguo y continuar para crear uno nuevo
                usernameByToken.invalidate(existingToken.get());
            }

            // Generar nuevo token
            final String token = UUID.randomUUID().toString();
            usernameByToken.put(token, user.getUsername());

            AuthenticationDto authenticationDto = new AuthenticationDto(user, token);
            res.status(200); // HTTP 200 OK
            return gson.toJson(authenticationDto);

        } else {
            res.status(404); // HTTP 404 Not Found
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
            res.status(401); // Unauthorized
            return gson.toJson("Not signed in");
        }

        String token = tokenOpt.get();
        String username = usernameByToken.getIfPresent(token);

        if (username == null) {
            res.status(403);
            return gson.toJson("Invalid token");
        }

        User user = users.findUserByUsername(username);
        if (user == null) {
            res.status(404);
            return gson.toJson("User not found");
        }

        res.type("application/json");
        return gson.toJson(user);
    }
}
