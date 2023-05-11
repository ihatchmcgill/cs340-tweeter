package edu.byu.cs.tweeter.client.model.service.handler;

import edu.byu.cs.tweeter.client.model.service.observer.SimpleNotificationObserver;

public class FollowHandler extends SimpleNotificationHandler {

    public FollowHandler(SimpleNotificationObserver observer) {
        super(observer);
    }
}
