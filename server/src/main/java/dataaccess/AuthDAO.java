package dataaccess;

import model.AuthData;

public interface AuthDAO {
    /**
     * Adds auth data to database
     *
     * @param authData authToken and username
     */
    abstract void createAuth(AuthData authData);

    /**
     * Given auth token returns auth data
     *
     * @param authToken String authorization token
     * @return Return auth data or null
     */
    abstract AuthData getAuth(String authToken);

    /**
     * Clears auth data given auth token
     *
     * @param authToken String authorization token
     */
    abstract void deleteAuth(String authToken);

    /**
     * Clears database
     */
    abstract void deleteAllAuths();
}
