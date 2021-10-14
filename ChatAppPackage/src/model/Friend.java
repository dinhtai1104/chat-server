package model;

import java.io.Serializable;
import java.sql.Date;

public class Friend implements Serializable {
    private int id;
    private User friend1;
    private Date friendDate;
    private User friend2;


    public Friend(User friend1, User friend2) {
        this.friend1 = friend1;
        this.friend2 = friend2;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public User getFriend2() {
        return friend2;
    }

    public void setFriend2(User friend2) {
        this.friend2 = friend2;
    }

    public User getFriend1() {
        return friend1;
    }

    public void setFriend1(User friend1) {
        this.friend1 = friend1;
    }

    public Date getFriendDate() {
        return friendDate;
    }

    public void setFriendDate(Date friendDate) {
        this.friendDate = friendDate;
    }
}
