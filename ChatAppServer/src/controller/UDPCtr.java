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
import dao.UserDAO;
import dao.UserInRoomDAO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import model.ConnectionType;
import model.FriendRequest;
import model.IPAddress;
import model.Message;
import model.ObjectWrapper;
import model.Room;
import model.User;


public class UDPCtr {
    private DatagramSocket myServer;    
    private IPAddress myAddress = new IPAddress("192.168.1.106", 1000); //default server address
    private UDPListening myListening;
    

    public UDPCtr(int port){
        myAddress.setPort(port);
        open();
    }
     
     
    public boolean open(){
        try {
            myServer = new DatagramSocket(myAddress.getPort());
            myAddress.setHost(InetAddress.getLocalHost().getHostAddress());
            myListening = new UDPListening();
            myListening.start();
            System.out.println("UDP server is running at the host: " + myAddress.getHost() + ", port: " + myAddress.getPort());
        }catch(Exception e) {
            e.printStackTrace();
            System.err.println("Error to open the datagram socket!");
            return false;
        }
        return true;
    }
     
    public boolean close(){
        try {
            myListening.stop();
            myServer.close();
        }catch(Exception e) {
            e.printStackTrace();
            System.err.println("Error to close the datagram socket!");
            return false;
        }
        return true;
    }
     
    class UDPListening extends Thread{
        public UDPListening() {
             
        }
        private User user;
        public void run() {
            while(true) {               
                try {   
                    //prepare the buffer and fetch the received data into the buffer
                    byte[] receiveData = new byte[1024 * 6];
                    DatagramPacket receivePacket = new  DatagramPacket(receiveData, receiveData.length);
                    myServer.receive(receivePacket);
                     
                    //read incoming data from the buffer 
                    ByteArrayInputStream bais = new ByteArrayInputStream(receiveData);
                    ObjectInputStream ois = new ObjectInputStream(bais);
                    ObjectWrapper receivedData = (ObjectWrapper)ois.readObject();
                     
                    //processing
                    ObjectWrapper resultData = new ObjectWrapper();
                    
                    switch (receivedData.getChoice()) {
                        case LOGIN:
                            user = (User)(receivedData.getData());
                            user = new UserDAO().checkLogin(user);
                            resultData.setChoice(ConnectionType.REPLY_LOGIN);
                            
                            if (user != null) {
                                new UserDAO().setOnlineOffline(user.getId(), true);
                            }
                            resultData.setData(user);
                            System.out.println("Login");
//                            if (user != null) {
//                                new UserDAO().setOnlineOffline(user.getId(), true);
//                            }
                            break;
                        case REGISTER:
                            user = (User)(receivedData.getData());
                            boolean check = new UserDAO().createAccount(user);
                            resultData.setChoice(ConnectionType.REPLY_REGISTER);
                            resultData.setData(check ? "ok" : "false");
                            break;
                        case ADDFRIEND:
                            FriendRequest fr = (FriendRequest)receivedData.getData();
                            new FriendRequestDAO().acceptRequest(fr);
                            break;
                        case GETROOM:
                            int userId = (int)receivedData.getData();
                            List<Room> listRoom = new UserInRoomDAO().getListRoom(userId);
                            resultData = new ObjectWrapper(listRoom, ConnectionType.REPLY_GETROOM);
//                            System.out.println("Get Request Update Room UI");
                            break;
                        case GETFRIEND:
                            int idUser = (int)receivedData.getData();
                            List<User> listFriend = new FriendDAO().getFriends(idUser);
                            resultData = new ObjectWrapper(listFriend, ConnectionType.REPLY_GETFRIEND);
                            break;
                        case GETROOMFRIEND:
                            List<Object> list = (List<Object>) resultData.getData();
                            Room room = new UserInRoomDAO().getRoomOfJust2People((int)list.get(0), (int)list.get(1));
                            resultData = new ObjectWrapper(room, ConnectionType.REPLY_GETROOMFRIEND);
                            break;
                        case CREATEROOM:
                            List<User> list1 = (List<User>) receivedData.getData();
                            new RoomDAO().createRoom(list1);
                            break;
                        case DECLINEFRIEND:
                            fr = (FriendRequest)receivedData.getData();
                            new FriendRequestDAO().deleteRequest(fr);
                            break;
                        case SEARCH:
                            String key = (String)receivedData.getData();
                            list1 = new UserDAO().findUsersByFullName(key);
                            resultData = new ObjectWrapper(list1, ConnectionType.REPLY_SEARCH);
                            break;
                        case GETFRIENDREQUEST:
                            int userid = (int)receivedData.getData();
                            List<FriendRequest> list2 = new FriendRequestDAO().getFriendRequests(userid);
                            resultData = new ObjectWrapper(list2, ConnectionType.REPLY_GETFRIENDREQUEST);
                            break;
                        case FRIENDREQUEST:
                            fr = (FriendRequest) receivedData.getData();
                            new FriendRequestDAO().sendRequest(fr);
                            break;
                        case EDITPROFILE:
                            User u = (User)receivedData.getData();
                            new UserDAO().updateAccount(u);
                            break;
                        case OFFLINE_INFORM:
                            int id = (int)receivedData.getData();
                            new UserDAO().setOnlineOffline(id, false);
                            break;
                        case CHAT:
                            Message message = (Message)receivedData.getData();
                            if (new MessageDAO().saveMessage(message)) {
                                List<Message> listM = new MessageDAO().getMessages(message.getRoom().getId());
                                resultData = new ObjectWrapper(listM, ConnectionType.REPLY_CHAT);
//                                ObjectWrapper sender = new ObjectWrapper(list, ConnectionType.REPLY_CHAT);
//                                sendDataAll(sender);
                                System.out.println("Sent To Client");
                            } else {
                                System.out.println("Loi khi save Message");
                            }
                            break;
                    }
                    
                    if (resultData.getData() != null) {
                        //prepare the buffer and write the data to send into the buffer
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ObjectOutputStream oos = new ObjectOutputStream(baos);
                        oos.writeObject(resultData);
                        oos.flush();            

                        //create data package and send
                        byte[] sendData = baos.toByteArray();
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, receivePacket.getAddress(), receivePacket.getPort());
                        myServer.send(sendPacket);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("Error when processing an incoming package");
                }    
            }
        }
    }
}