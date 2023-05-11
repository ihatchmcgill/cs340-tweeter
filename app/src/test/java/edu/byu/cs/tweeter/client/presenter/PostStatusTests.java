package edu.byu.cs.tweeter.client.presenter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.observer.SimpleNotificationObserver;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;

public class PostStatusTests {

    private GetMainPresenter.MainView mockMainView;
    private FollowService mockFollowService;
    private GetMainPresenter mainPresenterSpy;

    private String post = "This is an integration test";
    private String testUsername = "@isaac";
    private String testPassword = "test";

    private Cache mockCache;
    private ServerFacade server;

    private LoginRequest loginRequest;
    private LoginResponse loginResponse;
    @BeforeEach
    public void setup() {
        // Called before each test, set up any common code between tests
        mockMainView = Mockito.mock(GetMainPresenter.MainView.class);
        mockFollowService = Mockito.mock(FollowService.class);


        mainPresenterSpy = Mockito.spy(new GetMainPresenter(mockMainView));
        Mockito.doReturn(mockFollowService).when(mainPresenterSpy).getNewFollowService();

        server = new ServerFacade();
        Cache.setInstance(mockCache);

        loginRequest = new LoginRequest(testUsername, testPassword);
    }

    @Test
    public void testPostSuccess() throws IOException, TweeterRemoteException {
        //Post a status

        Answer<Void> answer = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                SimpleNotificationObserver observer = invocation.getArgument(1, SimpleNotificationObserver.class);
                observer.handleSuccess();
                return null;
            }
        };

        Mockito.doAnswer(answer).when(mockFollowService).postStatus(Mockito.any(),Mockito.any());

        mainPresenterSpy.postStatus(post);
        Mockito.verify(mockMainView).displayMessage("Posting Status...");
        Mockito.verify(mockMainView).cancelToast();
        Mockito.verify(mockMainView).displayMessage("Successfully Posted!");
    }
    @Test
    public void testPostFailed() {
        Answer<Void> answer = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                SimpleNotificationObserver observer = invocation.getArgument(1, SimpleNotificationObserver.class);
                observer.displayError("Error message");
                return null;
            }
        };
        Mockito.doAnswer(answer).when(mockFollowService).postStatus(Mockito.any(),Mockito.any());

        mainPresenterSpy.postStatus(post);
        Mockito.verify(mockMainView).displayMessage("Posting Status...");
        Mockito.verify(mockMainView).displayMessage("Failed to post status: Error message");
    }
    @Test
    public void testPostException() {
        Answer<Void> answer = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                SimpleNotificationObserver observer = invocation.getArgument(1, SimpleNotificationObserver.class);
                observer.displayException(new Exception("Exception Message"));
                return null;
            }
        };
        Mockito.doAnswer(answer).when(mockFollowService).postStatus(Mockito.any(),Mockito.any());

        mainPresenterSpy.postStatus(post);
        Mockito.verify(mockMainView).displayMessage("Posting Status...");
        Mockito.verify(mockMainView).displayMessage("Failed to post status because of exception: Exception Message");
    }
    @Test
    public void testParams(){
        Answer<Void> answer = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Status status = invocation.getArgument(0, Status.class);
                SimpleNotificationObserver observer = invocation.getArgument(1, SimpleNotificationObserver.class);
                Assertions.assertTrue(status.getClass()  == Status.class);
                Assertions.assertTrue(observer.getClass() == GetMainPresenter.GetPostStatusObserver.class);
                return null;
            }
        };
        Mockito.doAnswer(answer).when(mockFollowService).postStatus(Mockito.any(),Mockito.any());
        mainPresenterSpy.postStatus(post);

    }
}
