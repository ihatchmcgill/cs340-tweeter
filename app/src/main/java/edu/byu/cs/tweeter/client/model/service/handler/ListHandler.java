package edu.byu.cs.tweeter.client.model.service.handler;

import android.os.Bundle;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.PagedTask;
import edu.byu.cs.tweeter.client.model.service.observer.ListObserver;

public abstract class ListHandler<T> extends BackgroundTaskHandler<ListObserver> {

    public ListHandler(ListObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccess(Bundle data, ListObserver observer) {
        List<T> items = (List<T>) data.getSerializable(PagedTask.ITEMS_KEY);
        boolean hasMorePages = data.getBoolean(PagedTask.MORE_PAGES_KEY);


        observer.addItems(items,hasMorePages);
    }
}
