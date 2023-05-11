package edu.byu.cs.tweeter.model.domain;


import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a follow relationship.
 */
public class Follow implements Serializable {
    /**
     * The user doing the following.
     */
    public String follower_alias;
    /**
     * The user being followed.
     */
    public String followee_alias;

    public Follow() {
    }

    public Follow(String follower_alias, String followee_alias) {
        this.follower_alias = follower_alias;
        this.followee_alias = followee_alias;
    }

    public String getFollower_alias() {
        return follower_alias;
    }

    public void setFollower_alias(String follower_alias) {
        this.follower_alias = follower_alias;
    }

    public String getFollowee_alias() {
        return followee_alias;
    }

    public void setFollowee_alias(String followee_alias) {
        this.followee_alias = followee_alias;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Follow that = (Follow) o;
        return follower_alias.equals(that.follower_alias) &&
                followee_alias.equals(that.followee_alias);
    }

    @Override
    public int hashCode() {
        return Objects.hash(follower_alias, followee_alias);
    }

    @Override
    public String toString() {
        return "Follow{" +
                "follower=" + follower_alias +
                ", followee=" + followee_alias +
                '}';
    }
}
