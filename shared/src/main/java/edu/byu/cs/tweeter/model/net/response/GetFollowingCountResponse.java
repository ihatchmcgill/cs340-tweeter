package edu.byu.cs.tweeter.model.net.response;

public class GetFollowingCountResponse extends Response{
    private int numFollowing;

    public GetFollowingCountResponse(int numFollowing){
        super(true);
        this.numFollowing = numFollowing;
    }

    public int getNumFollowing(){
        return numFollowing;
    }
}
