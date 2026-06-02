package request;

public record CreateRequest(String authToken, String gameName) {
    public CreateRequest setAuthToken(String authToken) {
        return new CreateRequest(authToken, this.gameName);
    }
}
