package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;

import edu.byu.cs.tweeter.server.JsonSerializer;
import edu.byu.cs.tweeter.server.bean.DataPage;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.service.FollowService;
import edu.byu.cs.tweeter.server.sqs.PostStatusMessage;
import edu.byu.cs.tweeter.server.sqs.UpdateFeedMessage;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class PostStatusQueueHandler implements RequestHandler<SQSEvent, Void> {

    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
                .region(Region.US_WEST_2)
                .build();

        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();

        FollowDAO followDAO = new FollowDAO(enhancedClient);
        FollowService followService = new FollowService(followDAO, null, null);

        String queueUrl = "https://sqs.us-west-2.amazonaws.com/421541340110/updateFeedQueue";
        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();

        for (SQSEvent.SQSMessage msg : event.getRecords()) {
            PostStatusMessage postStatusObject = JsonSerializer.deserialize(msg.getBody(), PostStatusMessage.class);
            //Get new message
            DataPage<String> followerAliases = followService.getFollowersAliases(postStatusObject.getAuthorAlias(), null);

            UpdateFeedMessage updateFeedObject = new UpdateFeedMessage(followerAliases.getValues(), postStatusObject.getStatus());



            //Send first message
            String messageBody = JsonSerializer.serialize(updateFeedObject);
            SendMessageRequest send_msg_request = new SendMessageRequest()
                    .withQueueUrl(queueUrl)
                    .withMessageBody(messageBody);
            SendMessageResult send_msg_result = sqs.sendMessage(send_msg_request);


            //Loop through the rest of the messages
            while(followerAliases.getHasMorePages()){
                //Get another page
                followerAliases = followService.getFollowersAliases(postStatusObject.getAuthorAlias(),
                        followerAliases.getValues().get(followerAliases.getValues().size() - 1));

                updateFeedObject = new UpdateFeedMessage(followerAliases.getValues(), postStatusObject.getStatus());


                //Send next message
                messageBody = JsonSerializer.serialize(updateFeedObject);
                send_msg_request = new SendMessageRequest()
                        .withQueueUrl(queueUrl)
                        .withMessageBody(messageBody);
                send_msg_result = sqs.sendMessage(send_msg_request);
            }

        }

        return null;
    }
}
