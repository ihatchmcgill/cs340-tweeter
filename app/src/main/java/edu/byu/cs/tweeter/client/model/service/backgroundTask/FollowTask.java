package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;

/**
 * Background task that establishes a following relationship between two users.
 */
public class FollowTask extends AuthenticatedTask {
    /**
     * The user that is being followed.
     */
    private final User followee;

    public FollowTask(AuthToken authToken, User followee, Handler messageHandler) {
        super(authToken, messageHandler);
        this.followee = followee;
    }

    @Override
    protected void runTask() {
        ServerFacade serverFacade = new ServerFacade();
        FollowRequest request = new FollowRequest(authToken, followee.getAlias());
        FollowResponse response = null;
        try{
            response = serverFacade.follow(request, "/follow");
        }catch (TweeterRemoteException e){
            e.printStackTrace();
            sendExceptionMessage(e);
        }catch(IOException e){
            e.printStackTrace();
            sendExceptionMessage(e);
        }
        // We could do this from the presenter, without a task and handler, but we will
        // eventually remove the auth token from  the DB and will need this then.

        if(response.getSuccess()){
            sendSuccessMessage();
        }else{
            sendFailedMessage("Failed to Follow");
        }
    }

}
