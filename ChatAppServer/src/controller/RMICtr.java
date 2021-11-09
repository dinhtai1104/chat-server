package controller;


import dao.FriendDAO;
import dao.FriendRequestDAO;
import dao.RoomDAO;
import dao.UserDAO;
import dao.UserInRoomDAO;
import interfacecontrol.IService;
import java.net.InetAddress;

import java.util.List;
import model.FriendRequest;
import model.IPAddress;
import model.Room;
import model.User;
import net.sf.lipermi.handler.CallHandler;
import net.sf.lipermi.net.Server;

class RMICtr implements IService{
    private IPAddress myAddress = new IPAddress("localhost", 9088);
    private String rmiKey = "rmi";
    UserDAO userDAO;
    FriendDAO friendDAO;
    UserInRoomDAO userInRoomDAO;
    RoomDAO roomDAO;
    FriendRequestDAO friendRequestDAO;
    
    public RMICtr()   {
        userDAO = new UserDAO();
        friendDAO = new FriendDAO();
        userInRoomDAO = new UserInRoomDAO();
        roomDAO = new RoomDAO();
        friendRequestDAO = new FriendRequestDAO();
        start();
    }
    public void start()  {
    // registry this to the localhost
        try{
            
            CallHandler callHandler = new CallHandler();
            callHandler.registerGlobal(IService.class, this);
            Server server = new Server();
            server.bind(myAddress.getPort(), callHandler);
            myAddress.setHost(InetAddress.getLocalHost().getHostAddress());
            System.out.println("The RMI has registered the service key: " + rmiKey + ", at the port: " + myAddress.getPort());
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public User checkLogin(User u){
        return userDAO.checkLogin(u);
    }

    @Override
    public boolean createAccount(User user){
        return userDAO.createAccount(user);
    }

    @Override
    public boolean updateAccount(User user){
        return userDAO.updateAccount(user);
    }

    @Override
    public User getUser(int id)  {
        return userDAO.getUser(id);
    }

    @Override
    public List<User> findUsersByFullName(String key)  {
        return userDAO.findUsersByFullName(key);
    }

    @Override
    public List<User> getFriends(int user)  {
        return friendDAO.getFriends(user);
    }

    @Override
    public boolean addFriend(FriendRequest fr)  {
        return friendDAO.addFriend(fr);
    }

    @Override
    public boolean deleteFriend(int friend1, int friend2)  {
        return friendDAO.deleteFriend(friend1, friend2);
    }

    @Override
    public boolean sendRequest(FriendRequest friendRequest)  {
        return friendRequestDAO.sendRequest(friendRequest);
    }

    @Override
    public List<FriendRequest> getFriendRequests(int user)  {
        return friendRequestDAO.getFriendRequests(user);
    }

    @Override
    public boolean deleteRequest(FriendRequest request)  {
        return friendRequestDAO.deleteRequest(request);
    }

    @Override
    public boolean acceptRequest(FriendRequest request)  {
        return friendRequestDAO.acceptRequest(request);
    }

    @Override
    public Room createRoom(Room room)  {
        return roomDAO.createRoom(room);
    }

    @Override
    public Room getRoom(int id)  {
        return roomDAO.getRoom(id);
    }

    @Override
    public boolean deleteRoom(int room)  {
        return roomDAO.deleteRoom(room);
    }

    @Override
    public void createRoom(List<User> list)  {
        roomDAO.createRoom(list);
    }

    @Override
    public boolean checkRoomCreated(int u1, int u2)  {
        return userInRoomDAO.checkRoomCreated(u1, u2);
    }

    @Override
    public Room getRoomOfJust2People(int u1, int u2)  {
        return userInRoomDAO.getRoomOfJust2People(u1, u2);
    }

    @Override
    public boolean checkUserInRoom(int idUser, int idRoom)  {
        return userInRoomDAO.checkUserInRoom(idUser, idRoom);
    }

    @Override
    public boolean joinRoom(int idUser, int idRoom)  {
        return userInRoomDAO.joinRoom(idUser, idRoom);
    }

    @Override
    public List<Room> getListRoom(int idUser) {
        return userInRoomDAO.getListRoom(idUser);
    }

    @Override
    public List<User> getUsersInRoom(int roomId) {
        return userInRoomDAO.getUsersInRoom(roomId);
    }
}