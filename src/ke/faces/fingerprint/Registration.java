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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
/**
 *
 * @author LENOVO USER
 */
public class Registration extends JPanel implements ActionListener{
       
    //Action Listeners contants
    public static final String ACT_SAVE="save__update_participant";
    public static final String ACT_DELETE="void_participant";
    public static final String ACT_CANCEL="cancel";
    public static final String ACT_BACK="exit";
    public static final String ACT_ENROLL="Enroll_Identify";
    public static final String ACT_FIND="search_participant";
    public static final String ACT_COUNTY="filterBeach";
    public static final Font font = new Font("Times New Roman", Font.PLAIN, 16);
    //Text boxes and combo boxes
    public static JTextField txt_Mname;
    public static JTextField txt_Fname;
    public static JTextField txt_Gname;
    public static JTextField txt_Nname;
    public static JTextField txt_Age;
    public static JTextField txt_Beachname;
    public static JTextField txt_Identifier;
    public static JComboBox cboGender;
    public static JComboBox cboLocation;
    public static JComboBox cboCty;
    public static JCheckBox chkfingerTaken;
    
    //Label controls
    private JLabel lbl_Mname;
    private JLabel lbl_Fname;
    private JLabel lbl_Gname;
    private JLabel lbl_Nname;
    private JLabel lbl_Gender;
    private JLabel lbl_Age;
    private JLabel lbl_Beachname;
    private JLabel lbl_title;
    private JLabel lbl_Identifier;
    private JLabel lbl_County;
    private JLabel lbl_FingerTaken;
    
    //Command Buttons
    public static JButton btnSave;
    public static JButton btnDelete;
    public static JButton btnEnroll;
    private JButton btnBack;
    public static JButton btnCancel;
    
    
    //Display panel
    JDialog dlgRegistration;
    public static String[] gender;
   
    
    private List<String> selectLocations;
    public static HashMap<String,Integer> accessList;
    
