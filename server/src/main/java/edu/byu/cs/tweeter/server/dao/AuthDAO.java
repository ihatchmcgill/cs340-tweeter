package edu.byu.cs.tweeter.server.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.server.bean.AuthItem;
import edu.byu.cs.tweeter.server.dao.interfaces.AuthDaoInterface;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

public class AuthDAO implements AuthDaoInterface {
    private static final String TableName = "authtoken";

    private DynamoDbEnhancedClient enhancedClient;

    public AuthDAO(DynamoDbEnhancedClient enhancedClient){
        this.enhancedClient = enhancedClient;
    }

    @Override
    public void addAuthToken(AuthToken authToken, String userAlias) {
        assert authToken != null;
        assert userAlias != null;
        DynamoDbTable<AuthItem> table = enhancedClient.table(TableName, TableSchema.fromBean(AuthItem.class));

        AuthItem item = new AuthItem();
        item.setToken(authToken.getToken());
        item.setUser_alias(userAlias);
        //convert datetime string to long
        SimpleDateFormat f = new SimpleDateFormat("E MMM d k:mm:ss z y");
        long milliseconds = 0;
        try{
            Date d = f.parse(authToken.getDatetime());
            milliseconds = d.getTime();
        }catch (ParseException e){
            e.printStackTrace();
        }
        item.setDatetime(milliseconds);
        table.updateItem(item);
    }

    @Override
    public void deleteAuthToken(AuthToken authToken) {
        //delete authToken -> also delete all expired authTokens
        DynamoDbTable<AuthItem> table = enhancedClient.table(TableName, TableSchema.fromBean(AuthItem.class));
        Key key = Key.builder()
                .partitionValue(authToken.getToken())
                .build();

        AuthItem authToDelete = table.getItem(key);
        if(authToDelete != null){
            table.deleteItem(authToDelete);
        }

        //delete all other expired tokens
        deleteAllExpiredTokens();
    }

    @Override
    public boolean isAuthExpired(AuthToken authToken) {
        DynamoDbTable<AuthItem> table = enhancedClient.table(TableName, TableSchema.fromBean(AuthItem.class));
        Key key = Key.builder()
                .partitionValue(authToken.getToken())
                .build();

        AuthItem item = table.getItem(key);

        //Item returned is null, was deleted by another sweep through of the table.
        if(item == null){
            deleteAllExpiredTokens();
            return true;
        }

        if(System.currentTimeMillis() >= item.getDatetime() + 24 * 60 * 60 * 1000){
            table.deleteItem(item);
            deleteAllExpiredTokens();
            return true;
        }
        return false;
    }

    @Override
    public void refreshAuth(AuthToken authToken) {
        DynamoDbTable<AuthItem> table = enhancedClient.table(TableName, TableSchema.fromBean(AuthItem.class));
        Key key = Key.builder()
                .partitionValue(authToken.getToken())
                .build();

        AuthItem authToRefresh = table.getItem(key);
        if(authToRefresh != null){
            authToRefresh.setDatetime(System.currentTimeMillis());
            table.updateItem(authToRefresh);
        }
    }

    @Override
    public void deleteAllExpiredTokens() {
        DynamoDbTable<AuthItem> table = enhancedClient.table(TableName, TableSchema.fromBean(AuthItem.class));
        ScanEnhancedRequest scanRequest = ScanEnhancedRequest.builder().build();
        try {
            PageIterable<AuthItem> results = table.scan(scanRequest);
            for(Page<AuthItem> page : results) {
                for(AuthItem item : page.items()){
                    if(System.currentTimeMillis() >= item.getDatetime() + 24 * 60 * 60 * 1000){
                        table.deleteItem(item);
                    }
                }
            }
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    @Override
    public String getUserAliasFromToken(AuthToken token) {
        DynamoDbTable<AuthItem> table = enhancedClient.table(TableName, TableSchema.fromBean(AuthItem.class));
        Key key = Key.builder()
                .partitionValue(token.getToken())
                .build();

        AuthItem item = table.getItem(key);
        return item.getUser_alias();
    }
}
