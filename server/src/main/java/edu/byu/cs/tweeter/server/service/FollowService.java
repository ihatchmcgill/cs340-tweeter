package edu.byu.cs.tweeter.server.service;

import java.util.List;

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
import edu.byu.cs.tweeter.server.dao.AuthDAO;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService {

    FollowDAO followDAO;
    AuthDAO authDAO;
    UserDAO userDAO;
    public FollowService(FollowDAO followDao, AuthDAO authDAO, UserDAO userDAO){
        this.followDAO = followDao;
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }


    public DataPage<String> getFollowersAliases(String followeeAlias, String lastFollowerAlias){
        return followDAO.getFollowersAliases(followeeAlias, lastFollowerAlias);
    }
    /**
     * Returns the users that the user specified in the request is following. Uses information in
     * the request object to limit the number of followees returned and to return the next set of
     * followees after any that were returned in a previous request. Uses the {@link FollowDAO} to
     * get the followees.
     *
     * @param request contains the data required to fulfill the request.
     * @return the followees.
     */
    public FollowingResponse getFollowees(FollowingRequest request) {
        if(request.getFollowerAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }


        if(authDAO.isAuthExpired(request.getAuthToken())){
            throw new RuntimeException("[Server Error] Expired AuthToken. Please re-login");
        }else{
            authDAO.refreshAuth(request.getAuthToken());
        }
        return followDAO.getFollowees(request);
    }

    public FollowerResponse getFollowers(FollowerRequest request) {
        if(request.getFolloweeAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a followee alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }

        if(authDAO.isAuthExpired(request.getAuthToken())){
            throw new RuntimeException("[Server Error] Expired AuthToken. Please re-login");
        }else{
            authDAO.refreshAuth(request.getAuthToken());
        }

        return followDAO.getFollowers(request);
    }

    public UnfollowResponse unfollow(UnfollowRequest request) {
        if(request.getFolloweeAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a followee alias");
        }

        if(authDAO.isAuthExpired(request.getAuthToken())){
            throw new RuntimeException("[Server Error] Expired AuthToken. Please re-login");
        }else{
            authDAO.refreshAuth(request.getAuthToken());
        }

        String followerAlias = authDAO.getUserAliasFromToken(request.getAuthToken());

        userDAO.decrementFollowingCount(followerAlias);
        userDAO.decrementFollowerCount(request.getFolloweeAlias());

        return followDAO.unfollow(request, followerAlias);
    }
    public FollowResponse follow(FollowRequest request) {
        if(request.getFolloweeAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a followee alias");
        }

        if(authDAO.isAuthExpired(request.getAuthToken())){
            throw new RuntimeException("[Server Error] Expired AuthToken. Please re-login");
        }else{
            authDAO.refreshAuth(request.getAuthToken());
        }

        String followerAlias = authDAO.getUserAliasFromToken(request.getAuthToken());
        User followerUser = userDAO.getUser(followerAlias);

        userDAO.incrementFollowingCount(followerAlias);
        userDAO.incrementFollowerCount(request.getFolloweeAlias());

        User followeeUser = userDAO.getUser(request.getFolloweeAlias());
        return followDAO.follow(request, followerUser, followeeUser);

    }
    public IsFollowerResponse isFollower(IsFollowerRequest request) {
        if(request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have an AuthToken ");
        }
        else if(request.getFolloweeAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a followee alias");
        }
        else if(request.getFollowerAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        }

        if(authDAO.isAuthExpired(request.getAuthToken())){
            throw new RuntimeException("[Server Error] Expired AuthToken. Please re-login");
        }else{
            authDAO.refreshAuth(request.getAuthToken());
        }

        return followDAO.isFollower(request);
    }







}
