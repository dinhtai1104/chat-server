package dao;

import model.ChatFile;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ChatFileDAO extends DAO{

    public ChatFileDAO() {
        super();
    }

    public boolean saveFile(ChatFile chatFile) {
        String sql = "insert into tblchatfile(name, url) value(?,?)";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, chatFile.getName());
            ps.setString(2, chatFile.getUrl());
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public ChatFile getFile(int id) {
        ChatFile chatFile = new ChatFile();
        String sql = "select * from tblchatfile where id=?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                chatFile.setId(rs.getInt("id"));
                chatFile.setName(rs.getString("name"));
                chatFile.setUrl(rs.getString("url"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return chatFile;
    }
}
