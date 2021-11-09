/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interfacecontrol;

import java.util.List;
import model.FriendRequest;

/**
 *
 * @author Admin
 */
public interface IFriendRequestCtr{
    public boolean sendRequest(FriendRequest friendRequest);
    public List<FriendRequest> getFriendRequests (int user);
    public boolean deleteRequest(FriendRequest request);
    public boolean acceptRequest(FriendRequest request);
}