    private Reader  reader;
    private ReaderCollection readerCollection;
    public static Participant participant; //the new partarticipant or new new details of the selected participant
    public static Participant oldParticipant; //currently selected participant
    
    
    public Registration()
    {
        //log info
        FacesFingerPrintProject.logger.info("Creating Register Participant UI...");
        gender=new String[]{"Male","Female"}; //iniatialize an array
        dlgRegistration= new JDialog((JDialog)null, "PARTICIPANT REGISTRATION", true);
        dlgRegistration.setLayout(null);
        dlgRegistration.setBounds(200, 0,700, 500);
        participant = new Participant();
        
               
        lbl_title=new JLabel();
        lbl_title.setBounds(100, 10, 400, 20);         
        lbl_title.setText("PARTICIPANT REGISTRATION DETAILS");
        lbl_title.setFont(MainMenu.titleFont);         
        dlgRegistration.add(lbl_title);
        
        lbl_Identifier=new JLabel();
        lbl_Identifier.setBounds(50, 40, 100, 20);         
        lbl_Identifier.setText("Identifier:"); 
        lbl_Identifier.setVerticalAlignment(JLabel.TOP);
        lbl_Identifier.setFont(font);
        dlgRegistration.add(lbl_Identifier);
        
        txt_Identifier=new JTextField();
        txt_Identifier.setBounds(200, 40, 200, 25);          
        txt_Identifier.setFont(font);
        dlgRegistration.add(txt_Identifier);
        
        lbl_Fname=new JLabel();
        lbl_Fname.setBounds(50, 80, 100, 20);         
        lbl_Fname.setText("Family Name:");  
        lbl_Fname.setFont(font);
        //lbl_Fname.setFont(font);
        dlgRegistration.add(lbl_Fname);
        
        txt_Fname=new JTextField();
        txt_Fname.setBounds(200, 80, 200, 25);          
        txt_Fname.setFont(font);
        dlgRegistration.add(txt_Fname);
        
        lbl_Mname=new JLabel();
        lbl_Mname.setBounds(50, 120, 100, 20);         
        lbl_Mname.setText("Middle Name:");
        lbl_Mname.setFont(font);
        dlgRegistration.add(lbl_Mname);
        
        txt_Mname=new JTextField();
        txt_Mname.setBounds(200, 120, 200, 25);          
        txt_Mname.setFont(font);
        dlgRegistration.add(txt_Mname);
        
        lbl_Gname=new JLabel();
        lbl_Gname.setBounds(50, 160, 100, 20);         
        lbl_Gname.setText("First Name:");     
        lbl_Gname.setFont(font);
        dlgRegistration.add(lbl_Gname);
        
        txt_Gname=new JTextField();
        txt_Gname.setBounds(200, 160, 200, 25);          
        txt_Gname.setFont(font);
        dlgRegistration.add(txt_Gname);
        
        lbl_Nname=new JLabel();
        lbl_Nname.setBounds(50, 200, 100, 20);         
        lbl_Nname.setText("Nick Name:");     
        lbl_Nname.setFont(font);
        dlgRegistration.add(lbl_Nname);
        
        txt_Nname=new JTextField();
        txt_Nname.setBounds(200, 200, 200, 25);          
        txt_Nname.setFont(font);
        dlgRegistration.add(txt_Nname);
        
        lbl_Gender=new JLabel();
        lbl_Gender.setBounds(50, 240, 100, 20);         
        lbl_Gender.setText("Gender:");    
        lbl_Gender.setFont(font);
        dlgRegistration.add(lbl_Gender);
        
        cboGender=new JComboBox(gender);
        cboGender.setBounds(200, 240, 200, 25);          
        cboGender.setFont(font);
        dlgRegistration.add(cboGender);
        
        
        lbl_Age=new JLabel();
        lbl_Age.setBounds(50, 280, 100, 20);         
        lbl_Age.setText("Age:"); 
        lbl_Age.setFont(font);
        dlgRegistration.add(lbl_Age);
        
        txt_Age=new JTextField();
        txt_Age.setBounds(200, 280, 100, 25);          
        txt_Age.setFont(font);
        dlgRegistration.add(txt_Age);
        
        lbl_County=new JLabel();
        lbl_County.setBounds(50, 320, 100, 20);         
        lbl_County.setText("County:");    
        lbl_County.setFont(font);
        dlgRegistration.add(lbl_County);
        
        cboCty=new JComboBox(MainMenu.counties.toArray());
        cboCty.setBounds(200, 320, 200, 25);   
        cboCty.setActionCommand(ACT_COUNTY);
        cboCty.addActionListener(this);
        cboCty.setFont(font);
        dlgRegistration.add(cboCty);
        
        lbl_Beachname=new JLabel();
        lbl_Beachname.setBounds(50, 360, 100, 20);         
        lbl_Beachname.setText("Beach Name:");                 
        lbl_Beachname.setFont(font);
        dlgRegistration.add(lbl_Beachname);
        
        cboLocation=new JComboBox();
        cboLocation.setBounds(200, 360, 200, 25); 
        //cboLocation.setEditable(true);
        cboLocation.setFont(font);
        //cboLocation.sets
        dlgRegistration.add(cboLocation);
        
        lbl_FingerTaken=new JLabel();
        lbl_FingerTaken.setBounds(50, 380, 120, 20);         
        lbl_FingerTaken.setText("FingerPrint Taken:");                 
        lbl_FingerTaken.setFont(font);
        //dlgRegistration.add(lbl_FingerTaken);
        
        chkfingerTaken=new JCheckBox();
        chkfingerTaken.setBounds(200, 380, 120, 20);         
        //lbl_FingerTaken.setText("FingerPrint Taken:");                 
        chkfingerTaken.setFont(font);
       // dlgRegistration.add(chkfingerTaken);
        
        btnEnroll=new JButton("Enroll/Identify Finger");
        btnEnroll.setBounds(500, 40, 150, 40);
        btnEnroll.setActionCommand(ACT_ENROLL);
        btnEnroll.addActionListener(this);
        dlgRegistration.add(btnEnroll);
        
        btnSave=new JButton("Save Record");
        btnSave.setBounds(500, 100, 150, 40);
        btnSave.setActionCommand(ACT_SAVE);
        btnSave.addActionListener(this);
        btnSave.setEnabled(false);
        dlgRegistration.add(btnSave);
        
        btnCancel=new JButton("Update");
        btnCancel.setBounds(500, 160, 150, 40);
        btnCancel.setActionCommand(ACT_CANCEL);
        btnCancel.addActionListener(this);
        dlgRegistration.add(btnCancel);      
        
       // btnFind=new JButton("Find");
       // btnFind.setBounds(500, 220, 150, 40);
       // btnFind.setActionCommand(ACT_FIND);
       // btnFind.addActionListener(this);
       // dlgRegistration.add(btnFind);
        
        btnDelete=new JButton("Delete");
        btnDelete.setBounds(500, 220, 150, 40);
        btnDelete.setActionCommand(ACT_DELETE);
        btnDelete.addActionListener(this);
        btnDelete.setEnabled(false);
        dlgRegistration.add(btnDelete);
        
        btnBack=new JButton("Back");
        btnBack.setBounds(500, 280, 150, 40);
        btnBack.setActionCommand(ACT_BACK);
        btnBack.addActionListener(this);
        dlgRegistration.add(btnBack);
    }
    
