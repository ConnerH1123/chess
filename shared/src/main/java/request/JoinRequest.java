package request;

public record JoinRequest(String authToken, String playerColor, Integer gameID) {
    public JoinRequest setAuthToken(String authToken) {
        return new JoinRequest(authToken, this.playerColor, this.gameID);
    }
}
