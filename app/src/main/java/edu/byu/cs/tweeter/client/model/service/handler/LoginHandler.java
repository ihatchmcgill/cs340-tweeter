package edu.byu.cs.tweeter.client.model.service.handler;

import edu.byu.cs.tweeter.client.model.service.observer.StartUserActivityObserver;

public class LoginHandler extends RegisterAndLoginHandler {
    public LoginHandler(StartUserActivityObserver observer) {
        super(observer);
    }
}
