/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interfacecontrol;

import java.util.List;
import model.FriendRequest;
import model.Room;
import model.User;

/**
 *
 * @author Admin
 */
public interface IService {
    //User
    public User checkLogin(User u);
    public boolean createAccount(User user);
    public boolean updateAccount(User user);
    public User getUser(int id);
    
    //User in room
    public boolean checkRoomCreated(int u1, int u2);
    public Room getRoomOfJust2People(int u1, int u2);
    public boolean checkUserInRoom(int idUser, int idRoom);
    public boolean joinRoom(int idUser, int idRoom);
    public List<Room> getListRoom(int idUser);
    public List<User> getUsersInRoom(int roomId);
    
    //Room
    public Room createRoom(Room room);
    public Room getRoom(int id);
    public boolean deleteRoom(int room);
    public void createRoom(List<User> list);
    
    //Friend Request
    public boolean sendRequest(FriendRequest friendRequest);
    public List<FriendRequest> getFriendRequests (int user);
    public boolean deleteRequest(FriendRequest request);
    public boolean acceptRequest(FriendRequest request);
    
    //Friend
    public List<User> findUsersByFullName(String key);
    public List<User> getFriends(int user);
    public boolean addFriend(FriendRequest fr);
    public boolean deleteFriend(int friend1, int friend2);
}
