package edu.byu.cs.tweeter.server.dao;

import java.util.HashMap;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.GetStoryRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.GetStoryResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.server.JsonSerializer;
import edu.byu.cs.tweeter.server.bean.DataPage;
import edu.byu.cs.tweeter.server.bean.StoryItem;
import edu.byu.cs.tweeter.server.dao.interfaces.StoryDaoInterface;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class StoryDAO implements StoryDaoInterface {
    private DynamoDbEnhancedClient enhancedClient;

    private static final String TableName = "story";
    private static final String UserAttr = "senderAlias";
    private static final String TimestampAttr = "timestamp";

    public StoryDAO(DynamoDbEnhancedClient enhancedClient){
        this.enhancedClient = enhancedClient;
    }


    @Override
    public GetStoryResponse getStory(GetStoryRequest request) {
        assert request.getLimit() > 0;
        assert request.getUserAlias() != null;

        DynamoDbTable<StoryItem> table = enhancedClient.table(TableName, TableSchema.fromBean(StoryItem.class));
        Key key = Key.builder()
                .partitionValue(request.getUserAlias())
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .limit(request.getLimit());

        System.out.println("Getting stories with limit: " + request.getLimit());
        if(request.getLastStatus() != null) {
            System.out.println("Last status: " + request.getLastStatus());
            // Build up the Exclusive Start Key (telling DynamoDB where you left off reading items)
            Map<String, AttributeValue> startKey = new HashMap<>();

            //Start key built with user of last status posted and timestamp
            startKey.put(UserAttr, AttributeValue.builder().s(request.getUserAlias()).build());
            startKey.put(TimestampAttr, AttributeValue.builder().n(String.valueOf(request.getLastStatus().getTimestamp())).build());

            System.out.println("start key: " + startKey);
            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest queryRequest = requestBuilder.build();
        System.out.println("query "+ queryRequest);
        DataPage<Status> result = new DataPage<>();


        PageIterable<StoryItem> pages = table.query(queryRequest);
        pages.stream()
                .limit(1)
                .forEach((Page<StoryItem> page) -> {
                    result.setHasMorePages(page.lastEvaluatedKey() != null);
                    page.items().forEach(item -> {
                        Status statusToAdd = JsonSerializer.deserialize(item.getStatus(), Status.class);
                        result.getValues().add(statusToAdd);
                    });
                });

        System.out.println("Returning response with " + result.getHasMorePages());
        return new GetStoryResponse(result.getValues(), result.getHasMorePages());
    }

    @Override
    public PostStatusResponse postStatusInStory(PostStatusRequest request) {
        DynamoDbTable<StoryItem> table = enhancedClient.table(TableName, TableSchema.fromBean(StoryItem.class));
        StoryItem statusToAdd = new StoryItem();

        statusToAdd.setSenderAlias(request.getStatus().getUser().getAlias());
        statusToAdd.setSender_name(request.getStatus().getUser().getFirstName());
        statusToAdd.setStatus(JsonSerializer.serialize(request.getStatus()));
        statusToAdd.setTimestamp(request.getStatus().getTimestamp());

        table.putItem(statusToAdd);
        return new PostStatusResponse(true);
    }
}
