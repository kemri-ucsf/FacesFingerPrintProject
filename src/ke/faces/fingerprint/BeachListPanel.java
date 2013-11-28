/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ke.faces.fingerprint;

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author LENOVO USER
 */
public class BeachListPanel extends JPanel{
    private JList lstLocations;  
    BeachListPanel()
    {
        
        setLayout(new BorderLayout());
        lstLocations=new JList(MainMenu.locations.toArray());                  
        lstLocations.setBorder(BorderFactory.createTitledBorder(""));
        lstLocations.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        
        //scrollPane.setPreferredSize(new Dimension(250, 80));
        
        lstLocations.addListSelectionListener(new ListSelectionListener() {
        public void valueChanged(ListSelectionEvent e)
        {
         // int indices = lstLocations.getSelectedIndex();
          Object  selectedItem = lstLocations.getSelectedValue();
          String display = (String)selectedItem;
          //get the selected item and split it inot two i.e. beach and county
          //The split methos splits a string into an array given the regex (i.e. what to look at so as to split)
          
          if (!display.isEmpty())
          {
             String[] output=display.split("-", 2);
          
                for(Beach b: MainMenu.beachList)
                {
                    if(output[0].equalsIgnoreCase(b.getName())&& output[1].equalsIgnoreCase(b.getCounty()))
                    {
                        BeachForm.oldBeach.setBeachId(b.getBeachId());
                        BeachForm.oldBeach.setCounty(b.getCounty());
                        BeachForm.oldBeach.setName(b.getName());
                        BeachForm.oldBeach.setDescription(b.getDescription());
                        
                        BeachForm.txt_Description.setText(b.getDescription());
                        BeachForm.txt_Bname.setText(b.getName());
                        //List lstCounty=Arrays.asList(county); //good Idea but failed to work for me
                       // for(int i=0;i<county.length;i++)
                      //  {
                      //      if (county[i].equalsIgnoreCase(b.getCounty()))
                      //      {
                      //          cboCounty.setSelectedIndex(i);
                     //       }
                     //   }
                        BeachForm.cboCounty.setSelectedItem((Object)b.getCounty());
                                             
                    }
                } 
          }
          
             
        }
      });
        
        JScrollPane scrollPane = new JScrollPane(lstLocations);
        //add(lstLocations);
        add(scrollPane);
        
        validate();
    }
    
}
