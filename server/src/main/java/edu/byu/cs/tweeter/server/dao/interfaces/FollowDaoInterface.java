package edu.byu.cs.tweeter.server.dao.interfaces;

import java.util.Map;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowerRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.FollowerResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.bean.DataPage;

public interface FollowDaoInterface {
    FollowingResponse getFollowees(FollowingRequest request);
    FollowerResponse getFollowers(FollowerRequest request);
    UnfollowResponse unfollow(UnfollowRequest request, String followerAlias);
    FollowResponse follow(FollowRequest request, User follwerUser, User followeeUser);
    IsFollowerResponse isFollower(IsFollowerRequest request);

    DataPage<String> getFollowersAliases(String alias, String lastFollowerAlias);

    void addFollowersBatch(Map<String, User> followersMap, User followTargetUser);
}
