package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.observer.StartUserActivityObserver;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class AuthenticatePresenter extends Presenter<AuthenticatePresenter.AuthenticateView>{
    public UserService userService;

    public AuthenticatePresenter(AuthenticateView view) {
        super(view);
        userService = new UserService();
    }

    public interface AuthenticateView extends View{
        void startActivity(User user);
    }

    public UserService getNewUserService(){
        if(userService == null){
            userService = new UserService();
        }
        return userService;
    }

    public class GetUserObserver implements StartUserActivityObserver {
        @Override
        public void displayError(String message) {
            showError(message);
        }
        @Override
        public void displayException(Exception ex) {
            showException(ex);
        }
        @Override
        public void startUserActivity(User user) {
            view.startActivity(user);
        }
    }

    protected abstract void showException(Exception ex);

    protected abstract void showError(String message);

}
