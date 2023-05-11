package edu.byu.cs.tweeter.server;

import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class Main {
    public static void main(String args[]){
        DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
                .region(Region.US_WEST_2)
                .build();

        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
        UserDAO userDAO = new UserDAO(enhancedClient);
        FollowDAO followDAO = new FollowDAO(enhancedClient);
        Filler filler = new Filler(userDAO,followDAO);
        filler.fillDatabase();


    }
}
