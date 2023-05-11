package edu.byu.cs.tweeter.client.model.service.observer;

public interface ServiceObserver {
    void displayError(String message);
    void displayException(Exception ex);
}
