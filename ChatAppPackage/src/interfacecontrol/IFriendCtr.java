/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interfacecontrol;

import java.util.List;
import model.FriendRequest;
import model.User;

/**
 *
 * @author Admin
 */
public interface IFriendCtr {
    public List<User> getFriends(int user);
    public boolean addFriend(FriendRequest fr);
    public boolean deleteFriend(int friend1, int friend2);
}
