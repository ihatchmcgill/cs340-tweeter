package edu.byu.cs.tweeter.client.model.service.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.model.service.observer.StartUserActivityObserver;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * Message handler (i.e., observer) for GetUserTask.
 */
public class GetUserHandler extends BackgroundTaskHandler<StartUserActivityObserver> {
    public GetUserHandler(StartUserActivityObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccess(Bundle data, StartUserActivityObserver observer) {
        User user = (User) data.getSerializable(GetUserTask.USER_KEY);
        observer.startUserActivity(user);
    }
}