package edu.byu.cs.tweeter.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.observer.SimpleNotificationObserver;
import edu.byu.cs.tweeter.client.presenter.GetMainPresenter;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.GetStoryRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.response.GetStoryResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.util.Pair;

public class PostStatusIntegrationTest {
    private GetMainPresenter.MainView mockMainView;
    private GetMainPresenter mainPresenterSpy;


    private String post = "This is an integration test";
    private String testUsername = "@integrationTest";
    private String testPassword = "test";

    private ServerFacade server;
    private CountDownLatch countDownLatch;

    private LoginRequest loginRequest;
    private LoginResponse loginResponse;
    private GetStoryRequest storyRequest;
    private GetStoryResponse storyResponse;

    @BeforeEach
    public void setup() {
        // Called before each test, set up any common code between tests
        mockMainView = Mockito.mock(GetMainPresenter.MainView.class);
        mainPresenterSpy = Mockito.spy(new GetMainPresenter(mockMainView));

        server = new ServerFacade();

        loginRequest = new LoginRequest(testUsername, testPassword);
        resetCountDownLatch();
    }

    private void resetCountDownLatch() {
        countDownLatch = new CountDownLatch(1);
    }

    private void awaitCountDownLatch() throws InterruptedException {
        countDownLatch.await();
        resetCountDownLatch();
    }


    @Test
    public void testPostSuccess() throws IOException, TweeterRemoteException, InterruptedException {
        //Login User In
        loginResponse = server.login(loginRequest, "/login");
        Assertions.assertTrue(loginResponse.getSuccess());

        Cache.getInstance().setCurrUser(loginResponse.getUser());
        Cache.getInstance().setCurrUserAuthToken(loginResponse.getAuthToken());

        storyRequest = new GetStoryRequest(Cache.getInstance().getCurrUserAuthToken(), Cache.getInstance().getCurrUser().getAlias(), 100, null);

        Answer<Void> answerView = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                countDownLatch.countDown();
                return null;
            }
        };
        Mockito.doAnswer(answerView).when(mockMainView).displayMessage(Mockito.any());

        //Posts Status
        mainPresenterSpy.postStatus(post);
        awaitCountDownLatch();

        Mockito.verify(mockMainView).displayMessage("Successfully Posted!");

        //Gets users stories from server
        storyResponse = server.getStory(storyRequest, "/getstory");
        Pair<List<Status>, Boolean> page = new Pair<>(storyResponse.getStatuses(), storyResponse.getHasMorePages());
        boolean matchedStatus = false;
        for(Status item : page.getFirst()){
            if(item.getPost().equals(post)){
                matchedStatus = true;
            }
        }
        Assertions.assertTrue(matchedStatus);
    }
}