     public void createAndShowGUI()
     {
         if(FindTable.selectedParticipant!=null)
         {
             oldParticipant=new Participant();
             oldParticipant=FindTable.selectedParticipant;
             txt_Gname.setText(oldParticipant.getGivenName());
             txt_Fname.setText(oldParticipant.getFamilyName());
             txt_Mname.setText(oldParticipant.getMiddleName());
             txt_Nname.setText(oldParticipant.getNickName());
             txt_Identifier.setText(oldParticipant.getIdentifier());
             txt_Age.setText(Integer.toString(oldParticipant.getAge()));
             
             if(oldParticipant.getGender()=='M')
             {
                 cboGender.setSelectedIndex(0);
             }
             else
             {
                 cboGender.setSelectedIndex(1);
             }
             
             //get the county and beach
             Beach b=MainMenu.beachMap.get(oldParticipant.getBeachId());
             cboCty.setSelectedItem((Object)b.getCounty());
             cboLocation.setSelectedItem(b.getName());
             btnDelete.setEnabled(true);
             
         }
         else
         {
             btnDelete.setEnabled(false);
             btnCancel.setEnabled(false);
         }
         dlgRegistration.setVisible(true);  
         
         
         dlgRegistration.dispose(); //close the app once done
     }
     
     private Reader getSelectedReader()
    { 
        /*
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
     
     public boolean validFields()
     {
         //boolean b_ok=true;
         
         if (txt_Fname.getText()==null)
         {
            // b_ok=false;
             JOptionPane.showMessageDialog(null, "Enter Participant's family name or last name");
             return false;
         }
         if (txt_Gname.getText()==null)
         {
             //b_ok=false;
             JOptionPane.showMessageDialog(null, "Enter Participant's Given name or christian name");
             return false;
         }
         
         if (txt_Age.getText()==null)
         {
            // b_ok=false;
             JOptionPane.showMessageDialog(null, "Enter Participant's Age");
             return false;
         }
         
         if (txt_Identifier.getText()==null)
         {
            // b_ok=false;
             JOptionPane.showMessageDialog(null, "Enter Participant's Id");
             return false;
         }
         else
         {
             
             if(txt_Identifier.getText().length()>6 || txt_Identifier.getText().length()<6)
             {
                  JOptionPane.showMessageDialog(null, "The Participant Id Must be 6 characters");
                  return false;
             }
             
             
         }
         
         if (txt_Age.getText()!=null)
         {
            try{
                Integer.parseInt(txt_Age.getText());
            }
            catch(NumberFormatException ex)
            {
                JOptionPane.showMessageDialog(null, "Enter Numberic value for Age");        
                return false;
            }
            // return false;
             
         }
         return true;
     }
     
     
     public void actionPerformed(ActionEvent e){
        if(e.getActionCommand().equals(ACT_BACK))
        {
            dlgRegistration.setVisible(false);
            return;
	}
       
        else if(e.getActionCommand().equals(ACT_ENROLL))
        {
            reader=getSelectedReader();
        
            if(null ==reader)
            {
                MessageBox.Warning("Reader is not selected");
            }
            else
            {
                FingerPrintDialog.Run(reader);
                // this.enrollmentFMD=
                
            }
	}
        else if(e.getActionCommand().equals(ACT_SAVE))
        {
            if(participant.getDublicateIdentifier(txt_Identifier.getText())>0)
             {
                 //b_ok=false;
                 JOptionPane.showMessageDialog(null, "This Participant's Id is Already Assigned to Someone else");
                 return;
             }
            if(validFields()==true)//check if all fields are correctly filled
            {
               
               participant.setIdentifier(txt_Identifier.getText());
               participant.setAge(Integer.parseInt(txt_Age.getText())); 
               participant.setFamilyName(txt_Fname.getText());
               participant.setGivenName(txt_Gname.getText());
               participant.setMiddleName(txt_Mname.getText());
               participant.setNickName(txt_Nname.getText());
               if (cboGender.getSelectedIndex()==0)
               {
                   participant.setGender('M');
               }
               else
               {
                  participant.setGender('F'); 
               }
               participant.setBeachId(accessList.get((String)cboLocation.getSelectedItem()));
               
               participant.saveParticipant();
               participant.saveAuditTrail();
              // JOptionPane.showMessageDialog(null, "Participant Record Successfully Saved... ");
              // btnSave.setEnabled(false);
            }
        }
        else if(e.getActionCommand().equals(ACT_DELETE))
        {
            if (oldParticipant.getParticipant_Id()>0)
            {
              if(validFields()==true)
              {
                  int dialogResult = JOptionPane.showConfirmDialog (null, "Do you want to delete this Participant?","Warning",JOptionPane.YES_NO_OPTION);
                    if(dialogResult == JOptionPane.YES_OPTION)
                    {
                        oldParticipant.deleteParticipant();
                        oldParticipant.deleteAuditTrail();
                        return;
                    }
                  
              }
            }
        }
        else if(e.getActionCommand().equals(ACT_CANCEL))
        {
            if(btnCancel.getText()=="Update")
            {
                
                if (oldParticipant!=null)
                {
                    if(validFields()==true)//check if all fields are correctly filled
                    {
                        participant.setIdentifier(txt_Identifier.getText());
                        participant.setAge(Integer.parseInt(txt_Age.getText())); 
                        participant.setFamilyName(txt_Fname.getText());
                        participant.setGivenName(txt_Gname.getText());
                        participant.setMiddleName(txt_Mname.getText());
                        participant.setNickName(txt_Nname.getText());
                        if (cboGender.getSelectedIndex()==0)
                        {
                            participant.setGender('M');
                        }
                        else
                        {
                            participant.setGender('F'); 
                        }
                        participant.setBeachId(accessList.get((String)cboLocation.getSelectedItem()));
                        participant.setParticipant_Id(oldParticipant.getParticipant_Id());
                        
                        if(!participant.equals(oldParticipant))
                        {
                           participant.updateParticipant(); 
                           participant.updateAuditTrail(oldParticipant);
                        }
                        
              // 
                    }
                    else
                    {
                        return;
                    }
                }
                else
                {
                    JOptionPane.showMessageDialog(null, "There is No Record to Update... ");
                }
                return;
                
            }
            else if(btnCancel.getText()=="Cancel")
            {
                txt_Identifier.setText("");
                txt_Age.setText("");
                txt_Fname.setText("");
                txt_Gname.setText("");
                txt_Mname.setText("");
                txt_Nname.setText("");
                btnCancel.setText("Update");
                btnSave.setEnabled(false);
                return;
                
            }
            
        }
        else if(e.getActionCommand().equals(ACT_COUNTY))
        {
            selectLocations=new ArrayList<String>();
            accessList=new HashMap<String,Integer>();
            cboLocation.removeAllItems();//clear the locations combobox
            for(Beach b:MainMenu.beachList)
            {
                if(b.getCounty().equalsIgnoreCase((String)cboCty.getSelectedItem()))
                {
                   // cboLocation.addI
                    selectLocations.add(b.getName());
                    accessList.put(b.getName(), b.getBeachId());
                }
            }
            String[] selectArray=null;
            selectArray=new String[selectLocations.size()];//set selectArray Size
            selectLocations.toArray(selectArray); //set the elements of select Array
            
            //fill the locations/beach names combo box
            
            for(int i=0;i<selectArray.length;i++)
            {
                cboLocation.addItem(selectArray[i]);
            }
        }
        
        
        
     }
}
