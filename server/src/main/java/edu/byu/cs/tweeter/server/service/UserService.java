package edu.byu.cs.tweeter.server.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetFollowerCountRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.GetFollowerCountResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.server.dao.AuthDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.util.FakeData;
import edu.byu.cs.tweeter.util.Timestamp;

public class UserService {

    UserDAO userDAO;
    AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO){
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public LoginResponse login(LoginRequest request) {
        if(request.getUsername() == null){
            throw new RuntimeException("[Bad Request] Missing a username");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[Bad Request] Missing a password");
        }
        User user =  userDAO.getUser(request.getUsername());

        if(userDAO.isValidPassword(user.getAlias(), hashPassword(request.getPassword()))){
            AuthToken authToken = generateAuthToken();
            authDAO.addAuthToken(authToken, user.getAlias());
            return new LoginResponse(user, authToken);
        }

        throw new RuntimeException("[Bad Request] Invalid Password");
    }

    private AuthToken generateAuthToken() {
        UUID uuid = UUID.randomUUID();
        AuthToken newAuthToken = new AuthToken(uuid.toString(), Timestamp.getFormattedDate(System.currentTimeMillis()));
        return newAuthToken;
    }

    public RegisterResponse register(RegisterRequest request) {
        if(request.getUsername() == null){
            throw new RuntimeException("[Bad Request] Missing a username");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[Bad Request] Missing a password");
        }


        String passwordHash = hashPassword(request.getPassword());
        AuthToken authToken = generateAuthToken();

        //add authtoken for user.
        authDAO.addAuthToken(authToken, request.getUsername());

        //returns user registered, null if invalid
        User registeredUser = userDAO.registerUser(request, passwordHash);
        if(registeredUser != null){
            return new RegisterResponse(registeredUser, authToken);
        }else{
            return new RegisterResponse("Failed to register new user. User already exists.");
        }

    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "FAILED TO HASH";
    }

    public GetUserResponse getUser(GetUserRequest request) {
        if(request.getAlias() == null){
            throw new RuntimeException("[Bad Request] Missing an alias");
        }
        return new GetUserResponse(userDAO.getUser(request.getAlias()));
    }

    public LogoutResponse logout(LogoutRequest request) {
        if(request.getAuthToken() == null){
            throw new RuntimeException("[Bad Request] Missing an AuthToken");
        }
        authDAO.deleteAuthToken(request.getAuthToken());
        return new LogoutResponse(true);
    }

    public GetFollowingCountResponse getFollowingCount(GetFollowingCountRequest request) {
        if(request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have an AuthToken ");
        }
        else if(request.getUserAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a user alias");
        }

        if(authDAO.isAuthExpired(request.getAuthToken())){
            throw new RuntimeException("[Server Error] Expired AuthToken. Please re-login");
        }else{
            authDAO.refreshAuth(request.getAuthToken());
        }
        return userDAO.getFolloweeCount(request.getUserAlias());
    }

    public GetFollowerCountResponse getFollowerCount(GetFollowerCountRequest request) {
        if(request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have an AuthToken ");
        }
        else if(request.getUserAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a user alias");
        }
        if(authDAO.isAuthExpired(request.getAuthToken())){
            throw new RuntimeException("[Server Error] Expired AuthToken. Please re-login");
        }else{
            authDAO.refreshAuth(request.getAuthToken());
        }
        return userDAO.getFollowerCount(request.getUserAlias());
    }

}
