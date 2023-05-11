package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.observer.ListObserver;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class GetFeedPresenter extends PagedPresenter<Status>{
    private StatusService statusService;

    public GetFeedPresenter(PagedView<Status> view) {
        super(view);
        statusService = new StatusService(); 
    }

    @Override
    protected void serviceAddItems(User user, int PAGE_SIZE, Status lastItem, ListObserver<Status> observer) {
        statusService.addMoreItems(user, PAGE_SIZE, lastItem, observer);
    }

    @Override
    protected void showError(String message) {
        view.displayMessage("Failed to get feed: " + message);
    }

    @Override
    protected void showException(Exception ex) {
        view.displayMessage("Failed to get feed because of exception: " + ex.getMessage());
    }

}
