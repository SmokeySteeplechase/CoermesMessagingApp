package com.example.myapplication;

public  class chat {
    public String date, from, message, messageID,time,to,type;
    private String NOF;

    public chat() {
    }

    public chat(String date, String from, String message, String messageID, String time, String to, String type, String NOF) {
        this.date = date;
        this.from = from;
        this.message = message;
        this.messageID = messageID;
        this.time = time;
        this.to = to;
        this.type = type;
        this.NOF = NOF;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNOF() {
        return NOF;
    }

    public void setNOF(String NOF) {
        this.NOF = NOF;
    }

}
