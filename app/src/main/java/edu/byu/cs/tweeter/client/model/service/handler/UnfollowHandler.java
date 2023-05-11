package edu.byu.cs.tweeter.client.model.service.handler;

import edu.byu.cs.tweeter.client.model.service.observer.SimpleNotificationObserver;

public class UnfollowHandler extends SimpleNotificationHandler {
    public UnfollowHandler(SimpleNotificationObserver observer) {
       super(observer);
    }
}