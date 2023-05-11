package edu.byu.cs.tweeter.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.GetFollowingCountRequest;
import edu.byu.cs.tweeter.model.net.response.GetFollowingCountResponse;

public class GetFollowingCount {
    private ServerFacade serverFacade;
    private GetFollowingCountRequest request;
    private GetFollowingCountResponse response;
    private AuthToken authToken;

    @BeforeEach
    public void setup() {
        authToken = new AuthToken();
        request = new GetFollowingCountRequest(authToken,"@allen");

        serverFacade = Mockito.spy(new ServerFacade());
    }

    @Test
    public void testRequest() {
        try{
            response = serverFacade.getFollowingCount(request, "/getfollowingcount");
        }catch (TweeterRemoteException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
        Assertions.assertTrue(response.getSuccess());
    }

}
