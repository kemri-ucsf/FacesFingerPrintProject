/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ke.faces.fingerprint;

import com.digitalpersona.uareu.Reader;
import com.digitalpersona.uareu.ReaderCollection;
import com.digitalpersona.uareu.UareUException;
import com.digitalpersona.uareu.UareUGlobal;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 *
 * @author LENOVO USER
 */
public class MainMenu extends JFrame implements ActionListener{
    
    public static final String ACT_EXIT="Exit_Application";
    public static final String ACT_ENROLL="Enroll_Participant";
    public static final String ACT_IDENTIFY="Identify_Participant";
    public static final String ACT_BEACH="beach";
    public static final String ACT_REPORT="report";
    public static final String ACT_USER="user";
    public static final Font titleFont = new Font("Times New Roman", Font.BOLD, 20);
    
    public static Identification identify;
    public static Reader reader;
    
    private JDialog menuDialog;
    private JLabel title;
    private JLabel title2;
    private JButton btnExit;
    private JButton btnEnroll;
    private JButton btnIdentify;
    private JButton btnBeach;
    private JButton btnUser;
    private JButton btnReport;
    private JLabel txt_User;
    private ReaderCollection readerCollection;
    
   public static List<String> counties; //store preloaded county details
   public static List<Beach> beachList; // store preloaded beach infomation
   public static Map<Integer,Beach> beachMap; // store preloaded beach Map infomation
   public static List<String> locations; // store preloaded beach Map infomation
   private Sql db=new Sql();
   public static User gUser;
    public MainMenu(User user)
    {
        gUser=user; 
        loadBeach();
        menuDialog= new JDialog((JDialog)null, "FISHERMEN FINGERPRINTING PROJECT", true);        
         menuDialog.setLayout(null);
         menuDialog.setBounds(200, 0,750, 600);
//        identify=new Identification();
         
         title=new JLabel();
         title.setBounds(150, 10, 300, 20);         
         title.setText("APPLICATION'S MAIN MENU");
         title.setFont(titleFont);
         //title.setAlignmentX(TOP_ALIGNMENT);
         menuDialog.add(title);
         
         btnEnroll=new JButton("Find/Create Participant");
         btnEnroll.setBounds(10, 50, 200, 60);
         btnEnroll.setActionCommand(ACT_ENROLL);
         btnEnroll.addActionListener(this);
         menuDialog.add(btnEnroll);
         
         
         btnIdentify=new JButton("Identify Participant");
         btnIdentify.setBounds(10, 130, 200, 60);
         btnIdentify.setActionCommand(ACT_IDENTIFY);
         btnIdentify.addActionListener(this);
         menuDialog.add(btnIdentify);
         
         btnBeach=new JButton("Add/Edit Beach");
         btnBeach.setBounds(10, 210, 200, 60);
         btnBeach.setActionCommand(ACT_BEACH);
         btnBeach.addActionListener(this);
         menuDialog.add( btnBeach);
         
         
         btnReport=new JButton("View Reports");
         btnReport.setBounds(10, 290, 200, 60);
         btnReport.setActionCommand(ACT_REPORT);
         btnReport.addActionListener(this);
         menuDialog.add( btnReport);
         
         btnUser=new JButton("User Management");
         btnUser.setBounds(10, 370, 200, 60);
         btnUser.setActionCommand(ACT_USER);
         btnUser.addActionListener(this);
         menuDialog.add( btnUser);
         
         
         btnExit=new JButton("Exit");
         btnExit.setBounds(10, 460, 200, 60);
         btnExit.setActionCommand(ACT_EXIT);
         btnExit.addActionListener(this);
         menuDialog.add(btnExit);
         
         //load/display image in a label
         ImageIcon image=new ImageIcon("images\\fingerprintImageSmall.jpg");
         title2 = new JLabel();
         title2.setBounds(300,100,350,350);//some random value that I know is in my dialog
         title2.setHorizontalAlignment(JLabel.CENTER);
         title2.setVerticalAlignment(JLabel.TOP);
         title2.setIcon(image);         
         menuDialog.add(title2);
         
         txt_User=new JLabel();
         txt_User.setBounds(400,480,200,25);
         txt_User.setText("User:  "+gUser.getName());
         txt_User.setFont(titleFont);
         menuDialog.add(txt_User);
        
        
          menuDialog.setVisible(true);
          
          menuDialog.dispose(); //close the app once soen
          validate(); //ensure that Image is loaded
    }
    private static final Logger LOG = Logger.getLogger(MainMenu.class.getName());
    
    /*
     * Load all beach details and count information into memory
     */
    private void loadBeach()
    {
        locations =new ArrayList<String>();
        db=new Sql();
        try
        {
            beachList=db.getAllBeachData();            
            beachMap=db.getBeachMap();
            counties=db.getCounty();
        }
        catch(SQLException ex)
        {
            JOptionPane.showMessageDialog(null, ex.getMessage());
            ex.printStackTrace();  
        }
        
            for(Beach b:beachList)
            {
                if(b.getName()!=null)
                {
                   locations.add(b.getName()+"-"+b.getCounty()); 
                }
                
            }
    }
     
    public Reader getSelectedReader()
    { /*
            * Get the plugged in U n U Digital Persona Read
        */
   
        try{
        readerCollection = UareUGlobal.GetReaderCollection();
        readerCollection.GetReaders();
	} 
	catch(UareUException e) { 
            MessageBox.DpError("ReaderCollection.GetReaders()", e);
	}
     
        if(0 == readerCollection.size()) return null;
        return readerCollection.get(0);
    
    }
    
    public static void createAndShowGUI(User user) {
		MainMenu paneContent = new MainMenu(user);
		
		//initialize capture library by acquiring reader collection
		try{
			paneContent.readerCollection = UareUGlobal.GetReaderCollection();
		}
		catch(UareUException e) {
			MessageBox.DpError("UareUGlobal.getReaderCollection()", e);
			return;
		}

		//run dialog
		//JDialog dlg = new JDialog((JDialog)null, "UareU SDK 2.x Java sample application", true);
		//paneContent.doModal(dlg);
		
		//release capture library by destroying reader collection
		try{
			UareUGlobal.DestroyReaderCollection();
		}
		catch(UareUException e) {
			MessageBox.DpError("UareUGlobal.destroyReaderCollection()", e);
		}
    }
    
    public void actionPerformed(ActionEvent e){
        if(e.getActionCommand().equals(ACT_EXIT)){
            menuDialog.setVisible(false);
	}
        else if(e.getActionCommand().equals(ACT_ENROLL)){
                        
               // Registration registration=new Registration();
              //  registration.createAndShowGUI();   
            FindCreateDialog find=new FindCreateDialog();
            find.createAndShowGUI();
            
	}
        else if(e.getActionCommand().equals(ACT_IDENTIFY)){
                FindTable.selectedParticipant=null;         
                Registration registration=new Registration();
                registration.createAndShowGUI();            
            
	}
        else if(e.getActionCommand().equals(ACT_BEACH)){
                        
                BeachForm beach=new BeachForm ();
                beach.createAndShowGUI();            
            
	}
        else if(e.getActionCommand().equals(ACT_REPORT)){
                        
            try
            {
                Reporting report=new Reporting();
                report.generateReport();
               // report.loadBeachLocations();
            }
           catch(IOException ex)
           {
               ex.printStackTrace();
               JOptionPane.showMessageDialog(null, ex.getMessage());
               Logger.getLogger(Reporting.class.getName()).log(Level.SEVERE, null, ex);  
           }
                        
            
	}
    }
    
}
