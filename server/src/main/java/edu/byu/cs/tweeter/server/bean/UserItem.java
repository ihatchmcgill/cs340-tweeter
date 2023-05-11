package edu.byu.cs.tweeter.server.bean;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;


@DynamoDbBean
public class UserItem {
    private String userAlias;
    private String first_name;
    private String last_name;
    private String password_hash;
    private String image_url;
    private int num_followers;
    private int num_following;



    @DynamoDbPartitionKey
    public String getUserAlias() {
        return userAlias;
    }

    public void setUserAlias(String userAlias) {
        this.userAlias = userAlias;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getPassword_hash() {
        return password_hash;
    }

    public void setPassword_hash(String password_hash) {
        this.password_hash = password_hash;
    }

    public int getNum_followers() {
        return num_followers;
    }

    public void setNum_followers(int num_followers) {
        this.num_followers = num_followers;
    }

    public int getNum_following() {
        return num_following;
    }

    public void setNum_following(int num_following) {
        this.num_following = num_following;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }


    @Override
    public String toString() {
        return "UserItem{" +
                "alias='" + userAlias + '\'' +
                ", first_name='" + first_name + '\'' +
                ", last_name=" + last_name + '\'' +
                ", password_hash=" + password_hash + '\'' +
                ", num_followers=" + num_followers + '\'' +
                ", num_following=" + num_following + '\'' +
                ", image_url=" + image_url +
                '}';
    }
}
