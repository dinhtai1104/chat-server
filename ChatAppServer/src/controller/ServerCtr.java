/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import dao.FriendDAO;
import dao.FriendRequestDAO;
import dao.MessageDAO;
import dao.RoomDAO;
import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
 
import dao.UserDAO;
import dao.UserInRoomDAO;
import java.util.List;
import model.ConnectionType;
import model.FriendRequest;
import model.IPAddress;
import model.Message;
import model.ObjectWrapper;
import model.Room;
import model.User;
import model.UserInRoom;
 
public class ServerCtr {
    private ServerSocket myServer;
    private ServerListening myListening;
    private ArrayList<ServerProcessing> myProcess;
    private IPAddress myAddress = new IPAddress("192.168.1.103",8888);  //default server host and port
     

     
    public ServerCtr(IPAddress ip){
        myProcess = new ArrayList<ServerProcessing>();
        myAddress = ip;
        openServer();       
    }
     
     
    private void openServer(){
        try {
            myServer = new ServerSocket(myAddress.getPort());
            myListening = new ServerListening();
            myListening.start();
//            myAddress.setHost(InetAddress.get().getHostAddress());
            //System.out.println("server started!");
            System.out.println("TCP server is running at the port " + myAddress.getPort() +"...");
        }catch(Exception e) {
            e.printStackTrace();;
        }
    }
     
