package websocket;
import client.ResponseException;
import com.google.gson.Gson;

import jakarta.websocket.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {

    private Session session;

    public WebSocketFacade(String url) throws ResponseException {

    }


    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
}
