/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ke.faces.fingerprint;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 *
 * @author LENOVO USER
 */
public class FindCreateDialog extends JPanel implements ActionListener{
 
    public static final String ACT_FIND="find_search";
    public static final String ACT_CREATE="creat_new";
    public static final String ACT_BACK="exit";
    
    
    private JDialog dlgFindCreate;
    public static final Font font = new Font("Times New Roman", Font.BOLD, 14);
     public static final Font font2 = new Font("Times New Roman", Font.PLAIN, 14);
    private JTextField txt_Name;
    private JTextField txt_Identifier;
    private JTextField txt_County;
    private JTextField txt_Beach;
    private JLabel lbl_Search;
    private JLabel lbl_Title;
    
    private JTextField txt_Search;
    
    private JButton btnBack;
    private JButton btnFind;
    private JButton btnCreate;
    private JTable jTable;
   
    
    String[] columnTitle=new String[]{"Identifier","Name","County","Beach"};
    private static DefaultTableModel model ;
    private List<Participant> findList;
    private Map beachMap;
    private Object[][] data={{"Identifier","Name","County","Beach"}};//since the column headers are not visible am using row 0 as my header for now
    
    FindCreateDialog()
    {
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
        
        model = new DefaultTableModel(data,columnTitle);
        jTable = new JTable(model);
        jTable.setBounds(10, 80, 500, 300); 
        jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTable.setRowHeight(25);
        jTable.setBorder(BorderFactory.createTitledBorder(""));
        
        JScrollPane scrollPane = new JScrollPane(jTable);
        jTable.setFillsViewportHeight(true);
        //set column heights
        TableColumn column = null;
        
        column = jTable.getColumnModel().getColumn(0);
	column.setPreferredWidth(45);
        column = jTable.getColumnModel().getColumn(1);
	column.setPreferredWidth(90);
        
        dlgFindCreate.add(jTable.getTableHeader());
        add(new JScrollPane(jTable));
        //JScrollPane scrollPane = new JScrollPane(jTable);
        //dlgFindCreate.add(scrollPane);
        dlgFindCreate.add(jTable);
        
       btnCreate=new JButton("Create Participant");
       btnCreate.setBounds(200, 400, 150, 35); 
       btnCreate.setActionCommand(ACT_FIND);
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
                      
              Registration registration=new Registration();
              registration.createAndShowGUI();
	}
        else  if(e.getActionCommand().equals(ACT_FIND))
        {
            String search;
            search=txt_Search.getText();
            if (search.length()>=3)
            {
                jTable.removeAll();
                    Sql db=new Sql();
                    try
                    {
                        db.Open();//open/create connection to the db
                      findList=  db.findParticipant(search);
                      beachMap=db.getBeachMap();
                        db.Close();
                    }
                    catch(SQLException ex)
                    {
                        ex.printStackTrace();
                    }  
                   // int i=1;
                    if(!findList.isEmpty())
                    {
                        for (Participant p:findList)
                        {
                           //Insert last position
                            Beach b=(Beach)beachMap.get(p.getBeachId());//get the Beach details given the ID
                            String pName;
                            //insertStr="\""+p.getIdentifier()+"\",\"";
                            pName=p.getFamilyName()+" "+p.getMiddleName()+" "+p.getGivenName()+" "+p.getNickName();
                            //insertStr=insertStr+b.getCounty()+"\",\""+b.getName()+"\"";
                            System.out.println("Testing Stuff"+pName);
                            model.insertRow(jTable.getRowCount(),new Object[]{p.getIdentifier(),pName.replaceAll("null", ""),b.getCounty(),b.getName()});
                        }
                    }

            }
            else
            {
                JOptionPane.showMessageDialog(null,"Enter atleast three characters then click the search/Find Button");
            }
            return;
	}
         
     }
        
}
