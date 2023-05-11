package edu.byu.cs.tweeter.client.model.service.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.model.service.observer.ButtonObserver;

public class IsFollowerHandler extends BackgroundTaskHandler<ButtonObserver> {

    public IsFollowerHandler(ButtonObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccess(Bundle data, ButtonObserver observer) {
        boolean isFollower = data.getBoolean(IsFollowerTask.IS_FOLLOWER_KEY);

        // If logged in user if a follower of the selected user, display the follow button as "following"
        if (isFollower) {
            observer.setFollowingButton();
        } else {
            observer.setFollowButton();
        }
    }
}