package server;

import com.google.gson.Gson;
import dataaccess.*;
import service.UserService.*;
import service.GameService.*;
import handler.Handler;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.HashMap;
import java.util.Map;

import service.*;

public class Server {

    private final Javalin javalin;
    private final Handler handler = new Handler();


    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user",this::register)
                .post("/session", this::login)
                .delete("/session", this::logout)
                .get("/game", this::listGames)
                .post("/game", this::createGame)
                .put("/game", this::joinGame)
                .delete("/db", this::clear);
    }

    //Body: {"username": "", "password": "", "email": ""}
    //Returns: {"username": "", "authToken": ""}
    private void register(Context ctx) {
        //String[] parameters = {"username", "password", "email"};
        //contextContainsBody(ctx, parameters));
        RegisterRequest registerRequest = new Gson().fromJson(ctx.body(), RegisterRequest.class);
        try {
            if (registerRequest.username() == null || registerRequest.password() == null || registerRequest.email() == null) {
                throw new BadRequestException("Error: missing field");
            }
            RegisterResult registerResult = handler.register(registerRequest);
            ctx.status(200);
            ctx.result(new Gson().toJson(registerResult));
        } catch (DataAccessException e) {
            exceptionHandler(e, ctx);
        }
    }

    //Body: {"username": "", "password": ""}
    //Returns: {"username": "", "authToken": ""}
    private void login(Context ctx) {
        //String[] parameters = {"username", "password"};
        LoginRequest loginRequest = new Gson().fromJson(ctx.body(), LoginRequest.class);
        try {
            if (loginRequest.username() == null || loginRequest.password() == null) {
                throw new BadRequestException("Error: missing field");
            }
            LoginResult loginResult = handler.login(loginRequest);
            ctx.status(200);
            ctx.result(new Gson().toJson(loginResult));
        } catch (DataAccessException e) {
            exceptionHandler(e, ctx);
        }
    }

    //Header: Authorization: authToken
    //Returns: {}
    private void logout(Context ctx) {
        String header = "Authorization";
        //contextContainsHeader(ctx, header);
        String json = "{\"authToken\": \"" + ctx.header(header) + "\"}";
        LogoutRequest logoutRequest = new Gson().fromJson(json, LogoutRequest.class);
        try {
            handler.logout(logoutRequest);
            ctx.status(200);
        } catch (UnauthorizedException e) {
            exceptionHandler(e, ctx);
        }
    }

    //Header: authToken
    //Body: {"gameName": ""}
    //Returns: {"gameID": <gameID>}
    private void createGame(Context ctx) {
        String header = "Authorization";
        String json = "{\"authToken\": \"" + ctx.header(header) + "\", " + ctx.body().substring(3);
        CreateRequest createRequest = new Gson().fromJson(json, CreateRequest.class);
        try {
            CreateResult createResult = handler.create(createRequest);
            ctx.status(200);
            ctx.result(new Gson().toJson(createResult));
        } catch (UnauthorizedException e) {
            exceptionHandler(e, ctx);
        }
    }

    //Header: authToken
    //Returns: {"games": [{"gameID": <gameID>, "whiteUsername": "", "blackUsername": "", "gameName": ""}]}
    private void listGames(Context ctx) {
        String header = "Authorization";
        //contextContainsHeader(ctx, header);
        String json = "{\"authToken\": \"" + ctx.header(header) + "\"}";
        ListRequest listRequest = new Gson().fromJson(json, ListRequest.class);
        try {
            ListResult listResult = handler.list(listRequest);
            ctx.status(200);
            ctx.result(new Gson().toJson(listResult));
        } catch (UnauthorizedException e) {
            exceptionHandler(e, ctx);
        }
    }


    //Header: authToken
    //Body: {"playerColor": "", "gameID": <gameID>}
    //Returns: {}
    private void joinGame(Context ctx) {
        String header = "Authorization";
        String json = "{\"authToken\": \"" + ctx.header(header) + "\", " + ctx.body().substring(3);
        JoinRequest joinRequest = new Gson().fromJson(json, JoinRequest.class);
        try {
            handler.join(joinRequest);
            ctx.status(200);
        } catch (DataAccessException e) {
            exceptionHandler(e, ctx);
        }
    }

    //Returns: {}
    private void clear(Context ctx) {
        handler.clear();
    }

    private void exceptionHandler(Exception e, Context ctx) {
        String exceptionType = e.getClass().getSimpleName();
        int errorCode;
        String errorMessage = e.getMessage();
        if (errorMessage == null || errorMessage.isEmpty()) {
            errorMessage = "null";
        }
        if (exceptionType.equals("BadRequestException")) {
            errorCode = 400;
        }
        else if (exceptionType.equals("UnauthorizedException")) {
            errorCode = 401;
        }
        else if (exceptionType.equals("AlreadyTakenException")) {
            errorCode = 403;
        }
        else {
            errorCode = 500;
        }
        ctx.status(errorCode);
        ctx.result(errorMessageToJSON(errorMessage, errorCode));
    }

    private String errorMessageToJSON(String message, int errorCode) {
        return new Gson().toJson(Map.of("message", message, "status", errorCode));
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
