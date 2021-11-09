package dao;

import model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDAO extends DAO{

    public UserDAO() {
        super();
    }

    public User checkLogin(User user) {
        if (con == null) return null;
        String sql = "select * from tbluser where username=? and password=?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                FriendRequestDAO frqdao = new FriendRequestDAO();
                user.setId(rs.getInt("id"));
                user.setFullName(rs.getString("fullname"));
                user.setAddress(rs.getString("address"));
                user.setDateOfBirth(rs.getDate("dob"));
                user.setEmail(rs.getString("email"));
                user.setPhoneNum(rs.getString("phone"));
                user.setAvatarUrl(rs.getString("avatarurl"));
                user.setStatus(1);
                user.setFriendList(new FriendDAO().getFriends(user.getId()));
                user.setRoomList(new UserInRoomDAO().getListRoom(user.getId()));
                user.setFriendRequestList(new FriendRequestDAO().getFriendRequests(user.getId()));
                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean createAccount(User user) {
        if (isExistUsername(user.getUsername())) {
            return false;
        }
        String sql = "insert into tbluser(username, password, email, phone, address, dob, fullname, createat, status) value(?,?,?,?,?,?,?,?,?)";
        try {
            PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPhoneNum());
            ps.setString(5, user.getAddress());
            ps.setDate(6, user.getDateOfBirth());
            ps.setString(7, user.getFullName());
            ps.setDate(8, null);
            ps.setInt(9, 0);
            int rs = ps.executeUpdate();
            return true;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean isExistUsername(String username) {
        String sql = "select * from tbluser where username=?";
        try {
            PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }
            return false;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteAccount(int idUser) {
        String sql = "delete from tbluser where id=?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, idUser);
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean updateAccount(User user) {
        String sql = "update tbluser set password=?,email=?,fullname=?,address=?,dob=?,phone=?,avatarurl=? where id=?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, user.getPassword());
            ps.setString(2, user.getUsername());
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getAddress());
            ps.setDate(5, user.getDateOfBirth());
            ps.setString(6, user.getPhoneNum());
            ps.setString(7, user.getAvatarUrl());
            ps.setInt(8, user.getId());
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public User getUser(int id) {
        User user = new User();
        String sql = "select * from tbluser where id=?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setAddress(rs.getString("address"));
                user.setEmail(rs.getString("email"));
                user.setPhoneNum(rs.getString("phone"));
                user.setDateOfBirth(rs.getDate("dob"));
                user.setFullName(rs.getString("fullname"));
                user.setAvatarUrl(rs.getString("avatarurl"));
                user.setStatus(rs.getInt("status"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    public List<User> findUsersByFullName(String key) {
        List<User> listUser = new ArrayList<>();
        String sql = "select * from tbluser where fullname like ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, "%" + key + "%");
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setAddress(rs.getString("address"));
                user.setEmail(rs.getString("email"));
                user.setPhoneNum(rs.getString("phone"));
                user.setDateOfBirth(rs.getDate("dob"));
                user.setFullName(rs.getString("fullname"));
                user.setAvatarUrl(rs.getString("avatarurl"));
                user.setStatus(rs.getInt("status"));
                listUser.add(user);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return listUser;
    }

    public List<User> findUsersByPhoneNum(String key) {
        List<User> listUser = new ArrayList<>();
        String sql = "select * from tbluser where phone like ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, "%" + key + "%");
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
               User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setAddress(rs.getString("address"));
                user.setEmail(rs.getString("email"));
                user.setPhoneNum(rs.getString("phone"));
                user.setDateOfBirth(rs.getDate("dob"));
                user.setFullName(rs.getString("fullname"));
                user.setAvatarUrl(rs.getString("avatarurl"));
                user.setStatus(rs.getInt("status"));
                listUser.add(user);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return listUser;
    }

    public List<User> findUsersByEmail(String key) {
        List<User> listUser = new ArrayList<>();
        String sql = "select * from tbluser where email like ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, "%" + key + "%");
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setAddress(rs.getString("address"));
                user.setEmail(rs.getString("email"));
                user.setPhoneNum(rs.getString("phone"));
                user.setDateOfBirth(rs.getDate("dob"));
                user.setFullName(rs.getString("fullname"));
                user.setAvatarUrl(rs.getString("avatarurl"));
                user.setStatus(rs.getInt("status"));
                listUser.add(user);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return listUser;
    }

    public void setOnlineOffline(int idUser, boolean b) {
        String sql = "update tbluser set status=? where id=?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, b ? 1 : 0);
            ps.setInt(2, idUser);
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        System.out.println(new UserDAO().findUsersByFullName("sp").get(0).getFullName());
    }


}
