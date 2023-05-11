package edu.byu.cs.tweeter.client.model.service.handler;

import edu.byu.cs.tweeter.client.model.service.observer.StartUserActivityObserver;

public class RegisterHandler extends RegisterAndLoginHandler {
    public RegisterHandler(StartUserActivityObserver observer) {
        super(observer);
    }
}
