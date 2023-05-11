package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;

/**
 * Background task that logs out a user (i.e., ends a session).
 */
public class LogoutTask extends AuthenticatedTask {

    public LogoutTask(AuthToken authToken, Handler messageHandler) {
        super(authToken, messageHandler);
    }

    @Override
    protected void runTask() {
        ServerFacade serverFacade = new ServerFacade();
        LogoutRequest request = new LogoutRequest(authToken);
        LogoutResponse response = null;
        try{
            response = serverFacade.logout(request, "/logout");
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
            sendFailedMessage("Logout Failed");
        }

    }
}
