package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    private final HashMap<String, UserData> userDatabase = new HashMap<>();

    @Override
    public void createUser(UserData userData) {
        userDatabase.put(userData.username(),userData);
    }

    @Override
    public UserData getUser(String username) {
        return userDatabase.get(username);
    }

    @Override
    public void deleteAllUsers() {
        userDatabase.clear();
    }
}
