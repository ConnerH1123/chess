package dataaccess;

import model.AuthData;

public interface AuthDAO {
    /**
     * Adds auth data to database
     *
     * @param authData authToken and username
     */
    void createAuth(AuthData authData) throws DataAccessException;

    /**
     * Given auth token returns auth data
     *
     * @param authToken String authorization token
     * @return Return auth data or null
     */
    AuthData getAuth(String authToken) throws DataAccessException;

    /**
     * Clears auth data given auth token
     *
     * @param authToken String authorization token
     */
    void deleteAuth(String authToken) throws DataAccessException;

    /**
     * Clears database
     */
    void deleteAllAuths() throws DataAccessException;
}
