package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.GetFollowerCountResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowingCountResponse;
import edu.byu.cs.tweeter.server.bean.UserItem;
import edu.byu.cs.tweeter.server.dao.interfaces.UserDaoInterface;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteResult;
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

public class UserDAO implements UserDaoInterface {
    private static final String TableName = "user";

    private DynamoDbEnhancedClient enhancedClient;

    public UserDAO(DynamoDbEnhancedClient enhancedClient){
        this.enhancedClient = enhancedClient;
    }

    /**
     * Gets the count of users from the database that the user specified is following. The
     * current implementation uses generated data and doesn't actually access a database.
     *
     * @param followerAlias the User whose count of how many following is desired.
     * @return said count.
     */
    @Override
    public GetFollowingCountResponse getFolloweeCount(String followerAlias) {
        assert followerAlias != null;
        DynamoDbTable<UserItem> table = enhancedClient.table(TableName, TableSchema.fromBean(UserItem.class));
        Key key = Key.builder()
                .partitionValue(followerAlias).build();

        UserItem userItem = table.getItem(key);
        return new GetFollowingCountResponse(userItem.getNum_following());
    }

    @Override
    public GetFollowerCountResponse getFollowerCount(String followeeAlias) {
        assert followeeAlias != null;
        DynamoDbTable<UserItem> table = enhancedClient.table(TableName, TableSchema.fromBean(UserItem.class));
        Key key = Key.builder()
                .partitionValue(followeeAlias).build();

        UserItem userItem = table.getItem(key);
        return new GetFollowerCountResponse(userItem.getNum_followers());
    }
    @Override
    public User getUser(String username) {
        assert username != null;
        DynamoDbTable<UserItem> table = enhancedClient.table(TableName, TableSchema.fromBean(UserItem.class));
        Key key = Key.builder()
                .partitionValue(username).build();

        UserItem userItem = table.getItem(key);
        return new User(userItem.getFirst_name(), userItem.getLast_name(), userItem.getUserAlias(), userItem.getImage_url());
    }

    @Override
    public User registerUser(RegisterRequest request, String passwordHash) {
        assert request != null;
        assert passwordHash != null;
        System.out.println("register function called");
        DynamoDbTable<UserItem> table = enhancedClient.table(TableName, TableSchema.fromBean(UserItem.class));
        Key key = Key.builder()
                .partitionValue(request.getUsername()).build();

        UserItem item = table.getItem(key);
        System.out.println("Checking if item is null");
        System.out.println("item: " + item);
        if(item == null) {
            System.out.println("Item is null");
            // Create AmazonS3 object for doing S3 operations
            AmazonS3 s3 = AmazonS3ClientBuilder
                    .standard()
                    .withRegion("us-west-2")
                    .build();

            byte[] byteArray = Base64.getDecoder().decode(request.getImage());

            ObjectMetadata data = new ObjectMetadata();
            data.setContentLength(byteArray.length);
            data.setContentType("image/jpeg");
            PutObjectRequest s3Request = new PutObjectRequest("tweeterprofilepicturebucket", request.getUsername(), new ByteArrayInputStream(byteArray), data).withCannedAcl(CannedAccessControlList.PublicRead);
            System.out.println("putting in s3 bucket");
            s3.putObject(s3Request);

            String link = "https://tweeterprofilepicturebucket.s3.us-west-2.amazonaws.com/" + request.getUsername();

            UserItem newItem = new UserItem();
            newItem.setFirst_name(request.getFirstName());
            newItem.setLast_name(request.getLastName());
            newItem.setUserAlias(request.getUsername());
            newItem.setPassword_hash(passwordHash);
            newItem.setNum_followers(0);
            newItem.setNum_following(0);
            newItem.setImage_url(link);
            table.putItem(newItem);

            System.out.println("Returning new user");
            return new User(request.getFirstName(), request.getLastName(), request.getUsername(), link);
        }
        else{
            System.out.println("Item not null, returning null");
            return null;
        }
    }

