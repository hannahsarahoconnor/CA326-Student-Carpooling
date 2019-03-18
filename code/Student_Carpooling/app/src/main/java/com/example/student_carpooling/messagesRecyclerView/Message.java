package com.example.student_carpooling.messagesRecyclerView;

public class Message {

    private String Sender;
    private String Recipient;
    private String Message;


    public Message(String Sender, String Recipient, String Message){
        this.Sender = Sender;
        this.Recipient = Recipient;
        this.Message = Message;

    }

    public Message(){

    }

    public String getMessage() {
        return Message;
    }

    public String getRecipient() {
        return Recipient;
    }

    public String getSender() {
        return Sender;
    }

    public void setMessage(String message) {
        Message = message;
    }
}
