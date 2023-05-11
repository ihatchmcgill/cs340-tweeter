package edu.byu.cs.tweeter.server.sqs;

import edu.byu.cs.tweeter.model.domain.Status;

public class PostStatusMessage {
    private Status status;
    private String authorAlias;

    public PostStatusMessage(Status status, String authorAlias){
        this.status = status;
        this.authorAlias = authorAlias;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getAuthorAlias() {
        return authorAlias;
    }

    public void setAuthorAlias(String authorAlias) {
        this.authorAlias = authorAlias;
    }
}
