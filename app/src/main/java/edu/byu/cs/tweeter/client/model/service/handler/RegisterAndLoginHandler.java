package edu.byu.cs.tweeter.client.model.service.handler;

import android.os.Bundle;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.AuthenticateTask;
import edu.byu.cs.tweeter.client.model.service.observer.StartUserActivityObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class RegisterAndLoginHandler extends BackgroundTaskHandler<StartUserActivityObserver> {

    public RegisterAndLoginHandler(StartUserActivityObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccess(Bundle data, StartUserActivityObserver observer) {
        User userForAction = (User) data.getSerializable(AuthenticateTask.USER_KEY);
        AuthToken authToken = (AuthToken) data.getSerializable(AuthenticateTask.AUTH_TOKEN_KEY);

        // Cache user session information
        Cache.getInstance().setCurrUser(userForAction);
        Cache.getInstance().setCurrUserAuthToken(authToken);

        observer.startUserActivity(userForAction);
    }
}
