package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class Presenter<T extends Presenter.View> {
    protected T view;

    public interface View{
        void displayMessage(String message);
    }

    public Presenter(T view) {
        this.view = view;
    }

    public User getUser(){
        return Cache.getInstance().getCurrUser();
    }


}
