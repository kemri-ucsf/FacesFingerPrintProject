/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ke.faces.fingerprint;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author LENOVO USER
 */
public class BeachForm extends JPanel implements ActionListener{
    
     //Action Listeners contants
    public static final String ACT_SAVE="save__update_Beach";
    public static final String ACT_DELETE="void_Beach";
    public static final String ACT_UPDATE="update_Beach";
    public static final String ACT_CANCEL="cancel";
    public static final String ACT_BACK="exit";
    
    public static final Font font = new Font("Times New Roman", Font.PLAIN, 16);
    private JLabel lbl_Bname;
    private JLabel lbl_Description;
    private JLabel lbl_Title;
    private JLabel lbl_Title2;
    private JLabel lbl_County;
    
    private JPanel test;
    
    public static JTextField txt_Bname;
    public static JTextField txt_Description;
    
    public static JButton btnSave;
    public static JButton btnUpdate;
    public static JButton btnDelete;
    private JButton btnBack;
    public JButton btnCancel;    
    private JDialog dlgBeachForm;
    private JList lstLocations;
    public static List<String> locations;
    public static List<Beach> beachList;
    private Sql db;
    public static Beach beach;
    public static Beach oldBeach;
    public static JComboBox cboCounty;
    private BeachListPanel list;
    
    public static String[] county;
    BeachForm()
    {
        oldBeach = new Beach();
        county=new String[]{"Select County","Kisumu","Homabay","Migori","Busia","Siaya"};
        loadBeach();
        dlgBeachForm= new JDialog((JDialog)null, "BEACH DETAILS FORM", true);
        dlgBeachForm.setLayout(null);
        dlgBeachForm.setBounds(200, 0,450, 400);
        
        
        
        lbl_Title=new JLabel();
        lbl_Title.setBounds(100, 10, 300, 20);         
        lbl_Title.setText("BEACH DETAILS");
        lbl_Title.setFont(MainMenu.titleFont);         
        dlgBeachForm.add(lbl_Title);
        
        lbl_Bname=new JLabel();
        lbl_Bname.setBounds(10, 40, 100, 20);         
        lbl_Bname.setText("Name:"); 
        lbl_Bname.setVerticalAlignment(JLabel.TOP);
        lbl_Bname.setFont(font);
        dlgBeachForm.add(lbl_Bname);
        
        txt_Bname=new JTextField();
        txt_Bname.setBounds(100, 40, 170, 25);          
        txt_Bname.setFont(font);
        dlgBeachForm.add(txt_Bname);
        
        lbl_Description=new JLabel();
        lbl_Description.setBounds(10, 80, 100, 20);         
        lbl_Description.setText("Description:");  
        lbl_Description.setFont(font);
        //lbl_Fname.setFont(font);
        dlgBeachForm.add(lbl_Description);
        
        txt_Description=new JTextField();
        txt_Description.setBounds(100, 80, 170, 25);          
        txt_Description.setFont(font);
        dlgBeachForm.add(txt_Description);
        
        lbl_County=new JLabel();
        lbl_County.setBounds(10, 120, 100, 20);         
        lbl_County.setText("County:");  
        lbl_County.setFont(font);
        //lbl_Fname.setFont(font);
        dlgBeachForm.add(lbl_County);
        
        cboCounty=new JComboBox(MainMenu.counties.toArray());
        cboCounty.setBounds(100, 120, 170, 25);          
        cboCounty.setFont(font);
        dlgBeachForm.add(cboCounty);
        
        lbl_Title2=new JLabel();
        lbl_Title2.setBounds(10, 150, 300, 20);         
        lbl_Title2.setText("Avialable Beaches");
        lbl_Title2.setFont(font); 
        lbl_Title2.setVerticalAlignment(JLabel.BOTTOM);
        dlgBeachForm.add(lbl_Title2);
        
        list=new BeachListPanel();
        list.setBounds(10, 170, 270, 150);
        dlgBeachForm.add(list); 
        
        
        btnSave=new JButton("Add");
        btnSave.setBounds(320, 40, 100, 40);
        btnSave.setActionCommand(ACT_SAVE);
        btnSave.addActionListener(this);
       // btnSave.setEnabled(false);
        dlgBeachForm.add(btnSave);
        
        btnCancel=new JButton("Cancel");
        btnCancel.setBounds(320, 100, 100, 40);
        btnCancel.setActionCommand(ACT_CANCEL);
        btnCancel.addActionListener(this);
        dlgBeachForm.add(btnCancel);
        
        btnUpdate=new JButton("Update");
        btnUpdate.setBounds(320, 160, 100, 40);
        btnUpdate.setActionCommand(ACT_UPDATE);
        btnUpdate.addActionListener(this);
        dlgBeachForm.add(btnUpdate);
                
        btnDelete=new JButton("Delete");
        btnDelete.setBounds(320, 220, 100, 40);
        btnDelete.setActionCommand(ACT_DELETE);
        btnDelete.addActionListener(this);
        //btnDelete.setEnabled(false);
        dlgBeachForm.add(btnDelete);
        
        btnBack=new JButton("Back");
        btnBack.setBounds(320, 280, 100, 40);
        btnBack.setActionCommand(ACT_BACK);
        btnBack.addActionListener(this);
        dlgBeachForm.add(btnBack);
       
    }
    
