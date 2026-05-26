package dataaccess;

import model.UserData;

public interface UserDAO {
    /**
     * Adds new user
     *
     * @param userData Username, password, and email of user
     */
    void createUser(UserData userData) throws DataAccessException;

    /**
     * Given username, returns user data
     *
     * @param username The username of the user
     * @return User data or null
     */
    UserData getUser(String username) throws DataAccessException;

    /**
     * Clears database
     */
    void deleteAllUsers() throws DataAccessException;
}
