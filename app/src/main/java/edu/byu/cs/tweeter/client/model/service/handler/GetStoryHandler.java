package edu.byu.cs.tweeter.client.model.service.handler;

import edu.byu.cs.tweeter.client.model.service.observer.ListObserver;
import edu.byu.cs.tweeter.model.domain.Status;

public class GetStoryHandler extends ListHandler<Status> {

    public GetStoryHandler(ListObserver observer) {
        super(observer);
    }
}
