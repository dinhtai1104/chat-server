/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interfacecontrol;
import java.util.List;
import model.Room;
import model.User;

/**
 *
 * @author Admin
 */
public interface IRoomCtr{
    public Room createRoom(Room room);
    public Room getRoom(int id);
    public boolean deleteRoom(int room);
    public void createRoom(List<User> list);
}
