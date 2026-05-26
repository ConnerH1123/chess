package service;

import dataaccess.*;
import model.UserData;
import model.AuthData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Objects;
import java.util.UUID;

public class UserService extends Authorizable {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        super(authDAO);
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest r) throws DataAccessException {
        String username = r.username();
        String password = r.password();
        String email = r.email();
        if (userDAO.getUser(username) != null) {
            throw new AlreadyTakenException("Error: username already taken");
        }
        UserData newUserData = new UserData(username, password, email);
        userDAO.createUser(newUserData);
        String authToken = generateAuthToken();
        AuthData authData = new AuthData(authToken, username);
        authDAO.createAuth(authData);
        return new RegisterResult(username, authToken);
    }

    public record RegisterRequest(String username, String password, String email) {}
    public record RegisterResult(String username, String authToken) {}

    private String generateAuthToken(){
        return UUID.randomUUID().toString();
    }

    public LoginResult login(LoginRequest r) throws DataAccessException {
        String username = r.username();
        String password = r.password();
        UserData userData = userDAO.getUser(username);
        if (userData == null || (!Objects.equals(password, userData.password())) && !BCrypt.checkpw(password, userData.password())) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        String authToken = generateAuthToken();
        AuthData authData = new AuthData(authToken, username);
        authDAO.createAuth(authData);
        return new LoginResult(username, authToken);
    }

    public record LoginRequest(String username, String password) {}
    public record LoginResult(String username, String authToken) {}

    public void logout(LogoutRequest r) throws DataAccessException{
        String authToken = r.authToken();
        authorize(authToken);
        authDAO.deleteAuth(authToken);
    }

    public record LogoutRequest(String authToken) {}
}
