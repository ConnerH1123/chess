package server;

import com.google.gson.Gson;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.Map;


public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/echo", this::echo)
                .post("/user",this::register)
                .post("/session", this::login)
                .delete("/session", this::logout)
                .get("/game", this::listGames)
                .post("/game", this::createGame)
                .put("/game", this::joinGame)
                .delete("/db", this::clear);

        // Register your endpoints and exception handlers here.

    }

    private void echo(Context context) {
        // Convert body json to object
        Map bodyObject = getBodyObject(context, Map.class);

        // Convert bodyObject back to json and send to client
        String json = new Gson().toJson(bodyObject) + "\n";
        context.json(json);
    }


    //Body: {"username": "", "password": "", "email": ""}
    //Returns: {"username": "", "authToken": ""}
    private void register(Context ctx) {

    }

    //Body: {"username": "", "password": ""}
    //Returns: {"username": "", "authToken": ""}
    private void login(Context ctx) {

    }

    //Header: authToken
    //Returns: {}
    private void logout(Context ctx) {

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

    private static <T> T getBodyObject(Context context, Class<T> theClass) {
        var bodyObject = new Gson().fromJson(context.body(), theClass);

        if (bodyObject == null) {
            throw new RuntimeException("missing required body");
        }

        return bodyObject;
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
