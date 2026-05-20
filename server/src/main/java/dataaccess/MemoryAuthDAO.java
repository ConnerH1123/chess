package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {
    private final HashMap<String, AuthData> authDatabase = new HashMap<>();

    @Override
    public void createAuth(AuthData authData) {
        authDatabase.put(authData.authToken(), authData);
    }

    @Override
    public AuthData getAuth(String authToken) {
        return authDatabase.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) {
        authDatabase.remove(authToken);
    }

    @Override
    public void deleteAllAuths() {
        authDatabase.clear();
    }
}
