package edu.byu.cs.tweeter.server.service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.GetFeedRequest;
import edu.byu.cs.tweeter.model.net.request.GetStoryRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.GetFeedResponse;
import edu.byu.cs.tweeter.model.net.response.GetStoryResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.server.JsonSerializer;
import edu.byu.cs.tweeter.server.dao.AuthDAO;
import edu.byu.cs.tweeter.server.dao.FeedDAO;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.StoryDAO;
import edu.byu.cs.tweeter.server.sqs.PostStatusMessage;


//access Feed DAO and a Story DAO
public class StatusService{

    StoryDAO storyDAO;
    FeedDAO feedDAO;
    AuthDAO authDAO;
    FollowDAO followDAO;
    public StatusService(StoryDAO storyDAO, FeedDAO feedDAO, AuthDAO authDAO, FollowDAO followDAO){
        this.storyDAO = storyDAO;
        this.feedDAO = feedDAO;
        this.authDAO = authDAO;
        this.followDAO = followDAO;
    }
    /**
     * Returns the users that the user specified in the request is following. Uses information in
     * the request object to limit the number of followees returned and to return the next set of
     * followees after any that were returned in a previous request. Uses the {@link FollowDAO} to
     * get the followees.
     *
     * @param request contains the data required to fulfill the request.
     * @return the feed.
     */
    public GetFeedResponse getFeed(GetFeedRequest request) {
        if(request.getUserAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a user alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }

        if(authDAO.isAuthExpired(request.getAuthToken())){
            throw new RuntimeException("[Server Error] Expired AuthToken. Please re-login");
        }else{
            authDAO.refreshAuth(request.getAuthToken());
        }

        return feedDAO.getFeed(request);
    }

    public GetStoryResponse getStory(GetStoryRequest request) {
        if(request.getUserAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a user alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }

        if(authDAO.isAuthExpired(request.getAuthToken())){
            throw new RuntimeException("[Server Error] Expired AuthToken. Please re-login");
        }else{
            authDAO.refreshAuth(request.getAuthToken());
        }

        return storyDAO.getStory(request);
    }

    public PostStatusResponse postStatusInStory(PostStatusRequest request) {
        if(request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have an AuthToken");
        } else if(request.getStatus() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a Status");
        }


        if(authDAO.isAuthExpired(request.getAuthToken())){
            throw new RuntimeException("[Server Error] Expired AuthToken. Please re-login");
        }else{
            authDAO.refreshAuth(request.getAuthToken());
        }

        // No need to use SQS
        PostStatusResponse storyResponse = storyDAO.postStatusInStory(request);


        PostStatusMessage messageObject = new PostStatusMessage(request.getStatus(), request.getStatus().getUser().getAlias());
        String queueUrl = "https://sqs.us-west-2.amazonaws.com/421541340110/postStatusQueue";
        String messageBody = JsonSerializer.serialize(messageObject);

        System.out.println("Sending message to Queue");
        SendMessageRequest send_msg_request = new SendMessageRequest()
                .withQueueUrl(queueUrl)
                .withMessageBody(messageBody);
        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
        SendMessageResult send_msg_result = sqs.sendMessage(send_msg_request);
        System.out.println(send_msg_result.toString());

        return new PostStatusResponse(storyResponse.getSuccess());
    }

    public void postStatusInFeed(List<String> followers, Status status){
        feedDAO.postStatusInFeed(followers, status);
    }


}
