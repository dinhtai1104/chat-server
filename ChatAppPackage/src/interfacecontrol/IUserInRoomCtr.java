/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interfacecontrol;

import model.Room;

/**
 *
 * @author Admin
 */
public interface IUserInRoomCtr{
    public boolean checkRoomCreated(int u1, int u2);
    public Room getRoomOfJust2People(int u1, int u2);
    public boolean checkUserInRoom(int idUser, int idRoom);
    public boolean joinRoom(int idUser, int idRoom);
}
