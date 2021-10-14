package model;

import java.io.Serializable;
import java.sql.Date;

public class Message implements Serializable {
    private int id;
    private User author;
    private Room room;
    private ChatFile fileChat;
    private String content;
    private String status;
    private Date createDate;

    public Message() {
    }

    public Message(User author, Room room, String content) {
        this.author = author;
        this.room = room;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public ChatFile getFileChat() {
        return fileChat;
    }

    public void setFileChat(ChatFile fileChat) {
        this.fileChat = fileChat;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
