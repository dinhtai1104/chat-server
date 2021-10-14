package dao;

import model.Room;
import model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO extends DAO{

    public RoomDAO() {
        super();
    }



    public Room createRoom(Room room) {
        String sql = "insert into tblroom(name) value(?)";

        try {
            PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, "");
//            ps.setDate(2, room.getCreateDate());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                room.setId(rs.getInt(1));
                System.out.println(room.getId());
            }
            return room;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Room getRoom(int id) {
        Room room = new Room();
        String sql = "select * from tblroom where id=?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                room.setId(rs.getInt("id"));
                room.setName(rs.getString("name"));
                room.setMessages(new MessageDAO().getMessages(id));
//                room.setCreateDate(rs.getDate("createat"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return room;
    }

    public boolean deleteRoom(int room) {
        String sql = "delete from tblroom where id=?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, room);
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
    


}
