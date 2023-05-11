package edu.byu.cs.tweeter.client.model.service.handler;

import edu.byu.cs.tweeter.client.model.service.observer.CountObserver;

public class GetFollowingCountHandler extends CountHandler {

    public GetFollowingCountHandler(CountObserver observer) {
        super(observer);
    }
}
