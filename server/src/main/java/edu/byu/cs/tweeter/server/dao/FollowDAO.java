package edu.byu.cs.tweeter.server.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.Follow;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowerRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.FollowerResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.JsonSerializer;
import edu.byu.cs.tweeter.server.bean.DataPage;
import edu.byu.cs.tweeter.server.bean.FollowItem;
import edu.byu.cs.tweeter.server.dao.interfaces.FollowDaoInterface;
import edu.byu.cs.tweeter.util.Pair;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteResult;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

/**
 * A DAO for accessing 'following' data from the database.
 */
public class FollowDAO implements FollowDaoInterface {

    private static final String TableName = "follows";
    public static final String IndexName = "follows_index";

    private static final String FollowersAttr = "follower_handle";
    private static final String FolloweesAttr = "followee_handle";

    private DynamoDbEnhancedClient enhancedClient;

    public FollowDAO(DynamoDbEnhancedClient enhancedClient){
        this.enhancedClient = enhancedClient;
    }



    private static boolean isNonEmptyString(String value) {
        return (value != null && value.length() > 0);
    }


    /**
     * Gets the users from the database that the user specified in the request is following. Uses
     * information in the request object to limit the number of followees returned and to return the
     * next set of followees after any that were returned in a previous request. The current
     * implementation returns generated data and doesn't actually access a database.
     *
     * @param request contains information about the user whose followees are to be returned and any
     *                other information required to satisfy the request.
     * @return the followees.
     */
    @Override
    public FollowingResponse getFollowees(FollowingRequest request) {
        DynamoDbTable<FollowItem> table = enhancedClient.table(TableName, TableSchema.fromBean(FollowItem.class));
        Key key = Key.builder()
                .partitionValue(request.getFollowerAlias())
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .limit(request.getLimit())
                .scanIndexForward(true);

        if(isNonEmptyString(request.getLastFolloweeAlias())) {
            // Build up the Exclusive Start Key (telling DynamoDB where you left off reading items)
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(FollowersAttr, AttributeValue.builder().s(request.getFollowerAlias()).build());
            startKey.put(FolloweesAttr, AttributeValue.builder().s(request.getLastFolloweeAlias()).build());

            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest queryRequest = requestBuilder.build();

        DataPage<User> result = new DataPage<>();

        PageIterable<FollowItem> pages = table.query(queryRequest);
        pages.stream()
                .limit(1)
                .forEach((Page<FollowItem> page) -> {
                    result.setHasMorePages(page.lastEvaluatedKey() != null);
                    page.items().forEach(item -> {
                        User followee = JsonSerializer.deserialize(item.getFollowee_user(), User.class);
                        result.getValues().add(followee);
                    });
                });

        return new FollowingResponse(result.getValues(), result.getHasMorePages());
    }

    @Override
    public FollowerResponse getFollowers(FollowerRequest request) {
        DynamoDbIndex<FollowItem> index = enhancedClient.table(TableName, TableSchema.fromBean(FollowItem.class)).index(IndexName);
        Key key = Key.builder()
                .partitionValue(request.getFolloweeAlias())
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .limit(request.getLimit())
                .scanIndexForward(true);

        if(isNonEmptyString(request.getLastFollowerAlias())) {
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(FolloweesAttr, AttributeValue.builder().s(request.getFolloweeAlias()).build());
            startKey.put(FollowersAttr, AttributeValue.builder().s(request.getLastFollowerAlias()).build());

            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest queryRequest = requestBuilder.build();

        DataPage<User> result = new DataPage<>();

        SdkIterable<Page<FollowItem>> sdkIterable = index.query(queryRequest);
        PageIterable<FollowItem> pages = PageIterable.create(sdkIterable);
        pages.stream()
                .limit(1)
                .forEach((Page<FollowItem> page) -> {
                    result.setHasMorePages(page.lastEvaluatedKey() != null);
                    page.items().forEach(item -> {
                        User follower = JsonSerializer.deserialize(item.getFollower_user(), User.class);
                        result.getValues().add(follower);
                    });
                });

        return new FollowerResponse(result.getValues(), result.getHasMorePages());
    }


    @Override
    public UnfollowResponse unfollow(UnfollowRequest request, String followerAlias) {
        DynamoDbTable<FollowItem> table = enhancedClient.table(TableName, TableSchema.fromBean(FollowItem.class));
        Key key = Key.builder()
                .partitionValue(followerAlias).sortValue(request.getFolloweeAlias())
                .build();

        FollowItem item = table.getItem(key);
        if(item != null){
            table.deleteItem(item);
            return new UnfollowResponse(true);
        }else{
            return new UnfollowResponse(false);
        }

    }

    @Override
    public FollowResponse follow(FollowRequest request, User followerUser, User followeeUser) {
        DynamoDbTable<FollowItem> table = enhancedClient.table(TableName, TableSchema.fromBean(FollowItem.class));
        Key key = Key.builder()
                .partitionValue(followerUser.getAlias()).sortValue(request.getFolloweeAlias())
                .build();

        FollowItem item = table.getItem(key);
        if(item == null){
            FollowItem itemToAdd = new FollowItem();
            itemToAdd.setFollower_handle(followerUser.getAlias());
            itemToAdd.setFollower_user(JsonSerializer.serialize(followerUser));
            itemToAdd.setFollowee_handle(request.getFolloweeAlias());
            itemToAdd.setFollowee_user(JsonSerializer.serialize(followeeUser));
            table.putItem(itemToAdd);
            return new FollowResponse(true);
        }else{
            //already following
            return new FollowResponse(false);
        }

    }

    @Override
    public IsFollowerResponse isFollower(IsFollowerRequest request) {
        DynamoDbTable<FollowItem> table = enhancedClient.table(TableName, TableSchema.fromBean(FollowItem.class));
        Key key = Key.builder()
                .partitionValue(request.getFollowerAlias()).sortValue(request.getFolloweeAlias())
                .build();

        FollowItem item = table.getItem(key);
        System.out.println(item);
        return new IsFollowerResponse(true, item != null);
    }

    @Override
    public DataPage<String> getFollowersAliases(String followeeAlias, String lastFollowerAlias) {
        DynamoDbIndex<FollowItem> index = enhancedClient.table(TableName, TableSchema.fromBean(FollowItem.class)).index(IndexName);
        Key key = Key.builder()
                .partitionValue(followeeAlias)
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .limit(100)
                .scanIndexForward(true);

        if(isNonEmptyString(lastFollowerAlias)) {
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(FolloweesAttr, AttributeValue.builder().s(followeeAlias).build());
            startKey.put(FollowersAttr, AttributeValue.builder().s(lastFollowerAlias).build());

            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest queryRequest = requestBuilder.build();

        DataPage<String> result = new DataPage<>();

        SdkIterable<Page<FollowItem>> sdkIterable = index.query(queryRequest);
        PageIterable<FollowItem> pages = PageIterable.create(sdkIterable);
        pages.stream()
                .limit(1)
                .forEach((Page<FollowItem> page) -> {
                    result.setHasMorePages(page.lastEvaluatedKey() != null);
                    page.items().forEach(item -> {
                        result.getValues().add(item.getFollower_handle());
                    });
                });

        return result;
    }

    @Override
    public void addFollowersBatch(Map<String, User> followersMap, User followTargetUser) {
        List<FollowItem> batchToWrite = new ArrayList<>();
        for (Map.Entry<String,User> item : followersMap.entrySet()) {
            FollowItem dto = new FollowItem();
            dto.setFollowee_handle(followTargetUser.getAlias());
            dto.setFollowee_user(JsonSerializer.serialize(followTargetUser));
            dto.setFollower_handle(item.getKey());
            dto.setFollower_user(JsonSerializer.serialize(item.getValue()));
            batchToWrite.add(dto);

            if (batchToWrite.size() == 25) {
                // package this batch up and send to DynamoDB.
                writeChunkOfFollowDTOs(batchToWrite);
                batchToWrite = new ArrayList<>();
            }
        }

        // write any remaining
        if (batchToWrite.size() > 0) {
            // package this batch up and send to DynamoDB.
            writeChunkOfFollowDTOs(batchToWrite);
        }
    }

    private void writeChunkOfFollowDTOs(List<FollowItem> followItems) {
        if(followItems.size() > 25)
            throw new RuntimeException("Too many follows to write");

        DynamoDbTable<FollowItem> table = enhancedClient.table(TableName, TableSchema.fromBean(FollowItem.class));
        WriteBatch.Builder<FollowItem> writeBuilder = WriteBatch.builder(FollowItem.class).mappedTableResource(table);
        for (FollowItem item : followItems) {
            writeBuilder.addPutItem(builder -> builder.item(item));
        }
        BatchWriteItemEnhancedRequest batchWriteItemEnhancedRequest = BatchWriteItemEnhancedRequest.builder()
                .writeBatches(writeBuilder.build()).build();

        try {
            BatchWriteResult result = enhancedClient.batchWriteItem(batchWriteItemEnhancedRequest);

            // just hammer dynamodb again with anything that didn't get written this time
            if (result.unprocessedPutItemsForTable(table).size() > 0) {
                writeChunkOfFollowDTOs(result.unprocessedPutItemsForTable(table));
            }

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
