package dataaccess;

import model.UserData;

public interface UserDAO {
    /**
     * Adds new user
     *
     * @param userData Username, password, and email of user
     */
    abstract void createUser(UserData userData);

    /**
     * Given username, returns user data
     *
     * @param username The username of the user
     * @return User data or null
     */
    abstract UserData getUser(String username);

    /**
     * Clears database
     */
    abstract void deleteAllUsers();
}
