package edu.byu.cs.tweeter.client.model.service.handler;

import edu.byu.cs.tweeter.client.model.service.observer.SimpleNotificationObserver;

public class PostStatusHandler extends SimpleNotificationHandler {

    public PostStatusHandler(SimpleNotificationObserver observer) {
        super(observer);
    }
}
