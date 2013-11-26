/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ke.faces.fingerprint;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 *
 * @author LENOVO USER
 */
public class LoginDialog extends JPanel implements ActionListener{
    public static final String ACT_LOGIN="login";
    public static final String ACT_BACK="exit";
    
    private static JDialog dlgLoginForm;
    private JButton btnBack;
    public JButton btnLogin; 
    
    public static JTextField txt_uName;
    public static JPasswordField txt_Pass;
    
    public static final Font font = new Font("Times New Roman", Font.PLAIN, 16);
    private JLabel lbl_uName;
    private JLabel lbl_Password;
    private JLabel lbl_Title;
    private User user;
    private Sql db;
    public LoginDialog()
    {
        user =new User();
        db=new Sql();
        dlgLoginForm= new JDialog((JDialog)null, "LOGIN", true);
        dlgLoginForm.setLayout(null);
        dlgLoginForm.setBounds(200, 0,350, 300);
        
        lbl_Title=new JLabel();
        lbl_Title.setBounds(10, 10, 200, 20);         
        lbl_Title.setText("LOGIN DETAILS");
        lbl_Title.setFont(MainMenu.titleFont);         
        dlgLoginForm.add(lbl_Title);
        
        lbl_uName=new JLabel();
        lbl_uName.setBounds(10, 40, 100, 20);         
        lbl_uName.setText("User Name:"); 
        lbl_uName.setVerticalAlignment(JLabel.TOP);
        lbl_uName.setFont(font);
        dlgLoginForm.add(lbl_uName);
        
        txt_uName=new JTextField();
        txt_uName.setBounds(100, 40, 170, 25);          
        txt_uName.setFont(font);
        dlgLoginForm.add(txt_uName);
        
        lbl_Password=new JLabel();
        lbl_Password.setBounds(10, 80, 100, 20);         
        lbl_Password.setText("Password:");  
        lbl_Password.setFont(font);
        //lbl_Fname.setFont(font);
        dlgLoginForm.add(lbl_Password);
        
        txt_Pass=new JPasswordField();
        txt_Pass.setBounds(100, 80, 170, 25);          
        txt_Pass.setFont(font);
        dlgLoginForm.add(txt_Pass);
        
        btnLogin=new JButton("Login");
        btnLogin.setBounds(10, 160, 100, 40);
        btnLogin.setActionCommand(ACT_LOGIN);
        btnLogin.addActionListener(this);
       // btnSave.setEnabled(false);
        dlgLoginForm.add(btnLogin);
        
        btnBack=new JButton("Cancel");
        btnBack.setBounds(200, 160, 100, 40);
        btnBack.setActionCommand(ACT_BACK);
        btnBack.addActionListener(this);
        dlgLoginForm.add(btnBack);
    }
     public static void createAndShowGUI()
     {
         LoginDialog login = new LoginDialog();
         FacesFingerPrintProject.logger.info("Loading The Log on Page");
         dlgLoginForm.setVisible(true);          
         dlgLoginForm.dispose(); //close the app once soen
     }
     
     public void actionPerformed(ActionEvent e)
     {
        if(e.getActionCommand().equals(ACT_BACK))
        {
            dlgLoginForm.setVisible(false);
            return;
	}
        else if(e.getActionCommand().equals(ACT_LOGIN))
        {
            //dlgLoginForm.setVisible(false);
            if(txt_uName.getText()==null)
            {
                JOptionPane.showMessageDialog(null, "Enter User name...");
                return;
            }
            if(txt_Pass.getText()==null)
            {
                JOptionPane.showMessageDialog(null, "Enter Password...");
                return;
            }
            String pass=txt_Pass.getText();
            String uName=txt_uName.getText();
            db.Open();//open/create connection to the db
            user=db.getUser(uName, pass);
             db.Close();
           // user=new User();
           // user.setName("Admin");
          //  user.setUserId(1);
         //   user.setUserName("Admin");
               
              if(user!=null)
               {
                   FacesFingerPrintProject.logger.info("Sucessfull Log on");
                   dlgLoginForm.setVisible(false);  
                   MainMenu.createAndShowGUI(user);
              }
              else
               {
                    FacesFingerPrintProject.logger.info("Failled Loging on User: "+txt_uName.getText());
                    JOptionPane.showMessageDialog(null, "Invalid User name or Password Try Again...");
                    return;
               }
          
            return;
	}
         
     }
        
    
}
