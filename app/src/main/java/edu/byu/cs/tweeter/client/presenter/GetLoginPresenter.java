package edu.byu.cs.tweeter.client.presenter;

import android.text.Editable;

public class GetLoginPresenter extends AuthenticatePresenter{

    public GetLoginPresenter(AuthenticatePresenter.AuthenticateView view){
        super(view);
    }

    @Override
    protected void showException(Exception ex) {
        view.displayMessage("Failed to login because of exception: " + ex.getMessage());
    }

    @Override
    protected void showError(String message) {
        view.displayMessage("Failed to login: " + message);
    }

    public void loginTask(String alias, String password) {
        getNewUserService().loginUser(alias,password,new GetUserObserver());
    }

    public void validateLogin(Editable alias, Editable password) {
        if (alias.length() > 0 && alias.charAt(0) != '@') {
            throw new IllegalArgumentException("Alias must begin with @.");
        }
        if (alias.length() < 2) {
            throw new IllegalArgumentException("Alias must contain 1 or more characters after the @.");
        }
        if (password.length() == 0) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }
    }
}
