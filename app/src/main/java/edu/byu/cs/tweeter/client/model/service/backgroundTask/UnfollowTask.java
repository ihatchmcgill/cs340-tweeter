package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;

/**
 * Background task that removes a following relationship between two users.
 */
public class UnfollowTask extends AuthenticatedTask {

    /**
     * The user that is being unfollowed.
     */
    private final User followee;

    public UnfollowTask(AuthToken authToken, User followee, Handler messageHandler) {
        super(authToken, messageHandler);
        this.followee = followee;
    }

    @Override
    protected void runTask() {
        ServerFacade serverFacade = new ServerFacade();
        UnfollowRequest request = new UnfollowRequest(authToken, followee.getAlias());
        UnfollowResponse response = null;
        try{
            response = serverFacade.unfollow(request, "/unfollow");
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
            sendFailedMessage("Unfollow Failed");
        }
    }


}
