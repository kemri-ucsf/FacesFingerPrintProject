/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ke.faces.fingerprint;

import com.digitalpersona.uareu.Fmd;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
    private int age;
    private String beachName;
    private char gender;
    private int participant_Id;
    private FingerPrint fingerPrint;
    private FingerPrint fingerPrint2;
    public static List<FingerPrint> lstFingerPrints;    
   
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

    
    public Participant(String identifier, String familyName, String middleName, String givenName, int age, String beachName, char gender) {
        this.identifier = identifier;
        this.familyName = familyName;
        this.middleName = middleName;
        this.givenName = givenName;
        this.age = age;
        this.beachName = beachName;
        this.gender = gender;
        //fingerPrint=new FingerPrint[10];
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

    public String getBeachName() {
        return beachName;
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

    public void setBeachName(String beachName) {
        this.beachName = beachName;
    }

    public void setGender(char gender) {
        this.gender = gender;
    }

    public void setParticipant_Id(int participant_Id) {
        this.participant_Id = participant_Id;
    }   


    @Override
    public String toString() {
        return "Participant{" + "identifier=" + identifier + ", familyName=" + familyName + ", middleName=" + middleName + ", givenName=" + givenName + ", age=" + age + ", beachName=" + beachName + ", gender=" + gender + ", participant_Id=" + participant_Id + '}';
    }
    private static final Logger LOG = Logger.getLogger(Participant.class.getName());

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
        if (this.age != other.age) {
            return false;
        }
        if (!Objects.equals(this.beachName, other.beachName)) {
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
        
           if (this.getBeachName()==null)
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
           }
           catch(SQLException e)
           {
               e.printStackTrace();
           }
        }
    
    
}
