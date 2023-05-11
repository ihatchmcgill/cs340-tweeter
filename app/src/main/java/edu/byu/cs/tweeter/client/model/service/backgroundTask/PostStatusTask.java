package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;

/**
 * Background task that posts a new status sent by a user.
 */
public class PostStatusTask extends AuthenticatedTask {

    /**
     * The new status being sent. Contains all properties of the status,
     * including the identity of the user sending the status.
     */
    private final Status status;

    public PostStatusTask(AuthToken authToken, Status status, Handler messageHandler) {
        super(authToken, messageHandler);
        this.status = status;
    }

    @Override
    protected void runTask() {
        ServerFacade serverFacade = new ServerFacade();
        PostStatusRequest request = new PostStatusRequest(authToken, status);
        PostStatusResponse response = null;
        Log.e("test","Sending Request...");
        try{
            response = serverFacade.postStatus(request, "/poststatus");
        }catch (TweeterRemoteException e){
            e.printStackTrace();
            sendExceptionMessage(e);
        }catch(IOException e){
            e.printStackTrace();
            sendExceptionMessage(e);
        }
        Log.e("test","Got Response...");

        if(response.getSuccess()){
            sendSuccessMessage();
        }else{
            sendFailedMessage("Failed to Post Status");
        }
    }

}