    public void stopServer() {
        try {
            for(ServerProcessing sp:myProcess)
                sp.stop();
            myListening.stop();
            myServer.close();
            System.out.println("TCP server is stopped!");
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
     
//    public void publicClientNumber() {
//        ObjectWrapper data = new ObjectWrapper(ObjectWrapper.SERVER_INFORM_CLIENT_NUMBER, myProcess.size());
//        for(ServerProcessing sp : myProcess) {
//            sp.sendData(data);
//        }
//    }
     
    /**
     * The class to listen the connections from client, avoiding the blocking of accept connection
     *
     */
    class ServerListening extends Thread{
         
        public ServerListening() {
            super();
        }
         
        public void run() {
            try {
                while(true) {
                    Socket clientSocket = myServer.accept();
                    System.out.println("Connect: " + clientSocket.toString());
                    ServerProcessing sp = new ServerProcessing(clientSocket);
                    sp.start();
                    myProcess.add(sp);
                    System.out.println("Number of client connecting to the server: " + myProcess.size());
//                    publicClientNumber();
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void sendDataAll(Object ob) {
        for (ServerProcessing ser : myProcess) {
            ser.sendData(ob);
        }
    }
    /**
     * The class to treat the requirement from client
     *
     */
    class ServerProcessing extends Thread{
        private Socket mySocket;
        private int idUser = 0;
        //private ObjectInputStream ois;
        //private ObjectOutputStream oos;
         
        public ServerProcessing(Socket s) {
            super();
            mySocket = s;
        }
         
        public void sendData(Object obj) {
            try {
                ObjectOutputStream oos= new ObjectOutputStream(mySocket.getOutputStream());
                oos.writeObject(obj);
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
         
        public void run() { 
            try {
                while(true) {
                        ObjectInputStream ois = new ObjectInputStream(mySocket.getInputStream());
    //                    ObjectOutputStream oos= new ObjectOutputStream(mySocket.getOutputStream());
                        {
                            Object o = ois.readObject();
                            if(o instanceof ObjectWrapper){ 
                                ObjectWrapper data = (ObjectWrapper)o;
                                if (data.getChoice() == ConnectionType.LOGIN) {
                                    System.out.println("Login");
                                    User user = (User)data.getData();
                                    UserDAO dao = new UserDAO();
                                    user = dao.checkLogin(user);
    //                                if (user != null) {
    //                                    
    //                                }
                                    if (user != null) {
                                        this.idUser = user.getId();
                                        new UserDAO().setOnlineOffline(idUser, true);
                                    }
                                    sendData(new ObjectWrapper(user, ConnectionType.REPLY_LOGIN));
                                } else if (data.getChoice() == ConnectionType.REGISTER){
                                    User user = (User)data.getData();
                                    UserDAO dao = new UserDAO();
                                    String rs = dao.createAccount(user) == true ? "ok" : "false";
                                    sendData(new ObjectWrapper(rs, ConnectionType.REPLY_REGISTER));
                                } else if (data.getChoice() == ConnectionType.ONLINE_INFORM) {
                                    User user = (User)data.getData();
                                    ObjectWrapper sendToAll = new ObjectWrapper(user.getId(), ConnectionType.ONLINE_INFORM);
                                    sendDataAll(sendToAll);
                                } else if (data.getChoice() == ConnectionType.OFFLINE_INFORM) {
                                    User user = (User)data.getData();
                                    ObjectWrapper sendToAll = new ObjectWrapper(user.getId(), ConnectionType.OFFLINE_INFORM);
                                    sendDataAll(sendToAll);
                                } else if (data.getChoice() == ConnectionType.GETFRIEND) {
                                    int idUser = (int)data.getData();
                                    List<User> listFriend = new FriendDAO().getFriends(idUser);
                                    ObjectWrapper sender = new ObjectWrapper(listFriend, ConnectionType.REPLY_GETFRIEND);
                                    sendData(sender);
                                } else if (data.getChoice() == ConnectionType.CHAT) {

                                    Message message = (Message) data.getData();
                                    System.out.println("Nhan duoc tin nhan: " + message.getContent() + 
                                            "\nDen room: " + message.getRoom().getId() + 
                                            "\nTu user: " + message.getAuthor().getFullName());
                                    if (new MessageDAO().saveMessage(message)) {
                                        List<Message> list = new MessageDAO().getMessages(message.getRoom().getId());
                                        ObjectWrapper sender = new ObjectWrapper(list, ConnectionType.REPLY_CHAT);
                                        sendDataAll(sender);
                                        System.out.println("Sent To Client");
                                    } else {
                                        System.out.println("Loi khi save Message");
                                    }
                                } else if( data.getChoice() == ConnectionType.FRIENDREQUEST ) {

                                    FriendRequest fr = (FriendRequest)data.getData();
                                    new FriendRequestDAO().sendRequest(fr);
                                    ObjectWrapper sender = new ObjectWrapper(fr, ConnectionType.REPLY_FRIENDREQUEST);
                                    sendData(sender);
                                } else if (data.getChoice() == ConnectionType.ADDFRIEND) {

                                    FriendRequest fr = (FriendRequest)data.getData();
                                    new FriendRequestDAO().acceptRequest(fr);
                                    List<User> friendList = new FriendDAO().getFriends(fr.getReceiver().getId());
                                    List<Object> list = new ArrayList<Object>();
                                    list.add(fr.getReceiver().getId());
                                    list.add(list);
                                    ObjectWrapper sender = new ObjectWrapper(list, ConnectionType.REPLY_ADDFRIEND);
    //                                sendDataAll(sender);

                                } else if (data.getChoice() == ConnectionType.DECLINEFRIEND) {
                                    FriendRequest fr = (FriendRequest)data.getData();
                                    new FriendRequestDAO().deleteRequest(fr);
                                    List<User> friendList = new FriendDAO().getFriends(fr.getReceiver().getId());

                                    List<Object> list = new ArrayList<Object>();
                                    list.add(fr.getReceiver().getId());
                                    list.add(list);

                                    ObjectWrapper sender = new ObjectWrapper(list, ConnectionType.REPLY_DECLINEFRIEND);
    //                                sendData(sender);
    //                                sendDataAll(sender);
                                } else if( data.getChoice() == ConnectionType.INVITEROOM ) {

                                } else if ( data.getChoice() == ConnectionType.GETROOM) {
                                    int userId = (int)data.getData();
                                    List<Room> listRoom = new UserInRoomDAO().getListRoom(userId);
                                    ObjectWrapper sender = new ObjectWrapper(listRoom, ConnectionType.REPLY_GETROOM);
                                    sendData(sender);
                                    System.out.println("Get Request Update Room UI");
                                } else if (data.getChoice() == ConnectionType.GETROOMFRIEND) {
                                    List<Object> list = (List<Object>) data.getData();
                                    Room room = new UserInRoomDAO().getRoomOfJust2People((int)list.get(0), (int)list.get(1));
                                    ObjectWrapper sender = new ObjectWrapper(room, ConnectionType.REPLY_GETROOMFRIEND);
                                    sendData(sender);
                                } else if (data.getChoice() == ConnectionType.GETFRIENDREQUEST){
                                    int userid = (int)data.getData();
                                    List<FriendRequest> list = new FriendRequestDAO().getFriendRequests(userid);
                                    ObjectWrapper sender = new ObjectWrapper(list, ConnectionType.REPLY_GETFRIENDREQUEST);
                                    sendData(sender);
                                } else if (data.getChoice() == ConnectionType.SEARCH) {
                                    String key = (String)data.getData();
                                    List<User> list = new UserDAO().findUsersByFullName(key);
                                    ObjectWrapper sender = new ObjectWrapper(list, ConnectionType.REPLY_SEARCH);
                                    sendData(sender);
                                } else if (data.getChoice() == ConnectionType.CREATEROOM) {
                                    List<User> list = (List<User>) data.getData();
                                    new RoomDAO().createRoom(list);
                                } else if (data.getChoice() == ConnectionType.EDITPROFILE) {
                                    User u = (User)data.getData();
                                    new UserDAO().updateAccount(u);
                                }
                            } 
                        }
                    }
                }
            catch (EOFException | SocketException e) {             
                    //e.printStackTrace();
                    myProcess.remove(this);
                        System.out.println("Number of client connecting to the server: " + myProcess.size());
                        new UserDAO().setOnlineOffline(idUser, false);
    //                publicClientNumber();
                    try {
                        mySocket.close();
                    }catch(Exception ex) {  
                        ex.printStackTrace();
                    }
                    this.stop();
                }catch (Exception e) {
                    e.printStackTrace();
                }
        }
    }
    
    public static void main(String[] args) {
        new ServerCtr( new IPAddress("192.168.1.103",  9086) );
    }
}