package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.observer.ListObserver;
import edu.byu.cs.tweeter.client.model.service.observer.StartUserActivityObserver;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class PagedPresenter<T> extends Presenter<PagedPresenter.PagedView> {
    public UserService userService;

    public T lastItem;
    public boolean hasMorePages;
    public boolean isLoading = false;
    public static final int PAGE_SIZE = 10;

    public boolean isLoading(){
        return isLoading;
    }
    public boolean hasMorePages(){
        return hasMorePages;
    }
    private void setHasMorePages(boolean hasMorePages) {
        this.hasMorePages = hasMorePages;
    }

    public PagedPresenter(PagedView view) {
        super(view);
        userService = new UserService();
    }

    public interface PagedView<T> extends View{
        void addItems(List<T> items);
        void setLoadingFooter(boolean value);
        void startUserActivity(User user);
    }

    public void getUserTask(String userAlias) {
        userService.getUserTask(userAlias, new GetUserObserver());
    }


    public class GetUserObserver implements StartUserActivityObserver {

        @Override
        public void displayError(String message) {
            view.displayMessage("Failed to get user's profile: " + message);
        }

        @Override
        public void displayException(Exception ex) {
            view.displayMessage("Failed to get user's profile because of exception: " + ex.getMessage());
        }

        @Override
        public void startUserActivity(User user) {
            view.startUserActivity(user);
        }
    }

    public void addMoreItems(User user) {
        if (!isLoading) {   // This guard is important for avoiding a race condition in the scrolling code.
            isLoading = true;
            view.setLoadingFooter(isLoading);
            serviceAddItems(user, PAGE_SIZE, lastItem, new GetPagedObserver());
        }
    }

    protected abstract void serviceAddItems(User user, int PAGE_SIZE, T lastItem, ListObserver<T> observer);

    public class GetPagedObserver implements ListObserver<T> {
        @Override
        public void displayError(String message) {
            isLoading = false;
            view.setLoadingFooter(isLoading);
            showError(message);
        }

        @Override
        public void displayException(Exception ex) {
            isLoading = false;
            view.setLoadingFooter(isLoading);
            showException(ex);

        }

        @Override
        public void addItems(List<T> items, boolean hasMorePages) {
            isLoading = false;
            view.setLoadingFooter(isLoading);
            setHasMorePages(hasMorePages);

            lastItem = (items.size() > 0) ? items.get(items.size() - 1) : null;
            view.addItems(items);
        }
    }

    protected abstract void showError(String message);
    protected abstract void showException(Exception ex);
}
