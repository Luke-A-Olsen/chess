package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.Map;

public class MemoryAuthDAO implements AuthDAO
{
    private final Map<String, AuthData> auths = new HashMap<>();

    @Override
    public void clear()
    {
        auths.clear();
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException
    {
        if (auth == null || auth.authToken() == null)
        {
            throw new DataAccessException("Error: bad request");
        }
        if (auths.containsKey(auth.authToken()))
        {
            throw new DataAccessException("Error: already taken");
        }
        auths.put(auth.authToken(), auth);
    }

    @Override
    public AuthData getAuth(String authToken)
    {
        return auths.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException
    {
        if (authToken == null || !auths.containsKey(authToken))
        {
            throw new DataAccessException("Error: unauthorized");
        }
        auths.remove(authToken);
    }
}
