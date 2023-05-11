package edu.byu.cs.tweeter.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowerRequest;
import edu.byu.cs.tweeter.model.net.response.FollowerResponse;

public class GetFollowersTest {
    private ServerFacade serverFacade;
    private FollowerRequest request;
    private FollowerResponse response;
    private AuthToken authToken;

    @BeforeEach
    public void setup() {
        authToken = new AuthToken();
        request = new FollowerRequest(authToken,"@allen", 3, null);

        serverFacade = new ServerFacade();
    }

    @Test
    public void testRequest() {
        try{
            response = serverFacade.getFollowers(request, "/getfollowers");
        }catch (TweeterRemoteException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
        Assertions.assertTrue(response.getSuccess());
    }
}
