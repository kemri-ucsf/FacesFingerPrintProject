/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ke.faces.fingerprint;

import java.util.logging.Logger;

/**
 *
 * @author LENOVO USER
 */
public class User {
    private int userId;
    private String userName;
    private String password;    
    private String name;
    private String designation;   
    

    public User()
    {
        
    }
    public User(String userName, String password,  String name, String designation) {
        this.userName = userName;
        this.password = password;       
        this.name = name;
        this.designation = designation;
    }
    

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

   
    public String getName() {
        return name;
    }

    public void setName(String lastName) {
        this.name = lastName;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    @Override
    public String toString() {
        return "User{" + "userName=" + userName + ", password=" + password + ",  name=" + name + ", designation=" + designation + '}';
    }
    
    
}
