package edu.byu.cs.tweeter.client.presenter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.observer.ButtonObserver;
import edu.byu.cs.tweeter.client.model.service.observer.CountObserver;
import edu.byu.cs.tweeter.client.model.service.observer.SimpleNotificationObserver;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import java.lang.*;

public class GetMainPresenter extends Presenter<GetMainPresenter.MainView>{

    public interface MainView extends Presenter.View {
        void setFollowing();
        void setFollow();

        void follow();
        void unfollow();

        void logout();

        void cancelToast();

        void setFolloweeCount(int count);
        void setFollowerCount(int count);
    }

    private FollowService followService;

    public GetMainPresenter(MainView view){
        super(view);
    }

    //Service Factory
    public FollowService getNewFollowService(){
        if(followService == null){
            followService = new FollowService();
        }
        return followService;
    }

    public void clearCache() {
        Cache.getInstance().clearCache();
    }

    public void getIsFollower(User selectedUser) {
        getNewFollowService().getIsFollower(selectedUser, new GetFollowButtonObserver());
    }

    private class GetFollowButtonObserver implements ButtonObserver {
        @Override
        public void displayError(String message) {
            view.displayMessage("Failed to determine following relationship: " + message);
        }
        @Override
        public void displayException(Exception ex) {
            view.displayMessage("Failed to determine following relationship because of exception: " + ex.getMessage());
        }
        @Override
        public void setFollowingButton() {
            view.setFollowing();
        }

        @Override
        public void setFollowButton() {
            view.setFollow();
        }

    }


    public void followTask(User selectedUser, boolean follow) {
        if(follow){
            getNewFollowService().follow(selectedUser, new GetFollowObserver());
        }else{
            getNewFollowService().unfollow(selectedUser, new GetUnfollowObserver());
        }

    }
    private class GetFollowObserver implements SimpleNotificationObserver{
        @Override
        public void displayError(String message) {
            view.displayMessage("Failed to follow: " + message);
        }
        @Override
        public void displayException(Exception ex) {
            view.displayMessage("Failed to follow because of exception: " + ex.getMessage());
        }
        @Override
        public void handleSuccess() {
            view.follow();
        }
    }

    private class GetUnfollowObserver implements SimpleNotificationObserver {
        @Override
        public void displayError(String message) {
            view.displayMessage("Failed to unfollow: " + message);
        }

        @Override
        public void displayException(Exception ex) {
            view.displayMessage("Failed to unfollow because of exception: " + ex.getMessage());
        }
        @Override
        public void handleSuccess() {
            view.unfollow();
        }

    }

    public void logout() {
        getNewFollowService().logout(new GetLogOutObserver());
    }

    private class GetLogOutObserver implements SimpleNotificationObserver{
        @Override
        public void displayError(String message) {
            view.displayMessage("Failed to logout: " + message);
        }

        @Override
        public void displayException(Exception ex) {
            view.displayMessage("Failed to logout due to an exception: " + ex.getMessage());
        }

        @Override
        public void handleSuccess() {
            view.logout();
        }
    }

    public void postStatus(String post) {
        try{
            Status newStatus = new Status(post, Cache.getInstance().getCurrUser(), System.currentTimeMillis(), parseURLs(post), parseMentions(post));
            getNewFollowService().postStatus(newStatus, getNewPostStatusObserver());
        } catch(Exception e){
            view.displayMessage("Posting status error");
        }
    }

    public GetPostStatusObserver getNewPostStatusObserver(){
        return new GetPostStatusObserver();
    }
    
    public class GetPostStatusObserver implements SimpleNotificationObserver{

        @Override
        public void displayError(String message) {
            view.displayMessage("Failed to post status: " + message);
        }

        @Override
        public void displayException(Exception ex) {
            view.displayMessage("Failed to post status because of exception: " + ex.getMessage());
        }

        @Override
        public void handleSuccess() {
            view.cancelToast();
            view.displayMessage("Successfully Posted!");
        }
    }

    public void getFollowsCounts(User selectedUser) {
        getNewFollowService().getFollowsCount(selectedUser, new GetFollowsObserver());
        getNewFollowService().getFollowersCount(selectedUser, new GetFollowersObserver());
    }

    private class GetFollowsObserver implements CountObserver {
        @Override
        public void displayError(String message) {
            view.displayMessage("Failed to get followees count: " + message);
        }

        @Override
        public void displayException(Exception ex) {
            view.displayMessage("Failed to get followees count because of exception: " + ex.getMessage());
        }

        @Override
        public void displayCount(int count) {
            view.setFolloweeCount(count);
        }
    }
    private class GetFollowersObserver implements CountObserver{

        @Override
        public void displayError(String message) {
            view.displayMessage("Failed to get followers count: " + message);
        }

        @Override
        public void displayException(Exception ex) {
            view.displayMessage("Failed to get followers count because of exception: " + ex.getMessage());
        }

        @Override
        public void displayCount(int count) {
            view.setFollowerCount(count);
        }

    }


    public String getFormattedDateTime() throws ParseException {
        SimpleDateFormat userFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat statusFormat = new SimpleDateFormat("MMM d yyyy h:mm aaa");

        return statusFormat.format(userFormat.parse(LocalDate.now().toString() + " " + LocalTime.now().toString().substring(0, 8)));
    }

    public List<String> parseURLs(String post) {
        List<String> containedUrls = new ArrayList<>();
        for (String word : post.split("\\s")) {
            if (word.startsWith("http://") || word.startsWith("https://")) {

                int index = findUrlEndIndex(word);

                word = word.substring(0, index);

                containedUrls.add(word);
            }
        }

        return containedUrls;
    }

    public List<String> parseMentions(String post) {
        List<String> containedMentions = new ArrayList<>();

        for (String word : post.split("\\s")) {
            if (word.startsWith("@")) {
                word = word.replaceAll("[^a-zA-Z0-9]", "");
                word = "@".concat(word);

                containedMentions.add(word);
            }
        }

        return containedMentions;
    }

    public int findUrlEndIndex(String word) {
        if (word.contains(".com")) {
            int index = word.indexOf(".com");
            index += 4;
            return index;
        } else if (word.contains(".org")) {
            int index = word.indexOf(".org");
            index += 4;
            return index;
        } else if (word.contains(".edu")) {
            int index = word.indexOf(".edu");
            index += 4;
            return index;
        } else if (word.contains(".net")) {
            int index = word.indexOf(".net");
            index += 4;
            return index;
        } else if (word.contains(".mil")) {
            int index = word.indexOf(".mil");
            index += 4;
            return index;
        } else {
            return word.length();
        }
    }

}
