package edu.byu.cs.tweeter.client.presenter;

import android.graphics.drawable.Drawable;
import android.text.Editable;

public class GetRegisterPresenter extends AuthenticatePresenter {

    public GetRegisterPresenter(AuthenticateView view){
        super(view);
    }

    @Override
    protected void showException(Exception ex) {
        view.displayMessage("Failed to register because of exception: " + ex.getMessage());
    }

    @Override
    protected void showError(String message) {
        view.displayMessage("Failed to register: " + message);
    }


    public void register(String firstName, String lastName, String alias, String password, String imageBytesBase64) {
        userService.register(firstName,lastName,alias,password,imageBytesBase64,new GetUserObserver());
    }


    public void validateRegistration(Editable firstName, Editable lastName, Editable alias,
                                     Editable password, Drawable imageToUpload) {
        if (firstName.length() == 0) {
            throw new IllegalArgumentException("First Name cannot be empty.");
        }
        if (lastName.length() == 0) {
            throw new IllegalArgumentException("Last Name cannot be empty.");
        }
        if (alias.length() == 0) {
            throw new IllegalArgumentException("Alias cannot be empty.");
        }
        if (alias.charAt(0) != '@') {
            throw new IllegalArgumentException("Alias must begin with @.");
        }
        if (alias.length() < 2) {
            throw new IllegalArgumentException("Alias must contain 1 or more characters after the @.");
        }
        if (password.length() == 0) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }

        if (imageToUpload == null) {
            throw new IllegalArgumentException("Profile image must be uploaded.");
        }
    }
}
