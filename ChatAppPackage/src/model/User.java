package model;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;

public class User implements Serializable {
    private int id;
    private String username = "";
    private String password = "";
    private String email = "";
    private String fullName = "";
    private String address = "";
    private String phoneNum = "";
    private String avatarUrl = "";
    private Date dateOfBirth = null;
    private Date createat = null;
    private List<User> friendList;
    private List<FriendRequest> friendRequestList;
    private List<Room> roomList;
    private int status;
    
    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    
    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public List<Room> getRoomList() {
        return roomList;
    }

    public void setRoomList(List<Room> roomList) {
        this.roomList = roomList;
    }
    
    
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public Date getCreateat() {
        return createat;
    }

    public void setCreateat(Date createat) {
        this.createat = createat;
    }
    

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public List<User> getFriendList() {
        return friendList;
    }

    public void setFriendList(List<User> friendList) {
        this.friendList = friendList;
    }

    public List<FriendRequest> getFriendRequestList() {
        return friendRequestList;
    }

    public void setFriendRequestList(List<FriendRequest> friendRequestList) {
        this.friendRequestList = friendRequestList;
    }

    
}
