package dao;

import model.Message;
import model.Room;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.User;

public class MessageDAO extends DAO{

    public MessageDAO() {
        super();
    }

    public boolean saveMessage(Message message) {
        String sql = "insert into tblmessage(authorid, room, content, status, createat) value(?,?,?,?,?)";
        try {
            PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, message.getAuthor().getId());
            ps.setInt(2, message.getRoom().getId());
            ps.setString(3, message.getContent());
            ps.setString(4, message.getStatus());
            ps.setDate(5, message.getCreateDate());
            ps.executeUpdate();
            return true;
        }catch ( Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Message> getMessages(int room) {
        List<Message> mess = new ArrayList<>();

        String sql = "select * from tblmessage,tblroom,tbluser where tblmessage.room=? and tblmessage.room=tblroom.id and tbluser.id=tblmessage.authorid";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, room);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Message message = new Message();
                message.setId(rs.getInt("tblmessage.id"));
                
                User user = new User();
                user.setId(rs.getInt("tbluser.id"));
                user.setFullName(rs.getString("tbluser.fullname"));
                message.setAuthor(user);
                Room roomOb = new Room();
                roomOb.setId(rs.getInt("tblroom.id"));                
                roomOb.setName(rs.getString("tblroom.name"));
                message.setRoom(roomOb);
                message.setFileChat(null);
                message.setCreateDate(rs.getDate("tblmessage.createat"));
                message.setStatus(rs.getString("tblmessage.status"));
                message.setContent(rs.getString("tblmessage.content"));
                mess.add(message);
            }

        }catch ( Exception e) {
            e.printStackTrace();
        }
        return mess;
    }

    public boolean deleteMessage(int message) {
        String sql = "delete from tblmessage where id=?";
        try {
            PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, message);
            ps.executeUpdate();
            return true;
        }catch ( Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
