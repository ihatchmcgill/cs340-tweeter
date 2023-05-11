package edu.byu.cs.tweeter.server.dao.interfaces;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.GetFeedRequest;
import edu.byu.cs.tweeter.model.net.response.GetFeedResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;

public interface FeedDaoInterface {
    GetFeedResponse getFeed(GetFeedRequest request);
    void postStatusInFeed(List<String> followers, Status status);
}
