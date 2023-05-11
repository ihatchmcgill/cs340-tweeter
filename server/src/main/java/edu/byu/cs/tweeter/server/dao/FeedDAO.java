package edu.byu.cs.tweeter.server.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetFeedRequest;
import edu.byu.cs.tweeter.model.net.response.GetFeedResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.server.JsonSerializer;
import edu.byu.cs.tweeter.server.bean.DataPage;
import edu.byu.cs.tweeter.server.bean.FeedItem;
import edu.byu.cs.tweeter.server.bean.UserItem;
import edu.byu.cs.tweeter.server.dao.interfaces.FeedDaoInterface;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
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

public class FeedDAO implements FeedDaoInterface {

    private static final String TableName = "feed";
    private static final String UserAttr = "followerAlias";
    private static final String TimestampAttr = "timestamp";

    private DynamoDbEnhancedClient enhancedClient;

    public FeedDAO(DynamoDbEnhancedClient enhancedClient){
        this.enhancedClient = enhancedClient;
    }
    /**
     * Gets the statuses from the database that the user specified in the request. Uses
     * information in the request object to limit the number of statuses returned and to return the
     * next set of followees after any that were returned in a previous request. The current
     * implementation returns generated data and doesn't actually access a database.
     *
     * @param request contains information about the user whose followees are to be returned and any
     *                other information required to satisfy the request.
     * @return the followees.
     */
    @Override
    public GetFeedResponse getFeed(GetFeedRequest request) {
        assert request.getLimit() > 0;
        assert request.getUserAlias() != null;

        DynamoDbTable<FeedItem> table = enhancedClient.table(TableName, TableSchema.fromBean(FeedItem.class));
        Key key = Key.builder()
                .partitionValue(request.getUserAlias())
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .limit(request.getLimit());


        if(request.getLastStatus() != null) {
            System.out.print("last status " + request.getLastStatus());
            // Build up the Exclusive Start Key (telling DynamoDB where you left off reading items)
            Map<String, AttributeValue> startKey = new HashMap<>();

            System.out.print("user " + request.getLastStatus().getUser().getAlias());
            System.out.print("timestamp " + request.getLastStatus().getTimestamp());

            //Start key built with user of last status posted and timestamp
            startKey.put(UserAttr, AttributeValue.builder().s(request.getUserAlias()).build());
            startKey.put(TimestampAttr, AttributeValue.builder().n(String.valueOf(request.getLastStatus().getTimestamp())).build());


            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest queryRequest = requestBuilder.build();
        DataPage<Status> result = new DataPage<>();


        PageIterable<FeedItem> pages = table.query(queryRequest);
        pages.stream()
                .limit(1)
                .forEach((Page<FeedItem> page) -> {
                    result.setHasMorePages(page.lastEvaluatedKey() != null);
                    page.items().forEach(item -> {
                        Status statusToAdd = JsonSerializer.deserialize(item.getStatus(), Status.class);
                        result.getValues().add(statusToAdd);
                    });
                });

        return new GetFeedResponse(result.getValues(), result.getHasMorePages());
    }

    @Override
    public void postStatusInFeed(List<String> followers, Status status) {
        addFeedBatch(followers, status);
    }

    private void addFeedBatch(List<String> followers, Status status) {
        List<FeedItem> batchToWrite = new ArrayList<>();
        for (String follower : followers) {
            FeedItem dto = new FeedItem();
            dto.setFollowerAlias(follower);
            dto.setStatus(JsonSerializer.serialize(status));
            dto.setTimestamp(status.getTimestamp());

            batchToWrite.add(dto);

            if (batchToWrite.size() == 25) {
                // package this batch up and send to DynamoDB.
                writeChunkOfFeedDTOs(batchToWrite);
                batchToWrite = new ArrayList<>();
            }
        }

        // write any remaining
        if (batchToWrite.size() > 0) {
            // package this batch up and send to DynamoDB.
            writeChunkOfFeedDTOs(batchToWrite);
        }
    }
    private void writeChunkOfFeedDTOs(List<FeedItem> feedItems) {
        if(feedItems.size() > 25)
            throw new RuntimeException("Too many status' to write");

        DynamoDbTable<FeedItem> table = enhancedClient.table(TableName, TableSchema.fromBean(FeedItem.class));
        WriteBatch.Builder<FeedItem> writeBuilder = WriteBatch.builder(FeedItem.class).mappedTableResource(table);
        for (FeedItem item : feedItems) {
            writeBuilder.addPutItem(builder -> builder.item(item));
        }
        BatchWriteItemEnhancedRequest batchWriteItemEnhancedRequest = BatchWriteItemEnhancedRequest.builder()
                .writeBatches(writeBuilder.build()).build();

        try {
            BatchWriteResult result = enhancedClient.batchWriteItem(batchWriteItemEnhancedRequest);

            // just hammer dynamodb again with anything that didn't get written this time
            if (result.unprocessedPutItemsForTable(table).size() > 0) {
                writeChunkOfFeedDTOs(result.unprocessedPutItemsForTable(table));
            }

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
