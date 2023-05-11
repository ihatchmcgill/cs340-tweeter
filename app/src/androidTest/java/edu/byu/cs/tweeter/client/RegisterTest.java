package edu.byu.cs.tweeter.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;

public class RegisterTest {
    private ServerFacade serverFacade;
    private RegisterRequest request;
    private RegisterResponse response;

    private String firstName = "isaac";
    private String lastName = "mcgill";
    private String username = "@toast";
    private String password = "hash";
    private String image = "image";

    @BeforeEach
    public void setup() {
        request = new RegisterRequest(username,password,firstName,lastName,image);

        serverFacade = Mockito.spy(new ServerFacade());
    }

    @Test
    public void testRequest() {
        try{
            response = serverFacade.register(request, "/register");
        }catch (TweeterRemoteException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
        Assertions.assertTrue(response.getSuccess());
    }
}
