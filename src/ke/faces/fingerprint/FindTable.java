/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ke.faces.fingerprint;

import java.awt.Dimension;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

/**
 *
 * @author LENOVO USER
 */
public class FindTable extends JPanel{
    JTable jTable;
    String[] columnTitle=new String[]{"Identifier","Name","County","Beach"};
    private static DefaultTableModel model ;
    private Object[][] data;
    private List<Participant> findList;
    private Map beachMap;
    Sql db=new Sql();
    public static Participant selectedParticipant;
    public FindTable()
    {
        model = new DefaultTableModel(data,columnTitle);
        jTable = new JTable(model);
        jTable.setBounds(10, 80, 500, 300); 
        jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTable.setRowHeight(25);
        jTable.setBorder(BorderFactory.createTitledBorder(""));
        
        JScrollPane scrollPane = new JScrollPane(jTable);
        jTable.setFillsViewportHeight(true);
        JTableHeader header = jTable.getTableHeader();
        getSelectedRow();
        setPreferredSize(new Dimension(400, 200));       
        jTable.setPreferredScrollableViewportSize(new Dimension(500, 250));
        
        //set coulmun size
        TableColumn column = null;
        column = jTable.getColumnModel().getColumn(0); //Identifier column
        column.setPreferredWidth(45);
        
        column = jTable.getColumnModel().getColumn(1); //Name column
        column.setPreferredWidth(150);
        
         column = jTable.getColumnModel().getColumn(2); //County column
        column.setPreferredWidth(40);
       
        add(scrollPane);
        add(header);
        validate();                
    }
    public void insertRow(String search)
    {
        //remove all rows      
        model.getDataVector().removeAllElements();
        jTable.repaint();   
       // model.getDataVector().
         
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
    
    public int getRowCount()
    {
        return jTable.getRowCount();
    }
    
    public Participant getSelectedParticipant()
    {
        return selectedParticipant;
    }
    
    public void getSelectedRow()
    {
     //  JOptionPane.showMessageDialog(null, "Selected Row is: "+jTable.getSelectedRow());
       jTable.getSelectionModel().addListSelectionListener(
               new ListSelectionListener(){
                   public void valueChanged(ListSelectionEvent e)
                   {
                       int selectedRow=jTable.getSelectedRow();
                       if (selectedRow>=0)
                       {
                            Object data = model.getValueAt(selectedRow, 0);
                            int modelRow = jTable.convertRowIndexToModel(selectedRow);
                            System.out.println(String.format("Selected Row in view: %d. " + "Selected Row in model: %d.", selectedRow, modelRow));
                            System.out.println(String.format("Selected Row in view:  " + data.toString()));
                            try
                            {
                                db.Open();
                                selectedParticipant=db.getParticipant(data.toString());                                
                            }
                            catch(SQLException ex)
                            {
                                ex.printStackTrace();
                            } 
                            
                       }
                   }
                   
               });
    }
        
}
