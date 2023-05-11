package edu.byu.cs.tweeter.client.model.service;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.client.model.service.handler.GetUserHandler;
import edu.byu.cs.tweeter.client.model.service.handler.LoginHandler;
import edu.byu.cs.tweeter.client.model.service.handler.RegisterHandler;
import edu.byu.cs.tweeter.client.model.service.observer.StartUserActivityObserver;

public class UserService extends Service {

    public void getUserTask(String userAlias, StartUserActivityObserver observer) {
        GetUserTask getUserTask = new GetUserTask(Cache.getInstance().getCurrUserAuthToken(),
                userAlias, new GetUserHandler(observer));
        executeTask(getUserTask);
    }

    public void loginUser(String alias, String password, StartUserActivityObserver observer) {
        LoginTask loginTask = new LoginTask(alias,
                password,
                new LoginHandler(observer));
        executeTask(loginTask);
    }

    public void register(String firstName, String lastName, String alias, String password, String imageBytesBase64, StartUserActivityObserver observer) {
        RegisterTask registerTask = new RegisterTask(firstName, lastName,
                alias, password, imageBytesBase64, new RegisterHandler(observer));
        executeTask(registerTask);
    }

}
