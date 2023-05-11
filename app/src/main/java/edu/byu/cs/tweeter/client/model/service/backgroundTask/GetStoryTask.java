package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.io.IOException;
import java.util.List;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.GetFeedRequest;
import edu.byu.cs.tweeter.model.net.request.GetStoryRequest;
import edu.byu.cs.tweeter.model.net.response.GetFeedResponse;
import edu.byu.cs.tweeter.model.net.response.GetStoryResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that retrieves a page of statuses from a user's story.
 */
public class GetStoryTask extends PagedStatusTask {

    public GetStoryTask(AuthToken authToken, User targetUser, int limit, Status lastStatus,
                        Handler messageHandler) {
        super(authToken, targetUser, limit, lastStatus, messageHandler);
    }

    @Override
    protected Pair<List<Status>, Boolean> getItems() {
        ServerFacade serverFacade = new ServerFacade();
        GetStoryRequest request = new GetStoryRequest(authToken, targetUser.getAlias(), getLimit(), getLastItem());
        GetStoryResponse response = null;
        try{
            response = serverFacade.getStory(request,"/getstory");
        }catch (TweeterRemoteException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
        Pair<List<Status>, Boolean> page = new Pair<>(response.getStatuses(), response.getHasMorePages());
        return page;
    }
}
