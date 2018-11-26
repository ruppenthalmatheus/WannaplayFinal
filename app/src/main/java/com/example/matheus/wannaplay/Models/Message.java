package com.example.matheus.wannaplay.Models;

import java.util.Date;

public class Message {
    private String text;
    private String writerUserId;
    private Date date;

    public Message() {
    }

    public Message(String text, String writerUserId, Date date) {
        this.text = text;
        this.writerUserId = writerUserId;
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getWriterUserId() {
        return writerUserId;
    }

    public void setWriterUserId(String writerUserId) {
        this.writerUserId = writerUserId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
