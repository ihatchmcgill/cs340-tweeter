package edu.byu.cs.tweeter.client.model.service.handler;

import edu.byu.cs.tweeter.client.model.service.observer.ListObserver;
import edu.byu.cs.tweeter.model.domain.User;

public class GetFollowersHandler extends ListHandler<User> {

    public GetFollowersHandler(ListObserver observer) {
        super(observer);
    }
}
