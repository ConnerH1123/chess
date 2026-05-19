package server;

import com.google.gson.Gson;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.Map;


public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/echo", this::echo);

        // Register your endpoints and exception handlers here.

    }

    private void echo(Context context) {
        // Convert body json to object
        Map bodyObject = getBodyObject(context, Map.class);

        // Convert bodyObject back to json and send to client
        String json = new Gson().toJson(bodyObject);
        context.json(json);
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
