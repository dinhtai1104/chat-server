/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interfacecontrol;

import java.util.List;
import model.User;

/**
 *
 * @author Admin
 */
public interface IUserCtr{
    public User checkLogin(User u);
    public boolean createAccount(User user);
    public boolean updateAccount(User user);
    public User getUser(int id);
    public List<User> findUsersByFullName(String key);
}
