package service;

import dataaccess.AuthDAO;
import dataaccess.UnauthorizedException;
import model.AuthData;

public class Authorizable {
    AuthDAO authDAO;

    public Authorizable(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    public AuthData authorize(String authToken) throws UnauthorizedException {
        AuthData authData = authDAO.getAuth(authToken);
        if (authData == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        return authData;
    }
}
