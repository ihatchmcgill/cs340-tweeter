package edu.byu.cs.tweeter.server.dao.interfaces;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.GetFollowerCountResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowingCountResponse;

public interface UserDaoInterface {
    GetFollowerCountResponse getFollowerCount(String followeeAlias);
    GetFollowingCountResponse getFolloweeCount(String followerAlias);
    User getUser(String username);
    User registerUser(RegisterRequest request, String passwordHash);

    boolean isValidPassword(String alias, String hashPassword);

    void incrementFollowerCount(String followeeAlias);

    void decrementFollowerCount(String followeeAlias);

    void incrementFollowingCount(String followerAlias);

    void decrementFollowingCount(String followerAlias);
}
