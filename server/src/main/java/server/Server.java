package server;

import java.sql.*;
import com.google.gson.Gson;
import dataaccess.*;
import service.UserService.*;
import service.GameService.*;
import handler.Handler;
import io.javalin.Javalin;
import io.javalin.http.Context;
import java.util.Map;
import service.*;

public class Server {

    private Javalin javalin;
    private Handler handler;


    public Server() {
        try {
            handler = new Handler();
        } catch (DataAccessException e) {
            System.out.print(e.getMessage());
            return;
        }
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user",this::register)
                .post("/session", this::login)
                .delete("/session", this::logout)
                .get("/game", this::listGames)
                .post("/game", this::createGame)
                .put("/game", this::joinGame)
                .delete("/db", this::clear);

        javalin.exception(DataAccessException.class, this::handleDataAccessException);
    }

    //Body: {"username": "", "password": "", "email": ""}
    //Returns: {"username": "", "authToken": ""}
    private void register(Context ctx) throws DataAccessException {
        RegisterRequest registerRequest = new Gson().fromJson(ctx.body(), RegisterRequest.class);
        if (registerRequest.username() == null || registerRequest.password() == null || registerRequest.email() == null) {
            throw new BadRequestException("Error: missing field");
        }
        RegisterResult registerResult = handler.register(registerRequest);
        ctx.status(200);
        ctx.result(new Gson().toJson(registerResult));
    }

    //Body: {"username": "", "password": ""}
    //Returns: {"username": "", "authToken": ""}
    private void login(Context ctx) throws DataAccessException {
        LoginRequest loginRequest = new Gson().fromJson(ctx.body(), LoginRequest.class);
        if (loginRequest.username() == null || loginRequest.password() == null) {
            throw new BadRequestException("Error: missing field");
        }
        LoginResult loginResult = handler.login(loginRequest);
        ctx.status(200);
        ctx.result(new Gson().toJson(loginResult));
    }

    //Header: Authorization: authToken
    //Returns: {}
    private void logout(Context ctx) throws DataAccessException {
        String header = "Authorization";
        LogoutRequest logoutRequest = new LogoutRequest(ctx.header(header));
        handler.logout(logoutRequest);
        ctx.status(200);
    }

    //Header: authToken
    //Body: {"gameName": ""}
    //Returns: {"gameID": <gameID>}
    private void createGame(Context ctx) throws DataAccessException {
        String header = "Authorization";
        String authToken = ctx.header(header);
        CreateRequest partialCreateRequest = new Gson().fromJson(ctx.body(), CreateRequest.class);
        CreateRequest createRequest = partialCreateRequest.setAuthToken(authToken);
        if (createRequest.gameName() == null) {
            throw new BadRequestException("Error: game name required");
        }
        CreateResult createResult = handler.create(createRequest);
        ctx.status(200);
        ctx.result(new Gson().toJson(createResult));
    }

    //Header: authToken
    //Returns: {"games": [{"gameID": <gameID>, "whiteUsername": "", "blackUsername": "", "gameName": ""}]}
    private void listGames(Context ctx) throws DataAccessException {
        String header = "Authorization";
        String authToken = ctx.header(header);
        ListRequest listRequest = new ListRequest(authToken);
        ListResult listResult = handler.list(listRequest);
        ctx.status(200);
        ctx.result(new Gson().toJson(listResult));
    }

    //Header: authToken
    //Body: {"playerColor": "", "gameID": <gameID>}
    //Returns: {}
    private void joinGame(Context ctx) throws DataAccessException {
        String header = "Authorization";
        String authToken = ctx.header(header);
        JoinRequest partialJoinRequest = new Gson().fromJson(ctx.body(), JoinRequest.class);
        JoinRequest joinRequest = partialJoinRequest.setAuthToken(authToken);
        if (joinRequest.playerColor() == null || joinRequest.gameID() == null) {
            throw new BadRequestException("Error: missing field");
        }
        handler.join(joinRequest);
        ctx.status(200);
    }

    //Returns: {}
    private void clear(Context ctx) throws DataAccessException {
        handler.clear();
    }

    private void handleDataAccessException(DataAccessException e, Context ctx) {
        String exceptionType = e.getClass().getSimpleName();
        String errorMessage = e.getMessage();
        if (errorMessage == null || errorMessage.isEmpty()) {
            errorMessage = "null";
        }
        int errorCode = switch (exceptionType) {
            case "BadRequestException" -> 400;
            case "UnauthorizedException" -> 401;
            case "AlreadyTakenException" -> 403;
            default -> 500;
        };
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
