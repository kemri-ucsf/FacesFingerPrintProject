/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ke.faces.fingerprint;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author LENOVO USER
 */
public class Beach {
    
    private int beachId;
    private String name;
    private String description;
    private String county;

    private static final Logger LOG = Logger.getLogger(Beach.class.getName());
    
     public Beach()
     {
         this.name=null;
         this.description=null;
         this.county=null;
     }
    public Beach(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Beach(String name, String description, String county) {
        this.name = name;
        this.description = description;
        this.county = county;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }
    
    
    
    public int getBeachId() {
        return beachId;
    }

    public void setBeachId(int beachId) {
        this.beachId = beachId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Beach{" + "name=" + name + ", description=" + description + '}';
    }
   
    public void saveBeach()
    {
        
        //validate fields
        
        if (this.getName()==null)
        {
            JOptionPane.showMessageDialog(null, "Beach Name cannot be blank");
            return;
        }
         if (this.getDescription()==null)
        {
            JOptionPane.showMessageDialog(null, "Description cannot be blank");
            return;
        }
               
        if (this.getCounty()==null)
        {
            JOptionPane.showMessageDialog(null, "Select the County");
            return;
        }
           //Save participant
           Sql db=new Sql();
           try
           {
               db.Open();//open/create connection to the db
               db.insertBeach(this);
               db.Close();
           }
           catch(SQLException e)
           {
               e.printStackTrace();
           }           
        
    }
    public void deleteAuditTrail()
    {
        Sql db=new Sql();
           try
           {
               db.Open();//open/create connection to the db               
               db.voidAuditTrail(this.getClass().getName(), MainMenu.gUser.getUserId(),this.getBeachId());//the valu 5 shld be replaced with the currently logged on user
               db.Close();
                              
           }
           catch(SQLException e)
           {
               e.printStackTrace();
           }
    }
    public void deleteBeach()
    {
        //Void Beach
        if (this.getName()==null)
        {
            JOptionPane.showMessageDialog(null, "Beach Name cannot be blank");
            return;
        }
        if (this.getDescription()==null)
        {
            JOptionPane.showMessageDialog(null, "Description cannot be blank");
            return;
        }
               
        if (this.getCounty()==null)
        {
            JOptionPane.showMessageDialog(null, "Select the County");
            return;
        }
        
        Sql db=new Sql();
           try
           {
               db.Open();//open/create connection to the db
               db.voidBeach(this);
               db.Close();
           }
           catch(SQLException e)
           {
               e.printStackTrace();
           }
        }
    
    public void saveAuditTrail()
    {
       //create a hash map of the fields to be inserted
        Map<String,String> fieldList =new HashMap<String,String>();
        Class classP=this.getClass(); //create an object of type class to get the class details
        Field[] fields=classP.getDeclaredFields();//get declared fields in a class
     
        for(int i=0;i<fields.length;i++)
        {
            try
             {
                 Field   field= classP.getDeclaredField(fields[i].getName()); //get field names
                 //System.out.println(field)
                 fieldList.put(fields[i].getName(), (String)field.get(this));                 
                 System.out.println("Field Name: "+ fields[i].getName()+" Value: "+(String)field.get(this));                   
                          
                 
             }
             catch (NoSuchFieldException e) 
             {
                System.out.println(e);
             } 
             catch (SecurityException e)
             {
                System.out.println(e);
             } 
             catch (IllegalAccessException e)
             {
                System.out.println(e);
             }
        }
           //Save Audit trail
           Sql db=new Sql();
           try
           {
               db.Open();//open/create connection to the db
               db.insertTrail(fieldList, this.getClass().getName(), 5);//the valu 5 shld be replaced with the currently logged on user
               db.Close();
           }
           catch(SQLException e)
           {
               e.printStackTrace();
           }           
        
    }
    
    public void updateBeach()
    {
        //Void Beach
        if (this.getName()==null)
        {
            JOptionPane.showMessageDialog(null, "Beach Name cannot be blank");
            return;
        }
        if (this.getDescription()==null)
        {
            JOptionPane.showMessageDialog(null, "Description cannot be blank");
            return;
        }
               
        if (this.getCounty()==null)
        {
            JOptionPane.showMessageDialog(null, "Select the County");
            return;
        }
        
        Sql db=new Sql();
           try
           {
               db.Open();//open/create connection to the db
               db.updateBeach(this);
               db.Close();
           }
           catch(SQLException e)
           {
               e.printStackTrace();
           }
        }
    
}
