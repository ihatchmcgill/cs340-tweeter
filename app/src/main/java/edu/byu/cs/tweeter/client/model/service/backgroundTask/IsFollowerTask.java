package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;

/**
 * Background task that determines if one user is following another.
 */
public class IsFollowerTask extends AuthenticatedTask {

    public static final String IS_FOLLOWER_KEY = "is-follower";

    /**
     * The alleged follower.
     */
    private final User follower;

    /**
     * The alleged followee.
     */
    private final User followee;

    private boolean isFollower;

    public IsFollowerTask(AuthToken authToken, User follower, User followee, Handler messageHandler) {
        super(authToken, messageHandler);
        this.follower = follower;
        this.followee = followee;
    }

    @Override
    protected void runTask() {
        ServerFacade serverFacade = new ServerFacade();
        IsFollowerRequest request = new IsFollowerRequest(authToken, follower.getAlias(),followee.getAlias());
        IsFollowerResponse response = null;
        try{
            response = serverFacade.isFollower(request, "/isfollower");
        }catch (TweeterRemoteException e){
            e.printStackTrace();
            sendExceptionMessage(e);
        }catch(IOException e){
            e.printStackTrace();
            sendExceptionMessage(e);
        }
        isFollower = response.getIsFollower();

        if(response.getSuccess()){
            sendSuccessMessage();
        }else{
            sendFailedMessage("Failed to find follow relationship");
        }
    }

    @Override
    protected void loadSuccessBundle(Bundle msgBundle) {
        msgBundle.putBoolean(IS_FOLLOWER_KEY, isFollower);
    }
}