    private void loadBeach()
    {
        locations =new ArrayList<String>();
        db=new Sql();
        try
        {
            beachList=db.getAllBeachData();            
            
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
    public void createAndShowGUI()
     {
         dlgBeachForm.setVisible(true);          
         dlgBeachForm.dispose(); //close the app once soen
     }
    
     public void actionPerformed(ActionEvent e){
        if(e.getActionCommand().equals(ACT_BACK))
        {
            dlgBeachForm.setVisible(false);
            return;
	}
        else if(e.getActionCommand().equals(ACT_CANCEL))
        {
            //enable this buttons
            txt_Bname.setText("");
            txt_Description.setText("");
            cboCounty.setSelectedIndex(0);
            txt_Bname.requestFocus();
            btnUpdate.setEnabled(true);
            btnDelete.setEnabled(true);
            btnSave.setText("Add");
            return;
	}
        if(e.getActionCommand().equals(ACT_UPDATE))
        {
            if(validFields()==true)
            {
                beach = new Beach(txt_Bname.getText(),txt_Description.getText(),(String)cboCounty.getSelectedItem());
                //compare fields to note changes for audit trail
                oldBeach.updateBeach();
            }
            return;
	}
        else if(e.getActionCommand().equals(ACT_SAVE))
        {
            //dlgBeachForm.setVisible(false);
            if (btnSave.getText().equalsIgnoreCase("Add"))
            {
                txt_Bname.setText("");
                txt_Description.setText("");
                txt_Bname.requestFocus();
                //txt_Description.requestFocus();
                btnSave.setText("Save");
                cboCounty.setSelectedIndex(0);
                btnUpdate.setEnabled(false);
                btnDelete.setEnabled(false);
                return;
            }
            else if (btnSave.getText().equalsIgnoreCase("Save"))
            {
                //validate entries
                if(validFields()==true)
                {
                    //enable this buttons
                    btnUpdate.setEnabled(true);
                    btnDelete.setEnabled(true);
                    btnSave.setText("Add");
                 
                    beach = new Beach(txt_Bname.getText(),txt_Description.getText(),(String)cboCounty.getSelectedItem());
                    beach.saveBeach();
                    beach.saveAuditTrail();
                    return;
                
                }
                
            }
           
	}
        else if(e.getActionCommand().equals(ACT_DELETE))
        {
            //validate entries
            if(validFields()==true)
            {
                if (oldBeach!=null)
                {
                    oldBeach.deleteBeach();
                }
            }
                
                return;
	}
     }
     
     public boolean validFields()
     {
         boolean b_ok=true;
         if(txt_Bname.getText()==null)
         {
            JOptionPane.showMessageDialog(null, "Enter The Beach Name");
            b_ok=false;
         }
         if(txt_Description.getText()==null)
         {
             JOptionPane.showMessageDialog(null, "Enter The Beach Description");
             b_ok=false;
         }
         if (cboCounty.getSelectedIndex()==0)    
         {
            JOptionPane.showMessageDialog(null, "Select the County Name");
            b_ok=false;
         }
         return b_ok;
     }
}
