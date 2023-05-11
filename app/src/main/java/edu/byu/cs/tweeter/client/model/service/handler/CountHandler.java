package edu.byu.cs.tweeter.client.model.service.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetCountTask;
import edu.byu.cs.tweeter.client.model.service.observer.CountObserver;

public abstract class CountHandler extends BackgroundTaskHandler<CountObserver> {
    public CountHandler(CountObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccess(Bundle data, CountObserver observer) {
        int count = data.getInt(GetCountTask.COUNT_KEY);
        observer.displayCount(count);
    }
}