    @Override
    public boolean isValidPassword(String alias, String hashPassword) {
        DynamoDbTable<UserItem> table = enhancedClient.table(TableName, TableSchema.fromBean(UserItem.class));
        Key key = Key.builder()
                .partitionValue(alias)
                .build();

        UserItem item = table.getItem(key);
        System.out.println(item);
        if(item.getPassword_hash().equals(hashPassword)){
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public void incrementFollowerCount(String followeeAlias) {
        DynamoDbTable<UserItem> table = enhancedClient.table(TableName, TableSchema.fromBean(UserItem.class));
        Key key = Key.builder()
                .partitionValue(followeeAlias)
                .build();

        UserItem itemToUpdate = table.getItem(key);
        itemToUpdate.setNum_followers(itemToUpdate.getNum_followers() + 1);
        table.updateItem(itemToUpdate);
    }

    @Override
    public void decrementFollowerCount(String followeeAlias) {
        DynamoDbTable<UserItem> table = enhancedClient.table(TableName, TableSchema.fromBean(UserItem.class));
        Key key = Key.builder()
                .partitionValue(followeeAlias)
                .build();

        UserItem itemToUpdate = table.getItem(key);
        itemToUpdate.setNum_followers(itemToUpdate.getNum_followers() - 1);
        table.updateItem(itemToUpdate);
    }

    @Override
    public void incrementFollowingCount(String followerAlias) {
        DynamoDbTable<UserItem> table = enhancedClient.table(TableName, TableSchema.fromBean(UserItem.class));
        Key key = Key.builder()
                .partitionValue(followerAlias)
                .build();

        UserItem itemToUpdate = table.getItem(key);
        itemToUpdate.setNum_following(itemToUpdate.getNum_following() + 1);
        table.updateItem(itemToUpdate);
    }

    @Override
    public void decrementFollowingCount(String followerAlias) {
        DynamoDbTable<UserItem> table = enhancedClient.table(TableName, TableSchema.fromBean(UserItem.class));
        Key key = Key.builder()
                .partitionValue(followerAlias)
                .build();

        UserItem itemToUpdate = table.getItem(key);
        itemToUpdate.setNum_following(itemToUpdate.getNum_following() - 1);
        table.updateItem(itemToUpdate);
    }

    public void addUserBatch(List<User> users) {
        List<UserItem> batchToWrite = new ArrayList<>();
        for (User u : users) {
            UserItem dto = new UserItem();
            dto.setUserAlias(u.getAlias());
            dto.setFirst_name(u.getFirstName());
            dto.setLast_name(u.getLastName());
            //Hash for password 'test'
            dto.setPassword_hash("098f6bcd4621d373cade4e832627b4f6");
            dto.setImage_url(u.getImageUrl());
            dto.setNum_followers(0);
            dto.setNum_following(1);

            batchToWrite.add(dto);

            if (batchToWrite.size() == 25) {
                // package this batch up and send to DynamoDB.
                writeChunkOfUserDTOs(batchToWrite);
                batchToWrite = new ArrayList<>();
            }
        }

        // write any remaining
        if (batchToWrite.size() > 0) {
            // package this batch up and send to DynamoDB.
            writeChunkOfUserDTOs(batchToWrite);
        }
    }
    private void writeChunkOfUserDTOs(List<UserItem> userDTOs) {
        if(userDTOs.size() > 25)
            throw new RuntimeException("Too many users to write");

        DynamoDbTable<UserItem> table = enhancedClient.table(TableName, TableSchema.fromBean(UserItem.class));
        WriteBatch.Builder<UserItem> writeBuilder = WriteBatch.builder(UserItem.class).mappedTableResource(table);
        for (UserItem item : userDTOs) {
            writeBuilder.addPutItem(builder -> builder.item(item));
        }
        BatchWriteItemEnhancedRequest batchWriteItemEnhancedRequest = BatchWriteItemEnhancedRequest.builder()
                .writeBatches(writeBuilder.build()).build();

        try {
            BatchWriteResult result = enhancedClient.batchWriteItem(batchWriteItemEnhancedRequest);

            // just hammer dynamodb again with anything that didn't get written this time
            if (result.unprocessedPutItemsForTable(table).size() > 0) {
                writeChunkOfUserDTOs(result.unprocessedPutItemsForTable(table));
            }

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

}
