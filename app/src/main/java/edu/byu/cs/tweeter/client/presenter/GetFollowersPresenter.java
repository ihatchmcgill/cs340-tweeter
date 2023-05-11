package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.observer.ListObserver;
import edu.byu.cs.tweeter.model.domain.User;

public class GetFollowersPresenter extends PagedPresenter<User>{
    private FollowService followService;

    public GetFollowersPresenter(PagedView<User> view){
        super(view);
        followService = new FollowService();
    }


    @Override
    protected void serviceAddItems(User user, int PAGE_SIZE, User lastItem, ListObserver<User> observer) {
        followService.loadMoreFollowers(user, PAGE_SIZE, lastItem, observer);
    }

    @Override
    protected void showError(String message) {
        view.displayMessage("Failed to get followers: " + message);
    }

    @Override
    protected void showException(Exception ex) {
        view.displayMessage("Failed to get followers because of exception: " + ex.getMessage());
    }
}
