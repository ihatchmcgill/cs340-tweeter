package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;

import edu.byu.cs.tweeter.server.JsonSerializer;
import edu.byu.cs.tweeter.server.dao.FeedDAO;
import edu.byu.cs.tweeter.server.service.StatusService;
import edu.byu.cs.tweeter.server.sqs.UpdateFeedMessage;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class UpdateFeedQueueHandler implements RequestHandler<SQSEvent, Void> {

    @Override
    public Void handleRequest(SQSEvent event, Context context) {

        DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
                .region(Region.US_WEST_2)
                .build();

        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();

        FeedDAO feedDAO = new FeedDAO(enhancedClient);
        StatusService statusService = new StatusService(null, feedDAO, null, null);

        for (SQSEvent.SQSMessage msg : event.getRecords()) {
            UpdateFeedMessage updateFeedObject = JsonSerializer.deserialize(msg.getBody(), UpdateFeedMessage.class);
            statusService.postStatusInFeed(updateFeedObject.getFollowerAliases(), updateFeedObject.getStatus());
        }
        return null;
    }
}
