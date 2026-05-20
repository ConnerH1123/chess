package dataaccess;

import model.AuthData;

public interface AuthDAO {
    abstract void createAuth(AuthData authData);
    abstract AuthData getAuth(String authToken);
    abstract void deleteAuth(String authToken);
    abstract void deleteAllAuths();
}
