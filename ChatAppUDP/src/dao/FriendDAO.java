package dao;

import model.FriendRequest;
import model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import model.Room;

public class FriendDAO extends DAO{

    public FriendDAO() {
        super();
    }

    public List<User> getFriends(int user) {
        UserDAO userDAO = new UserDAO();

        List<User> users = new ArrayList<>();
        String sql = "select idfriend1, idfriend2 from tblfriend where idfriend1=? or idfriend2=?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, user);
            ps.setInt(2, user);

            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                int idfriend1 = rs.getInt("idfriend1");
                int idfriend2 = rs.getInt("idfriend2");
                if (idfriend1 == user) {
                    User userr = userDAO.getUser(idfriend2);
                    users.add(userr);
                } else {
                    User userr = userDAO.getUser(idfriend1);
                    users.add(userr);
                }
                
            }

        } catch ( Exception e) {

        }
        return users;
    }

    public boolean addFriend(FriendRequest fr) {

        String sql = "insert into tblfriend(idfriend1, idfriend2, createat) value(?,?,?)";
        try {
            PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, fr.getReceiver().getId());
            ps.setInt(2, fr.getSender().getId());
            ps.setDate(3, new Date(Calendar.getInstance().getTimeInMillis()));
            ps.executeUpdate();
            
            RoomDAO rDao = new RoomDAO();
            UserInRoomDAO userInRoomDAO = new UserInRoomDAO();
            Room room = new Room();
            room.setCreateDate(new Date(Calendar.getInstance().getTimeInMillis()));
            room.setName(fr.toString());
            room = rDao.createRoom(room);
            if (room != null) {
                userInRoomDAO.joinRoom(fr.getReceiver().getId(), room.getId());
                userInRoomDAO.joinRoom(fr.getSender().getId(), room.getId());
                return true;
            }
            return true;

        } catch ( Exception e) {

        }
        return false;
    }

    public boolean deleteFriend(int friend1, int friend2) {
        String sql = "delete from tblfriend where (idfriend1=? and idfriend2=?) or (idfriend1=? and idfriend2=?)";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, friend1);
            ps.setInt(2, friend2);
            ps.setInt(3, friend2);
            ps.setInt(4, friend1);
            ps.executeUpdate();
            return true;

        } catch ( Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
