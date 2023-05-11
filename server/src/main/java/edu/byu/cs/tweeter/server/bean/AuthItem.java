package edu.byu.cs.tweeter.server.bean;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;


@DynamoDbBean
public class AuthItem {
    private String token;
    private String user_alias;
    private long datetime;

    @DynamoDbPartitionKey
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUser_alias() {
        return user_alias;
    }

    public void setUser_alias(String user_alias) {
        this.user_alias = user_alias;
    }

    public long getDatetime() {
        return datetime;
    }

    public void setDatetime(long datetime) {
        this.datetime = datetime;
    }

    @Override
    public String toString() {
        return "AuthItem{" +
                "token='" + token + '\'' +
                ", user_alias='" + user_alias + '\'' +
                ", datetime=" + datetime + '\'' +
                '}';
    }
}
