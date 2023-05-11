package edu.byu.cs.tweeter.client.model.service.handler;

import edu.byu.cs.tweeter.client.model.service.observer.SimpleNotificationObserver;

public class LogoutHandler extends SimpleNotificationHandler {

    public LogoutHandler(SimpleNotificationObserver observer) {
        super(observer);
    }
}
