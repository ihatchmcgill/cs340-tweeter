package edu.byu.cs.tweeter.model.net.response;

public class IsFollowerResponse extends Response{

    private boolean isFollower;

    IsFollowerResponse(boolean success) {
        super(success);
    }

    public IsFollowerResponse(boolean success, boolean isFollower){
        super(success);
        this.isFollower = isFollower;
    }

    public boolean getIsFollower() {
        return isFollower;
    }

    public void setFollower(boolean follower) {
        isFollower = follower;
    }
}
