package client;

import com.google.gson.Gson;
import model.GameData;
import request.*;

import java.net.URI;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Objects;


public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;
    private String authToken = null;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public ServerFacade(int port) {
        serverUrl = "http://localhost:" + port;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public String register(RegisterRequest registerRequest) throws ResponseException {
        HttpRequest request = buildRequest("POST", "/user", null, registerRequest);
        HttpResponse<String> response = sendRequest(request);
        RegisterResult registerResult = handleResponse(response, RegisterResult.class);
        assert registerResult != null;
        authToken = registerResult.authToken();
        return authToken;
    }

    public String login(LoginRequest loginRequest) throws ResponseException {
        HttpRequest request = buildRequest("POST", "/session", null, loginRequest);
        HttpResponse<String> response = sendRequest(request);
        LoginResult loginResult = handleResponse(response, LoginResult.class);
        assert loginResult != null;
        authToken = loginResult.authToken();
        return authToken;
    }

    public void logout() throws ResponseException {
        HttpRequest request = buildRequest("DELETE", "/session", authToken, null);
        HttpResponse<String> response = sendRequest(request);
        handleResponse(response, null);
    }

    public int createGame(CreateRequest createRequest) throws ResponseException {
        HttpRequest request = buildRequest("POST", "/game", authToken, createRequest);
        HttpResponse<String> response = sendRequest(request);
        CreateResult createResult = handleResponse(response, CreateResult.class);
        assert createResult != null;
        return createResult.gameID();
    }

    public GameData[] listGames() throws ResponseException {
        HttpRequest request = buildRequest("GET", "/game", authToken, null);
        HttpResponse<String> response = sendRequest(request);
        ListResult listResult = handleResponse(response, ListResult.class);
        assert listResult != null;
        return listResult.games();
    }

    public void joinGame(JoinRequest joinRequest) throws ResponseException {
        HttpRequest request = buildRequest("PUT", "/game", authToken, joinRequest);
        HttpResponse<String> response = sendRequest(request);
        handleResponse(response, null);
    }

    private HttpRequest buildRequest(String method, String path, String authorization, Object body) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        if (authorization != null) {
            request.setHeader("Authorization", authorization);
        }
        return request.build();
    }

    private BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws ResponseException {
        try {
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new ResponseException(ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ResponseException {
        int status = response.statusCode();
        if (status != 200) {
            String body = response.body();
            if (!Objects.equals(body, "null")) {
                int messageStart = body.indexOf("\"message\":") + 11;
                int messageEnd = messageStart + body.substring(messageStart).indexOf("\"");
                String message = body.substring(messageStart, messageEnd);
                throw new ResponseException(message);
            }
            throw new ResponseException("Error " + status + ": An error occurred");
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

}
