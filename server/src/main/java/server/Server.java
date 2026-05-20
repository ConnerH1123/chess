package server;

import com.google.gson.Gson;
import dataaccess.*;
import service.UserService.*;
import handler.Handler;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.Map;
import java.util.Objects;

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
            RegisterResult registerResult = handler.register(registerRequest);
            ctx.status(200);
            ctx.result(new Gson().toJson(registerResult));
        } catch (AlreadyTakenException e) {
            exceptionHandler(e, ctx);
        }
    }

    //Body: {"username": "", "password": ""}
    //Returns: {"username": "", "authToken": ""}
    private void login(Context ctx) {
        //String[] parameters = {"username", "password"};
        //contextContainsBody(ctx, parameters));
        LoginRequest loginRequest = new Gson().fromJson(ctx.body(), LoginRequest.class);
        try {
            LoginResult loginResult = handler.login(loginRequest);
            ctx.status(200);
            ctx.result(new Gson().toJson(loginResult));
        } catch (UnauthorizedException e) {
            exceptionHandler(e, ctx);
        }
    }

    //Header: Authorization: authToken
    //Returns: {}
    private void logout(Context ctx) {
        String header = "Authorization";
        //contextContainsHeader(ctx, header);
        String json = "{Authorization: " + ctx.header(header) + "}";
        //Somehow authToken is not getting put into logoutRequest;
        LogoutRequest logoutRequest = new Gson().fromJson(json, LogoutRequest.class);
        try {
            handler.logout(logoutRequest);
            ctx.status(200);
        } catch (UnauthorizedException e) {
            exceptionHandler(e, ctx);
        }
    }
    //Header: authToken
    //Returns: {"games": [{"gameID": <gameID>, "whiteUsername": "", "blackUsername": "", "gameName": ""}]}
    private void listGames(Context ctx) {

    }

    //Header: authToken
    //Body: {"gameName": ""}
    //Returns: {"games": [{"gameID": <gameID>, "whiteUsername": "", "blackUsername": "", "gameName": ""}]}
    private void createGame(Context ctx) {

    }

    //Header: authToken
    //Body: {"playerColor": "", "gameID": <gameID>}
    //Returns: {}
    private void joinGame(Context ctx) {

    }

    //Returns: {}
    private void clear(Context ctx) {

    }

    private void exceptionHandler(Exception e, Context ctx) {
        String exceptionType = e.getClass().getSimpleName();
        int errorCode;
        String errorMessage = e.getMessage();
        if (exceptionType.equals("BadRequestException")) {
            errorCode = 400;
        }
        if (exceptionType.equals("UnauthorizedException")) {
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
