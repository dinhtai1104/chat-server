package dao;

import model.Room;
import model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.UserInRoom;

public class UserInRoomDAO extends DAO{

    public UserInRoomDAO() {
        super();
    }
    
    public boolean checkRoomCreated(int u1, int u2) {
        boolean result = false;
//        String sql = "select * from tbluserinroom,tblroom "
//                + "where (tbluserinroom.userid=? and tblroom.id=tbluserinroom.id) "
//                + "or (tbluserinroom.userid=? and tblroom.id=tbluserinroom.id)";
//        PreparedStatement ps = con.prepareStatement(sql);
//        ps.set
        return true;
    }
    
    public Room getRoomOfJust2People(int u1, int u2) {
        String sql = "select * from tbluserinroom,tblroom where tblroom.id=tbluserinroom.roomid and tblroom.type=2";
        PreparedStatement ps;
        try {
            ps = con.prepareStatement(sql);
            ResultSet rs= ps.executeQuery();
            while(rs.next()) {
                int roomid = rs.getInt("tbluserinroom.roomid");
                int s1 = rs.getInt("tbluserinroom.userid");
                if (rs.next()) {
                    int s2 = rs.getInt("tbluserinroom.userid");
                    if ((u1 == s1 && u2==s2) || (u1==s2&&u2==s1)) {
                        System.out.println("Found Room");
                        Room room = new Room();
                        room.setType(rs.getString("tblroom.type"));
                        room.setId(rs.getInt( "tblroom.id" ));
                        room = new RoomDAO().getRoom(room.getId());
                        return room;
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserInRoomDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        Room r = new Room();
        r.setType("2");
        r = new RoomDAO().createRoom(r);
        new UserInRoomDAO().joinRoom(u1, r.getId());       
        new UserInRoomDAO().joinRoom(u2, r.getId());
        r = new RoomDAO().getRoom(r.getId());

        return r;
    }
    
    public boolean checkUserInRoom(int idUser, int idRoom) {
        String sql = "select * from tbluserinroom where userid=? and roomid=?";
        try {
            PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, idUser);
            ps.setInt(2, idRoom);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return true;
            } else {
                return false;
            }
        }catch ( Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean joinRoom(int idUser, int idRoom) {
        String sql = "insert into tbluserinroom(userid, roomid, role) value(?,?,?)";
        try {
            PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, idUser);
            ps.setInt(2, idRoom);
            ps.setString(3, "member");
            ps.executeUpdate();
            return true;
        }catch ( Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean outRoom(int idUser, int idRoom) {
        String sql = "delete from tbluserinroom where userid=? and roomid=?";
        try {
            PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, idUser);
            ps.setInt(2, idRoom);
            ps.executeUpdate();
            return true;
        }catch ( Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<User> getUsersInRoom(int roomId) {
        List<User> listUser = new ArrayList<>();
        String sql =
        "select * from tbluser, tbluserinroom, tblroom where tbluser.id=tbluserinroom.userid and tbluserinroom.roomid=tblroom.id and tbluserinroom.roomid=?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, roomId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("tbluser.id"));
                user.setUsername(rs.getString("tbluser.username"));
                user.setPassword(rs.getString("tbluser.password"));
                user.setFullName(rs.getString("tbluser.fullname"));
                user.setEmail(rs.getString("tbluser.email"));
                user.setDateOfBirth(rs.getDate("tbluser.dob"));
                user.setPhoneNum(rs.getString("tbluser.phone"));
                user.setAddress(rs.getString("tbluser.address"));
                user.setAddress(rs.getString("tbluser.avatarurl"));
                listUser.add(user);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return listUser;
    }

    public boolean changeRoleUserInRom(int room, int user, String role) {
        boolean foundUserInRoom = checkUserInRoom(user, room);
        if (!foundUserInRoom) return false;
        try {
            String sql = "update tbluserinroom set role=? where roomid=? and userid=?";
            PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, room);
            ps.setInt(2, user);
            ps.setString(3, role);
            ps.executeUpdate();
            return true;
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return false;
    }
    
    public List<Room> getListRoom(int user) {
        List<Room> list = new ArrayList<Room>();
        try {
            String sql = "select tblroom.id, tblroom.name from tblroom, tbluserinroom where tbluserinroom.userid=? and tbluserinroom.roomid=tblroom.id";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, user);
        
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Room room = new Room();
                room.setId(rs.getInt("tblroom.id"));
                room.setName(rs.getString("tblroom.name"));
                room.setUserList(new UserInRoomDAO().getUsersInRoom(room.getId()));
                room.setMessages(new MessageDAO().getMessages(room.getId()));
                list.add(room);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
