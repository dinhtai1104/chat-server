package model;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;

public class Room implements Serializable {
    private int id;
    private String name;
    private String type;
    private List<User> userList;
    private List<Message> messages;
    private Date createDate;

    public Room() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    
    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        String nameRoom = "";
        for (User u : getUserList()) {
            String[] name = u.getFullName().split(" ");
            nameRoom += (name[name.length - 1] + ",");
        }
        return nameRoom;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
