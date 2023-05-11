package edu.byu.cs.tweeter.server.dao.interfaces;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public interface AuthDaoInterface {
    void addAuthToken(AuthToken authToken, String userAlias);
    void deleteAuthToken(AuthToken authToken);
    boolean isAuthExpired(AuthToken authToken);
    void refreshAuth(AuthToken authToken);
    void deleteAllExpiredTokens();
    String getUserAliasFromToken(AuthToken token);
}
