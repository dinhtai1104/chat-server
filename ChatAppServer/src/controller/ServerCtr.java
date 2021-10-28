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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.ConnectionType;
import model.FriendRequest;
import model.IPAddress;
import model.Message;
import model.ObjectWrapper;
import model.Room;
import model.User;
import model.UserInRoom;
 
public class ServerCtr {
    public static ArrayList<Integer> listPortUDP = new ArrayList<Integer>();
    
    //--------------------TCP------------------------------
    private ServerSocket myServer;
    private ServerListening myListening;
    private ArrayList<ServerProcessing> myProcess;
    private IPAddress myAddress = new IPAddress("192.168.1.103",8888);  //default server host and port
    
    //---------------------UDP------------------------------
    private IPAddress udpServerAddress = new IPAddress("localhost", 1000);
    
    
     
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
    
    public int getPortForUDPServer() {
        Random rd = new Random();
        while(true) {
            int port = rd.nextInt(5000) + 1001;
            boolean accept = true;
            for (int t : listPortUDP) {
                if (t == port) {
                    accept = false;
                    break;
                }
            }
            if (accept) {
                listPortUDP.add(port);
                return port;
            }
            if (listPortUDP.size() > 1000) {
                break;
            }
        }
        return 1;
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
        
        //FOR UDP
        private DatagramSocket udpClient;
        private int port;
        
        //private ObjectInputStream ois;
        //private ObjectOutputStream oos;
         
        public ServerProcessing(Socket s) {
            super();
            mySocket = s;
            port = getPortForUDPServer();
            try {
                udpClient = new DatagramSocket(port);
                System.out.println("Connect to UDP at " + udpServerAddress.getHost() + " and port: " + port);
                
                
            } catch (SocketException ex) {
                Logger.getLogger(ServerCtr.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
         
        public void sendData(Object obj) {
            try {
                ObjectOutputStream oos= new ObjectOutputStream(mySocket.getOutputStream());
                oos.writeObject(obj);
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
        //UDP---------------------------------------
        public boolean sendDataToUDP(ObjectWrapper data) {
            try {
                //prepare the buffer and write the data to send into the buffer
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(data);
                oos.flush();            

                //create data package and send
                byte[] sendData = baos.toByteArray();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(udpServerAddress.getHost()), udpServerAddress.getPort());
                udpClient.send(sendPacket);

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error in sending data package");
                return false;
            }
            return true;
        }
        
        public ObjectWrapper receiveData() {
            ObjectWrapper result = null;
            try {   
                //prepare the buffer and fetch the received data into the buffer
                byte[] receiveData = new byte[1024 * 5];
                DatagramPacket receivePacket = new  DatagramPacket(receiveData, receiveData.length);
                udpClient.receive(receivePacket);

                //read incoming data from the buffer 
                ByteArrayInputStream bais = new ByteArrayInputStream(receiveData);
                ObjectInputStream ois = new ObjectInputStream(bais);
                result = (ObjectWrapper)ois.readObject();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error in receiving data package");
            }
            return result;
        }
        
        //END-=-----------------------------------------
         
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
                                    System.out.println("Get Data Login");
                                    User user = (User)data.getData();
                                    
                                    //---------------UDP------------------------
                                    sendDataToUDP(data);
                                    ObjectWrapper received = receiveData();
                                    if (received.getChoice() == ConnectionType.REPLY_LOGIN) {
                                        this.idUser = ((User)received.getData()).getId();
                                        sendData(received);
                                    }

                                } else if (data.getChoice() == ConnectionType.REGISTER){
                                    User user = (User)data.getData();
                                    
                                    sendDataToUDP(data);
                                    ObjectWrapper received = receiveData();
                                    if (received.getChoice() == ConnectionType.REPLY_REGISTER) {
                                        sendData(received);
                                    }
                                    
                                } else if (data.getChoice() == ConnectionType.ONLINE_INFORM) {
                                    int user = (int)data.getData();
                                    this.idUser = user;
                                    new UserDAO().setOnlineOffline(idUser, true);
                                } else if (data.getChoice() == ConnectionType.OFFLINE_INFORM) {
                                    User user = (User)data.getData();
                                    ObjectWrapper sendToAll = new ObjectWrapper(user.getId(), ConnectionType.OFFLINE_INFORM);
                                    sendDataAll(sendToAll);
                                } else if (data.getChoice() == ConnectionType.GETFRIEND) {
                                    int idUser = (int)data.getData();
                                    sendDataToUDP(data);
                                    ObjectWrapper received = receiveData();
                                    if (received.getChoice() == ConnectionType.REPLY_GETFRIEND) {
                                        sendData(new ObjectWrapper(received.getData(), ConnectionType.REPLY_REGISTER));
                                    }
                                } else if (data.getChoice() == ConnectionType.CHAT) {

                                    Message message = (Message) data.getData();
                                    System.out.println("Nhan duoc tin nhan: " + message.getContent() + 
                                            "\nDen room: " + message.getRoom().getId() + 
                                            "\nTu user: " + message.getAuthor().getFullName());
                                    
                                    sendDataToUDP(data);
                                    ObjectWrapper received = receiveData();
                                    if (received.getChoice() == ConnectionType.REPLY_CHAT) {
                                        sendDataAll(received);
                                    }
                                    
                                    
                                } else if( data.getChoice() == ConnectionType.FRIENDREQUEST ) {

                                    sendDataToUDP(data);
                                    ObjectWrapper received = receiveData();
                                    if (received.getChoice() == ConnectionType.REPLY_FRIENDREQUEST) {
                                        sendData(received);
                                    }
                                    
                                } else if (data.getChoice() == ConnectionType.ADDFRIEND) {

                                    sendDataToUDP(data);
                                    
//                                    FriendRequest fr = (FriendRequest)data.getData();
//                                    new FriendRequestDAO().acceptRequest(fr);

                                } else if (data.getChoice() == ConnectionType.DECLINEFRIEND) {
                                    sendDataToUDP(data);

//                                    FriendRequest fr = (FriendRequest)data.getData();
//                                    new FriendRequestDAO().deleteRequest(fr);

                                } else if( data.getChoice() == ConnectionType.INVITEROOM ) {

                                } else if ( data.getChoice() == ConnectionType.GETROOM) {
                                    
                                    sendDataToUDP(data);
                                    ObjectWrapper received = receiveData();
                                    if (received.getChoice() == ConnectionType.REPLY_GETROOM) {
                                        sendData(received);
                                    }
//                                    int userId = (int)data.getData();
//                                    List<Room> listRoom = new UserInRoomDAO().getListRoom(userId);
//                                    ObjectWrapper sender = new ObjectWrapper(listRoom, ConnectionType.REPLY_GETROOM);
//                                    sendData(sender);
//                                    System.out.println("Get Request Update Room UI");

                                } else if (data.getChoice() == ConnectionType.GETROOMFRIEND) {
                                    
                                    sendDataToUDP(data);
                                    ObjectWrapper received = receiveData();
                                    if (received.getChoice() == ConnectionType.REPLY_GETROOMFRIEND) {
                                        sendData(received);
                                    }
                                    
                                } else if (data.getChoice() == ConnectionType.GETFRIENDREQUEST){
                                    
                                    sendDataToUDP(data);
                                    ObjectWrapper received = receiveData();
                                    if (received.getChoice() == ConnectionType.REPLY_GETFRIENDREQUEST) {
                                        sendData(received);
                                    }
                                    
//                                    int userid = (int)data.getData();
//                                    List<FriendRequest> list = new FriendRequestDAO().getFriendRequests(userid);
//                                    ObjectWrapper sender = new ObjectWrapper(list, ConnectionType.REPLY_GETFRIENDREQUEST);
//                                    sendData(sender);
                                } else if (data.getChoice() == ConnectionType.SEARCH) {
                                    
                                    sendDataToUDP(data);
                                    ObjectWrapper received = receiveData();
                                    if (received.getChoice() == ConnectionType.REPLY_SEARCH) {
                                        sendData(received);
                                    }
                                    
//                                    String key = (String)data.getData();
//                                    List<User> list = new UserDAO().findUsersByFullName(key);
//                                    ObjectWrapper sender = new ObjectWrapper(list, ConnectionType.REPLY_SEARCH);
//                                    sendData(sender);
                                } else if (data.getChoice() == ConnectionType.CREATEROOM) {
                                    sendDataToUDP(data);
                                   
//                                    List<User> list = (List<User>) data.getData();
//                                    new RoomDAO().createRoom(list);
                                } else if (data.getChoice() == ConnectionType.EDITPROFILE) {
                                    sendDataToUDP(data);
//                                    User u = (User)data.getData();
//                                    new UserDAO().updateAccount(u);
                                }
                            } 
                        }
                    }
                }
            catch (EOFException | SocketException e) {             
                    //e.printStackTrace();
                    myProcess.remove(this);
                        System.out.println("Number of client connecting to the server: " + myProcess.size());
                        
                        sendDataToUDP(new ObjectWrapper(idUser, ConnectionType.OFFLINE_INFORM));
                        
//                        new UserDAO().setOnlineOffline(idUser, false);
                        for (int i = 0; i < listPortUDP.size(); i++) {
                            if (listPortUDP.get(i) == this.port) {
                                listPortUDP.remove(listPortUDP.get(i));
                                break;
                            }
                        }
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
        new UDPCtr(1000);
        new ServerCtr( new IPAddress("localhost",  9086) );
    }
}