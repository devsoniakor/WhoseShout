package com.mad.whosetreat.model;

/**
 * Chat is modelling each chat rooms
 * Currently not being used
 */
public class Chat {
    private String mName;
    private String mMessage;
    private String mUid;
    private long mMessageTime;

    public Chat() { }

    public Chat(String userName, String message, String uid) {
        this.mName = userName;
        this.mMessage = message;
        mUid = uid;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public String getUid() {
        return mUid;
    }

    public void setUid(String uid) {
        mUid = uid;
    }

    public long getMessageTime() {
        return mMessageTime;
    }

    public void setMessageTime(long mMessageTime) {
        this.mMessageTime = mMessageTime;
    }
}
