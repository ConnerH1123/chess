package dataaccess;

import model.UserData;

public interface UserDAO {
    abstract void createUser(UserData userData);
    abstract UserData getUser(String username);
    abstract void deleteAllUsers();
}
