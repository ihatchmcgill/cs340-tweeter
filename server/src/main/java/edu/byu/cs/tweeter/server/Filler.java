package edu.byu.cs.tweeter.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;

public class Filler {

    // How many follower users to add
    // We recommend you test this with a smaller number first, to make sure it works for you
    private final static int NUM_USERS = 10000;

    // The alias of the user to be followed by each user created
    // This example code does not add the target user, that user must be added separately.
    private final static String FOLLOW_TARGET = "@isaac";

    UserDAO userDAO;
    FollowDAO followDAO;

    public Filler(UserDAO userDAO, FollowDAO followDAO){
        this.userDAO = userDAO;
        this.followDAO = followDAO;
    }

    public void fillDatabase() {

        Map<String, User> followersMap = new HashMap<>();
        List<User> users = new ArrayList<>();

        User followTargetUser = userDAO.getUser(FOLLOW_TARGET);

        // Iterate over the number of users you will create
        for (int i = 1; i <= NUM_USERS; i++) {

            String firstName = "Guy";
            String lastName = "" + i;
            String alias = "@guy" + i;

            // Note that in this example, a UserDTO only has a name and an alias.
            // The url for the profile image can be derived from the alias in this example

            //hardcoded a shared profile picture for all users.
            String s3ProfilePicURL = "https://tweeterprofilepicturebucket.s3.us-west-2.amazonaws.com/%40thanos";
            User user = new User(firstName, lastName, alias, s3ProfilePicURL);
            users.add(user);

            // Note that in this example, to represent a follows relationship, only the aliases
            // of the two users are needed
            followersMap.put(alias, user);
        }

        // Call the DAOs for the database logic
        if (users.size() > 0) {
            userDAO.addUserBatch(users);
        }
        if (followersMap.size() > 0) {
            followDAO.addFollowersBatch(followersMap, followTargetUser);
        }
    }
}
