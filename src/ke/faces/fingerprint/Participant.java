/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ke.faces.fingerprint;

import com.digitalpersona.uareu.Fmd;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author LENOVO USER
 */
public class Participant {
    private String identifier;
    private String familyName;
    private String middleName;
    private String givenName;
    private String nickName;
    private int age;
    private int beachId;
    private char gender;
    private int participant_Id;
    private FingerPrint fingerPrint;
    private FingerPrint fingerPrint2;
    public static List<FingerPrint> lstFingerPrints;    
    private static final Logger LOG = Logger.getLogger(Participant.class.getName());
    public Participant() {
        //fingerPrint=new FingerPrint[10];
        lstFingerPrints=new ArrayList<FingerPrint>();
    }

    public List<FingerPrint> getLstFingerPrints() {
        return lstFingerPrints;
    }

    public void setLstFingerPrints(List<FingerPrint> lstFingerPrints) {
        this.lstFingerPrints = lstFingerPrints;
    }

    
    public Participant(String identifier, String familyName, String middleName, String givenName,String nickName, int age, int beachId, char gender) {
        this.identifier = identifier;
        this.familyName = familyName;
        this.middleName = middleName;
        this.givenName = givenName;
        this.nickName = nickName;
        this.age = age;
        this.beachId = beachId;
        this.gender = gender;
        //fingerPrint=new FingerPrint[10];
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public FingerPrint getFingerPrint2() {
        return fingerPrint2;
    }

    public void setFingerPrint2(FingerPrint fingerPrint2) {
        this.fingerPrint2 = fingerPrint2;
    }

    
    public FingerPrint getFingerPrint() {
        return fingerPrint;
    }

    public void setFingerPrint(FingerPrint fingerPrint) {
        this.fingerPrint = fingerPrint;
    }

    
    public int getParticipant_Id() {
        return participant_Id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getFamilyName() {
        return familyName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getGivenName() {
        return givenName;
    }

    public int getAge() {
        return age;
    }

    public int getBeachId() {
        return beachId;
    }

    public char getGender() {
        return gender;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setBeachId(int beachId) {
        this.beachId = beachId;
    }

    public void setGender(char gender) {
        this.gender = gender;
    }

    public void setParticipant_Id(int participant_Id) {
        this.participant_Id = participant_Id;
    }   


    @Override
    public String toString() {
        return "Participant{" + "identifier=" + identifier + ", familyName=" + familyName + ", middleName=" + middleName + ", givenName=" + givenName + ", age=" + age + ", beachName=" + beachId + ", gender=" + gender + ", participant_Id=" + participant_Id + '}';
    }
    

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Participant other = (Participant) obj;
        if (!Objects.equals(this.identifier, other.identifier)) {
            return false;
        }
        if (!Objects.equals(this.familyName, other.familyName)) {
            return false;
        }
        if (!Objects.equals(this.middleName, other.middleName)) {
            return false;
        }
        if (!Objects.equals(this.givenName, other.givenName)) {
            return false;
        }
        if (!Objects.equals(this.nickName, other.nickName)) {
            return false;
        }
        if (this.age != other.age) {
            return false;
        }
        if (!Objects.equals(this.beachId, other.beachId)) {
            return false;
        }
        if (this.gender != other.gender) {
            return false;
        }
        return true;
    }
    
    
    public void saveParticipant()
    {
        
        //validate fields
        
        if (this.getGivenName()==null)
        {
            JOptionPane.showMessageDialog(null, "Given/Christian Name cannot be blank");
            return;
        }
         if (this.getFamilyName()==null)
        {
            JOptionPane.showMessageDialog(null, "Family/Last Name cannot be blank");
            return;
        }
          if ((Integer)this.getAge()==null)
        {
            JOptionPane.showMessageDialog(null, "Given/Christian Name cannot be blank");
            return;
        }
        
           if ((Integer)this.getBeachId()==null)
        {
            JOptionPane.showMessageDialog(null, "Beach Name cannot be blank");
            return;
        }
           //Save participant
           Sql db=new Sql();
           try
           {
               db.Open();//open/create connection to the db
               db.insertParticipant(this);
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
                 if(!field.toString().equalsIgnoreCase("lstFingerPrints")||!field.toString().contains("finger")||!field.toString().equalsIgnoreCase("LOG"))
                 {    
                     Class typeClass = fields[i].getType();
                     String fieldType = typeClass.getName();
                     if(fieldType=="int" && field.get(this)!=null)
                     {
                         fieldList.put(fields[i].getName(), Integer.toString((Integer)field.get(this)));                 
                         System.out.println("Field Name: "+ fields[i].getName()+" Value: "+Integer.toString((Integer)field.get(this)));
                     }
                     else if(fieldType=="char" && field.get(this)!=null)
                     {
                         fieldList.put(fields[i].getName(), Character.toString((Character)field.get(this)));                 
                         System.out.println("Field Name: "+ fields[i].getName()+" Value: "+Integer.toString((Character)field.get(this)));
                     }
                     else if (field.get(this)!=null)
                     {
                         fieldList.put(fields[i].getName(), field.get(this).toString());                 
                         System.out.println("Field Name: "+ fields[i].getName()+" Value: "+field.get(this).toString());
                     }
                    // fieldList.put(fields[i].getName(), field.get(this).toString());                 
                    // System.out.println("Field Name: "+ fields[i].getName()+" Value: "+(String)field.get(this));                   
                 }         
                 
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
    
    public void updateAuditTrail(Participant old)
    {
       //create a hash map of the fields to be inserted
        Map<String,String> oldFieldList =new HashMap<String,String>();
        Map<String,String> newFieldList =new HashMap<String,String>();
        
        Class classP=this.getClass(); //create an object of type class to get the class details
        Class classOld=old.getClass(); //create an object of type class to get the class details
        Field[] fields=classP.getDeclaredFields();//get declared fields in a class
        Field[] oldFields=classOld.getDeclaredFields();//get declared fields in a class
     
        for(int i=0;i<fields.length;i++)
        {
            try
             {
                 Field   field= classP.getDeclaredField(fields[i].getName()); //get field names
                 Field   oldField= classOld.getDeclaredField(oldFields[i].getName()); //get field names
                 
                 System.out.println("Comparing:"+oldFields[i].getName()+" And "+ fields[i].getName());
                 //get only updated fields
                 String oldVal;
                 String newVal;
                 if(field.get(this)!=oldField.get(old))
                 {
                     if(!field.toString().equalsIgnoreCase("lstFingerPrints")||!field.toString().contains("finger")||!field.toString().equalsIgnoreCase("LOG"))
                     {    
                        Class typeClass = fields[i].getType();
                        String fieldType = typeClass.getName();
                        if(fieldType=="int" && field.get(this)!=null)
                        {
                            oldVal=Integer.toString((Integer)oldField.get(old));
                            newVal=Integer.toString((Integer)field.get(this));
                            if(!oldVal.equalsIgnoreCase(newVal))
                            {
                                oldFieldList.put(oldFields[i].getName(), oldVal);
                                newFieldList.put(fields[i].getName(), newVal);
                                System.out.println("Field Name: "+ fields[i].getName()+" Old Value: "+oldVal+" New Value: "+newVal);                        
                            }
                        }
                        else if(fieldType=="char" && field.get(this)!=null)
                        {
                            oldVal=Character.toString((Character)oldField.get(old));
                            newVal= Character.toString((Character)field.get(this));
                            if(!oldVal.equalsIgnoreCase(newVal))
                            {
                                oldFieldList.put(oldFields[i].getName(), oldVal);
                                newFieldList.put(fields[i].getName(), newVal);
                                System.out.println("Field Name: "+ fields[i].getName()+" Old Value: "+oldVal+" New Value: "+newVal);                        
                            }
                        }
                        else if (field.get(this)!=null)
                        {                         
                           // oldFieldList.put(oldFields[i].getName(), oldField.get(this).toString());
                           // newFieldList.put(fields[i].getName(), field.get(this).toString());
                           // System.out.println("Field Name: "+ fields[i].getName()+" Old Value: "+oldField.get(this).toString()+" New Value: "+field.get(this).toString());
                            oldVal=oldField.get(old).toString();
                            newVal= field.get(this).toString();
                            if(!oldVal.equalsIgnoreCase(newVal))
                            {
                                oldFieldList.put(oldFields[i].getName(), oldVal);
                                newFieldList.put(fields[i].getName(), newVal);
                                System.out.println("Field Name: "+ fields[i].getName()+" Old Value: "+oldVal+" New Value: "+newVal);                        
                            }
                        }
                    // fieldList.put(fields[i].getName(), field.get(this).toString());                 
                    // System.out.println("Field Name: "+ fields[i].getName()+" Value: "+(String)field.get(this));                   
                       }
                     
                 }
                          
                 
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
               if(oldFieldList.size()>0)
               {
                   db.updateTrail(oldFieldList, newFieldList,this.getClass().getName(), 5,old.getParticipant_Id());//the valu 5 shld be replaced with the currently logged on user
                   db.Close();
               }
               
           }
           catch(SQLException e)
           {
               e.printStackTrace();
           }           
        
    }
     public void updateParticipant()
    {
        
        //validate fields
        
        if (this.getGivenName()==null)
        {
            JOptionPane.showMessageDialog(null, "Given/Christian Name cannot be blank");
            return;
        }
         if (this.getFamilyName()==null)
        {
            JOptionPane.showMessageDialog(null, "Family/Last Name cannot be blank");
            return;
        }
          if ((Integer)this.getAge()==null)
        {
            JOptionPane.showMessageDialog(null, "Given/Christian Name cannot be blank");
            return;
        }
        
           if ((Integer)this.getBeachId()==null)
        {
            JOptionPane.showMessageDialog(null, "Beach Name cannot be blank");
            return;
        }
           //Save participant
           Sql db=new Sql();
           try
           {
               db.Open();//open/create connection to the db
               db.updateParticipant(this);
               db.Close();
           }
           catch(SQLException e)
           {
               e.printStackTrace();
           }           
        
    }
    public void deleteParticipant()
    {
        //Save participant
        Sql db=new Sql();
        try
        {
            db.Open();//open/create connection to the db
            db.voidParticipant(this);
            db.Close();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }
    
    public int getDublicateIdentifier(String identifier)
    {
        //Save participant
        Sql db=new Sql();
        try
        {
            db.Open();//open/create connection to the db
            return db.validateIdentifier(identifier);
           // db.Close();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        return 0;
    }
    
    
}
