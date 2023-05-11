package edu.byu.cs.tweeter.model.net.response;

public class GetFollowerCountResponse extends Response{
    private int numFollowing;

    public GetFollowerCountResponse(int numFollowing){
        super(true);
        this.numFollowing = numFollowing;
    }

    public int getNumFollowing(){
        return numFollowing;
    }
}
