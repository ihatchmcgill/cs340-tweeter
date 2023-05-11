package edu.byu.cs.tweeter.server.bean;

import edu.byu.cs.tweeter.server.dao.FollowDAO;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@DynamoDbBean
public class FollowItem {
    private String follower_handle;
    private String follower_user;
    private String followee_handle;
    private String followee_user;


    @DynamoDbPartitionKey
    @DynamoDbSecondarySortKey(indexNames = FollowDAO.IndexName)
    public String getFollower_handle() {
        return follower_handle;
    }

    public void setFollower_handle(String follower_handle) {
        this.follower_handle = follower_handle;
    }

    @DynamoDbSortKey
    @DynamoDbSecondaryPartitionKey(indexNames = FollowDAO.IndexName)
    public String getFollowee_handle() {
        return followee_handle;
    }

    public void setFollowee_handle(String followee_handle) {
        this.followee_handle = followee_handle;
    }


    public String getFollower_user() {
        return follower_user;
    }

    public void setFollower_user(String follower_user) {
        this.follower_user = follower_user;
    }

    public String getFollowee_user() {
        return followee_user;
    }

    public void setFollowee_user(String followee_user) {
        this.followee_user = followee_user;
    }


    @Override
    public String toString() {
        return "FollowItem{" +
                "follower_handle='" + follower_handle + '\'' +
                ", followee_handle=" + followee_handle  +
                '}';
    }
}
