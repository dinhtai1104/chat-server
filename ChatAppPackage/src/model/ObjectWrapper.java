/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;

/**
 *
 * @author Admin
 */
public class ObjectWrapper implements Serializable {
    private Object data;
    private ConnectionType choice;

    public ObjectWrapper() {
    }

    public ObjectWrapper(Object data, ConnectionType choice) {
        this.data = data;
        this.choice = choice;
    }

    
    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public ConnectionType getChoice() {
        return choice;
    }

    public void setChoice(ConnectionType choice) {
        this.choice = choice;
    }
    
}
