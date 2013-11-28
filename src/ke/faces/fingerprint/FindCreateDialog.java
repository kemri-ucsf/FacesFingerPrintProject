/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ke.faces.fingerprint;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author LENOVO USER
 */
public class FindCreateDialog extends JPanel implements ActionListener{
 
    public static final String ACT_FIND="find_search";
    public static final String ACT_CREATE="creat_new";
    public static final String ACT_BACK="exit";
    public static final String ACT_VIEW="view_participant";
    
    public static JDialog dlgFindCreate;
    public static final Font font = new Font("Times New Roman", Font.BOLD, 14);
    public static final Font font2 = new Font("Times New Roman", Font.PLAIN, 14);
    
    private JLabel lbl_Search;
    private JLabel lbl_Title;
    
    private JTextField txt_Search;
    
    private JButton btnBack;
    private JButton btnFind;
    private JButton btnCreate;
    private JButton btnView;
   
   
    
    FindTable findTable;
    FindCreateDialog()
    {
        //log info
        FacesFingerPrintProject.logger.info("Creating Find/Create UI...");
        dlgFindCreate= new JDialog((JDialog)null, "FIND/CREATE PARTICIPANT FORM", true);
        dlgFindCreate.setLayout(null);
        dlgFindCreate.setBounds(200, 0,600, 500);
        
        lbl_Title=new JLabel();
        lbl_Title.setBounds(10, 10, 300, 20);         
        lbl_Title.setText("FIND/CREATE PARTICIPANT");
        lbl_Title.setFont(MainMenu.titleFont);         
        dlgFindCreate.add(lbl_Title);
        
       // txt_Identifier=new JTextField();
       // txt_Identifier.setBounds(10, 100, 90, 30);         
       // txt_Identifier.setText("Identifer"); 
       // txt_Identifier.setEditable(false);
       // txt_Identifier.setFont(font);
       // dlgFindCreate.add(txt_Identifier);
        
       // txt_Name=new JTextField();
       // txt_Name.setBounds(100, 100, 200, 30);         
       // txt_Name.setText("Name"); 
       // txt_Name.setEditable(false);
       // txt_Name.setFont(font);
       // dlgFindCreate.add(txt_Name);
        
       // txt_County=new JTextField();
       // txt_County.setBounds(300, 100, 100, 30);         
       // txt_County.setText("County"); 
       // txt_County.setEditable(false);
       // txt_County.setFont(font);
       // dlgFindCreate.add(txt_County);
        
       // txt_Beach=new JTextField();
       // txt_Beach.setBounds(400, 100, 100, 30);         
       // txt_Beach.setText("Beach"); 
       // txt_Beach.setEditable(false);
       // txt_Beach.setFont(font);
       // dlgFindCreate.add(txt_Beach);
        
        lbl_Search=new JLabel();
        lbl_Search.setBounds(10, 40, 350, 20);         
        lbl_Search.setText("Enter Participant's Identifier or Participant's Name:");
        lbl_Search.setFont(font2);     
        dlgFindCreate.add(lbl_Search);
        
        txt_Search=new JTextField();
        txt_Search.setBounds(320, 40, 200, 25);         
        txt_Search.setText(""); 
        //txt_Search.setEditable(false);
        txt_Search.setFont(font);
        dlgFindCreate.add(txt_Search);
        
        btnFind=new JButton("....");
        btnFind.setBounds(520, 40, 30, 25); 
        btnFind.setActionCommand(ACT_FIND);
        btnFind.addActionListener(this);
       // btnSave.setEnabled(false);
        dlgFindCreate.add(btnFind);
        
        
        
        findTable=new FindTable();
        findTable.setBounds(10, 80, 550, 300);
        dlgFindCreate.add(findTable);
        
       btnView=new JButton("View Participant");
       btnView.setBounds(10, 400, 150, 35); 
       btnView.setActionCommand(ACT_VIEW);
       btnView.addActionListener(this);
       // btnSave.setEnabled(false);
       dlgFindCreate.add(btnView);
       
       btnCreate=new JButton("Create Participant");
       btnCreate.setBounds(200, 400, 150, 35); 
       btnCreate.setActionCommand(ACT_CREATE);
       btnCreate.addActionListener(this);
       // btnSave.setEnabled(false);
       dlgFindCreate.add(btnCreate);
       
       btnBack=new JButton("Back");
       btnBack.setBounds(400, 400, 150, 35); 
       btnBack.setActionCommand(ACT_BACK);
       btnBack.addActionListener(this);
       // btnSave.setEnabled(false);
       dlgFindCreate.add(btnBack);
    }
    public void createAndShowGUI()
     {
         dlgFindCreate.setVisible(true);          
         dlgFindCreate.dispose(); //close the app once soen
     }
    
     public void actionPerformed(ActionEvent e){
        if(e.getActionCommand().equals(ACT_BACK))
        {
            dlgFindCreate.setVisible(false);
            return;
	}
        else if(e.getActionCommand().equals(ACT_CREATE))
        {
              FindTable.selectedParticipant=null;       
              //load registration form
              Registration registration=new Registration();
              registration.createAndShowGUI();
	}
        else if(e.getActionCommand().equals(ACT_VIEW))
        {
            if (findTable.getRowCount()==0)
            {
                    return;
            }
             else
             {
                    if(FindTable.selectedParticipant!=null)
                    {
                        //Load the registration form
                        FindCreateDialog.dlgFindCreate.setVisible(false); //close find create dialog
                        Registration registration=new Registration();
                        registration.createAndShowGUI();                                    
                     }
                    
                }
            return;
	}
        else  if(e.getActionCommand().equals(ACT_FIND))
        {           
            String search;
            search=txt_Search.getText();
            if (search.length()>=3)
            {
                findTable.insertRow(search);         
                    
            }
            else
            {
                JOptionPane.showMessageDialog(null,"Enter atleast three characters then click the search/Find Button");
                FacesFingerPrintProject.logger.info("Enter atleast three characters then click the search/Find Button...");
            }
            
	}
         
     }
        
}
