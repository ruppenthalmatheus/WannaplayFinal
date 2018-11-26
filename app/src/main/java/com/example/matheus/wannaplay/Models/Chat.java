package com.example.matheus.wannaplay.Models;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Chat {
    private String user1Id;
    private String user2Id;
    private Date lastMessageDate;
    private String lastMessage;
    private List<String> users;

    public Chat() {}

    public Chat(String user1Id, String user2Id, Date lastMessageDate, String lastMessage) {
        this.user1Id = user1Id;
        this.user2Id = user2Id;
        this.lastMessageDate = lastMessageDate;
        this.lastMessage = lastMessage;
        this.users = Arrays.asList(user1Id,user2Id);
    }

    public String getUser1Id() {
        return user1Id;
    }

    public void setUser1Id(String user1Id) {
        this.user1Id = user1Id;
    }

    public String getUser2Id() {
        return user2Id;
    }

    public void setUser2Id(String user2Id) {
        this.user2Id = user2Id;
    }

    public Date getLastMessageDate() {
        return lastMessageDate;
    }

    public void setLastMessageDate(Date lastMessageDate) {
        this.lastMessageDate = lastMessageDate;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }
}
