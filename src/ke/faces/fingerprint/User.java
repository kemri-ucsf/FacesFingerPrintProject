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
    private String firstName;
    private String lastName;
    private String designation;
    private static final Logger LOG = Logger.getLogger(User.class.getName());

    public User(String userName, String password, String firstName, String lastName, String designation) {
        this.userName = userName;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    @Override
    public String toString() {
        return "User{" + "userName=" + userName + ", password=" + password + ", firstName=" + firstName + ", lastName=" + lastName + ", designation=" + designation + '}';
    }
    
    
}
