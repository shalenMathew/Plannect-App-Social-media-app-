package com.example.plannect.Modals;

public class Chats {


    String sender;
    String receiver;
    String message;
    String senderusername;
    String receiverusername;
    boolean seen;


    public Chats(String sender, String receiver, String message, String senderusername, String receiverusername, boolean seen) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.senderusername = senderusername;
        this.receiverusername = receiverusername;
        this.seen = seen;
    }

    public Chats() {
    }


    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderusername() {
        return senderusername;
    }

    public void setSenderusername(String senderusername) {
        this.senderusername = senderusername;
    }

    public String getReceiverusername() {
        return receiverusername;
    }

    public void setReceiverusername(String receiverusername) {
        this.receiverusername = receiverusername;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
}
