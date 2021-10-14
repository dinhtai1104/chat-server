package dao;

import model.FriendRequest;
import model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FriendRequestDAO extends DAO{

    public FriendRequestDAO() {
        super();
    }

    private boolean checkSentRequest(int u1, int u2) {
        String sql = "select * from tblfriendrequest where (receiver=? and sender=?) or (receiver=? and sender=?)";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, u1);
            ps.setInt(2, u2);
            ps.setInt(3, u2);
            ps.setInt(4, u1);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return  true;
            }
            else return false;
        } catch ( Exception e ) {
            e.printStackTrace();;
        }
        return false;
    }
    public boolean sendRequest(FriendRequest friendRequest) {
        if (checkSentRequest(friendRequest.getReceiver().getId(), friendRequest.getSender().getId()))
            return false;
        String sql = "insert into tblfriendrequest(sender, receiver, createat) value(?,?,?)";
        try {
            PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, friendRequest.getSender().getId());
            ps.setInt(2, friendRequest.getReceiver().getId());
            ps.setDate(3, new Date(Calendar.getInstance().getTimeInMillis()));
            ps.executeUpdate();
            return true;
        } catch ( Exception e ) {
            e.printStackTrace();;
        }
        return false;
    }



    public List<FriendRequest> getFriendRequests (int user) {
        List<FriendRequest> requests = new ArrayList<>();
        String sql = "select * from tblfriendrequest where tblfriendrequest.receiver=? or tblfriendrequest.sender=?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, user);
            ps.setInt(2, user);
            ResultSet rs=ps.executeQuery();
            while(rs.next()) {
//                FriendRequest fr = new FriendRequest();
//                fr.setId(rs.getInt("id"));
//                fr.setReceiver(userDAO.getUser(user));
//                fr.setSender(userDAO.getUser(rs.getInt("sender")));
//                fr.setCreateDate(rs.getDate("createat"));
//                requests.add(fr);
                User sender = new UserDAO().getUser(rs.getInt("tblfriendrequest.sender"));
                User receiver = new UserDAO().getUser(rs.getInt("tblfriendrequest.receiver"));
                FriendRequest fr = new FriendRequest();
                fr.setReceiver(receiver);
                fr.setSender(sender);
                requests.add(fr);
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        System.out.println("Get " + requests.size() + " Frriend Request of " + user);
        return requests;
    }

    public boolean deleteRequest(FriendRequest request) {
        String sql = "delete from tblfriendrequest where (receiver=? and sender=?) or (receiver=? and sender=?)";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, request.getReceiver().getId());
            ps.setInt(2, request.getSender().getId());
            ps.setInt(3, request.getSender().getId());
            ps.setInt(4, request.getReceiver().getId());
            ps.executeUpdate();
            return true;
        } catch ( Exception e ) {
            e.printStackTrace();;
        }
        return false;
    }

    public boolean acceptRequest(FriendRequest request) {
        deleteRequest(request);
        FriendDAO friendDAO = new FriendDAO();

        return friendDAO.addFriend(request);
    }

}
