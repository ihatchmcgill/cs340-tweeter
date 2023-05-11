package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.GetFollowingCountRequest;
import edu.byu.cs.tweeter.model.net.response.GetFollowingCountResponse;

/**
 * Background task that queries how many other users a specified user is following.
 */
public class GetFollowingCountTask extends GetCountTask {

    public GetFollowingCountTask(AuthToken authToken, User targetUser, Handler messageHandler) {
        super(authToken, targetUser, messageHandler);
    }

    @Override
    protected int runCountTask() {
        ServerFacade serverFacade = new ServerFacade();
        GetFollowingCountRequest request = new GetFollowingCountRequest(authToken, getTargetUser().getAlias());
        GetFollowingCountResponse response = null;
        try{
            response = serverFacade.getFollowingCount(request, "/getfollowingcount");
        }catch (TweeterRemoteException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
        return response.getNumFollowing();
    }
}
