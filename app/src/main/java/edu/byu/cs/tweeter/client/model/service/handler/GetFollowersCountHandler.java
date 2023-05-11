package edu.byu.cs.tweeter.client.model.service.handler;

import edu.byu.cs.tweeter.client.model.service.observer.CountObserver;


public class GetFollowersCountHandler extends CountHandler {
    public GetFollowersCountHandler(CountObserver observer) {
        super(observer);
    }
}
