/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ke.faces.fingerprint;

/**
 *
 * @author LENOVO USER
 */
import java.lang.reflect.Field;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
/**
 *
 * @author Eric
 */
public class Sql {
    private static final String tableName="users";
    private static final String participantColumn="PTID";
    private static final String print1Column="print1";
    private static final String print2Column="print2";
    private static final String genderColumn="gender";
    private static final String ageColumn="age";
    private static final String familyNameColumn="lName";
    
    //Developement Credentials
    
  private static final String URL="jdbc:mysql://localhost:3308/fingerPrintTest?autoReconnect=true";
  private static final String PASSWORD="openmrs";
 private static final String USERNAME="openmrs";
    
    
    /*
     //* Production Credentials
     */ 
    //private static final String URL="jdbc:mysql://localhost:3306/fingerPrinttest?autoReconnect=true";
  //  private static final String PASSWORD="test12";
  //  private static final String USERNAME="root";
    
    // 1) create a java calendar instance        
    private static final Calendar calendar = Calendar.getInstance();
    // 2) get a java.util.Date from the calendar instance.
    // this date will represent the current instant, or "now".
    private static final java.util.Date date = calendar.getTime();
    // 3) a java current time (now) instance
    private static final Timestamp timestamp = new Timestamp(date.getTime());
    private static Connection c=null;
    private String preppedStmtInsert=null;
    private String preppedStmtUpdate=null;
    
    public class Record 
    {
	int PTID;
	byte[] fmdBinary;
		
	Record(int ID,byte[] fmd)
	{
            PTID=ID;
	   fmdBinary=fmd;
	}

        public int getPTID() {
            return PTID;
        }

        public byte[] getFmdBinary() {
            return fmdBinary;
        }
        
    }
    public static void Open() 
    {
	try
        {
            c=DriverManager.getConnection(URL, USERNAME, PASSWORD);
            FacesFingerPrintProject.logger.info("Opening Connection to the Database");
        }
        catch(SQLException ex)
        {
            //Logger.getLogger(Sql.class.getName()).log(Level.SEVERE, null, ex);
            //Loga Error messages to the log file
            FacesFingerPrintProject.logger.log(Level.SEVERE, "ERROR", ex);
        }
        
    }
    public  Statement createStatement()  {
        //c = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        
        Statement statement=null;
        try{
            FacesFingerPrintProject.logger.info("Creating Statement");
            Open();            
             statement = c.createStatement();
        }
        catch(SQLException ex)
        {
            //Logger.getLogger(Sql.class.getName()).log(Level.SEVERE, null, ex);
            FacesFingerPrintProject.logger.log(Level.SEVERE, "ERROR", ex);
        }
        return statement;
    }
    public  ResultSet executeQuery(String query) throws SQLException {
        Statement statement = createStatement();
        ResultSet rs=statement.executeQuery(query);
        //statement.
        //rs.next();
        return rs;
    }
    
    public  int executeUpdate(String query) throws SQLException {
        Statement statement = createStatement();
        return statement.executeUpdate(query);
    }
    
    public void Close()
    {
	try 
        {
            c.close();
	}
        catch (SQLException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            //Logger.getLogger(Sql.class.getName()).log(Level.SEVERE, null, e);
            FacesFingerPrintProject.logger.log(Level.SEVERE, "ERROR", e);
        }	
    }
    
    public void finalize()
    {
        try 
        {
            c.close();
	}
        catch (SQLException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            //Logger.getLogger(Sql.class.getName()).log(Level.SEVERE, null, e);
            FacesFingerPrintProject.logger.log(Level.SEVERE, "ERROR", e);
        }
        
    }
    
    public void insertParticipant(Participant p) 
    {
            
            preppedStmtInsert="INSERT INTO participant (identifier,fname,mname,gname,nname,age,gender,beachid,dateCreated,creator) VALUES(?,?,?,?,?,?,?,?,?,?)";
            //System.out.println("Check if db print has data: "+ p.getlMiddleFmd());
            try
            {
                PreparedStatement pst= c.prepareStatement(preppedStmtInsert);
		pst.setString(1, p.getIdentifier());
                pst.setString(2, p.getFamilyName());
                pst.setString(3, p.getMiddleName());
                pst.setString(4, p.getGivenName());
                pst.setString(5, p.getNickName());
                pst.setInt(6, p.getAge());
                pst.setString(7, Character.toString(p.getGender()));//convert xter to string
                pst.setInt(8, p.getBeachId());
		//pst.setBytes(8, p.getlMiddleFmd());
                pst.setTimestamp(9, timestamp);
                pst.setInt(10, MainMenu.gUser.getUserId());
		pst.execute();
                Close();//ensure all records are persisted on the db before saving prints
                Open();
                
                //save all the taken finger prints for this participant
                int ptid=getLastParticipantId();
                if (ptid!=0)
                {
                    insertFingerPrint(p,ptid);
                }
                JOptionPane.showMessageDialog(null, "Participant Record Successfully Saved... ");
                
                //insert audit trail;
                p.setParticipant_Id(ptid);
               // insertParticipantTrail(p);
            }
             catch (SQLException e) 
             {
		// TODO Auto-generated catch block
		e.printStackTrace();
                //Logger.getLogger(Sql.class.getName()).log(Level.SEVERE, null, e);
                FacesFingerPrintProject.logger.log(Level.SEVERE, "ERROR", e);
            }
		
    }
    public void insertTrail(Map<String,String> fieldList,String className,int userId)
    {
        try
        {
            int i=getLastParticipantId();
            for(String s:fieldList.keySet())
            {
                preppedStmtInsert="INSERT INTO audittrail (datetime,userid,formname,recordid,fieldname,newvalue) VALUES(?,?,?,?,?,?)";
                PreparedStatement pst= c.prepareStatement(preppedStmtInsert);
                pst.setTimestamp(1, timestamp);
                pst.setInt(2, userId);
                pst.setString(3, className);
                pst.setInt(4, i);
                pst.setString(5, s);
                pst.setString(6, fieldList.get(s));
                pst.execute();
                System.out.println("Field Name: "+ s+" Value: "+fieldList.get(s));
            
            }
        }
        catch (SQLException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
           // Logger.getLogger(Sql.class.getName()).log(Level.SEVERE, null, e);
            FacesFingerPrintProject.logger.log(Level.SEVERE, "ERROR", e);
        }
        
    }
    
    public void updateTrail(Map<String,String> oldFieldList,Map<String,String> newFieldList,String className,int userId,int rcd_id) 
    {
        //int i=getLastParticipantId();
        try
        {
            for(String s:oldFieldList.keySet())
            {
                preppedStmtInsert="INSERT INTO audittrail (datetime,userid,formname,recordid,fieldname,oldvalue,newvalue) VALUES(?,?,?,?,?,?,?)";
                PreparedStatement pst= c.prepareStatement(preppedStmtInsert);
                pst.setTimestamp(1, timestamp);
                pst.setInt(2, userId);
                pst.setString(3, className);
                pst.setInt(4, rcd_id);
                pst.setString(5, s);
                pst.setString(6, oldFieldList.get(s));
                pst.setString(7, newFieldList.get(s));
                pst.execute();
                System.out.println("Field Name: "+ s+" oldValue: "+oldFieldList.get(s)+" oldValue: "+newFieldList.get(s));
            
            }
        }
        catch (SQLException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
           // Logger.getLogger(Sql.class.getName()).log(Level.SEVERE, null, e);
            FacesFingerPrintProject.logger.log(Level.SEVERE, "ERROR", e);
        }
        
    }
    
    public void voidAuditTrail(String className,int userId,int rcd_id) 
    {
        //int i=getLastParticipantId(); 
        try
        {
            preppedStmtInsert="INSERT INTO audittrail (datetime,userid,formname,recordid,fieldname,newvalue) VALUES(?,?,?,?,?,?)";
            PreparedStatement pst= c.prepareStatement(preppedStmtInsert);
            pst.setTimestamp(1, timestamp);
            pst.setInt(2, userId);
            pst.setString(3, className);
            pst.setInt(4, rcd_id);
            pst.setString(5, "Record");
            //pst.setString(6, oldFieldList.get(s));
            pst.setString(6, "Deactivated");
            pst.execute();
            //System.out.println("Field Name: "+ s+" oldValue: "+oldFieldList.get(s)+" oldValue: "+newFieldList.get(s));            
        }
        catch (SQLException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            //Logger.getLogger(Sql.class.getName()).log(Level.SEVERE, null, e);
            FacesFingerPrintProject.logger.log(Level.SEVERE, "ERROR", e);
        }            
        
    }
   
    public void insertFingerPrint(Participant p,int ptid) 
    {       
            
        try
        {
            if(!p.getLstFingerPrints().isEmpty())
            {
                for(FingerPrint fp: p.getLstFingerPrints())
                {
                    preppedStmtInsert="INSERT INTO fingerprint (PTID,print1,printIndex,dateCreated,creator) VALUES(?,?,?,?,?)";
                    //System.out.println("Check if db print has data: "+ p.getlMiddleFmd());
                
                    PreparedStatement pst= c.prepareStatement(preppedStmtInsert);
                    pst.setInt(1, ptid);
                    pst.setBytes(2, fp.getFmd());
                    pst.setInt(3, fp.getIndex());
                    pst.setTimestamp(4, timestamp);
                    pst.setInt(5, MainMenu.gUser.getUserId());
                    pst.execute();
                }
            
            }       
        
        }
        catch (SQLException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Logger.getLogger(Sql.class.getName()).log(Level.SEVERE, "ERROR", e);
        }        
		
    }
    public void InsertUser(Participant p)
    {
            
        try
        {
            preppedStmtInsert="INSERT INTO " + tableName + "(" + familyNameColumn + "," + print1Column + ") VALUES(?,?)";
            //System.out.println("Check if db print has data: "+ p.getlMiddleFmd());
            PreparedStatement pst= c.prepareStatement(preppedStmtInsert);
            pst.setString(1, p.getFamilyName());
            //pst.setBytes(2, p.getlMiddleFmd());
            pst.execute();
        }
        catch (SQLException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
           // Logger.getLogger(Sql.class.getName()).log(Level.SEVERE, null, e);
            FacesFingerPrintProject.logger.log(Level.SEVERE, "ERROR", e);
        }
        
    }
    
    public List<Sql.Record> GetAllFPData()
    {
	List<Sql.Record> listParticipants=new ArrayList<Sql.Record>();
        try
        {
            String sqlStmt="Select * from fingerprint";
            //String sqlStmt="Select * from users"; //for testing first
            ResultSet rs = executeQuery(sqlStmt);
            while(rs.next())
            {
                if(rs.getBytes(print1Column)!=null)
                {
                    System.out.println("DB Read sucess: Loading Finger Prints ");
                    listParticipants.add(new Sql.Record(rs.getInt("PTID"),rs.getBytes(print1Column)));
                }
                    
            }	
        }
        catch (SQLException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
           // Logger.getLogger(Sql.class.getName()).log(Level.SEVERE, null, e);
            FacesFingerPrintProject.logger.log(Level.SEVERE, "ERROR", e);
        }
		
	return listParticipants;
    }
    
    public List<Beach> getAllBeachData() 
    {
        List<Beach> locationList=new ArrayList<Beach>();
        try
        {
            //String sqlStmt="Select * from fingerprint";
            String sqlStmt="Select beachid,name,county,description from beach where voided=0"; //for testing first
            Open();
            Statement statement = c.createStatement();
            ResultSet rs = statement.executeQuery(sqlStmt);
            while(rs.next())
            {
                System.out.println("DB Read sucess: Loading Beach names");
                Beach b =new Beach();
                b.setBeachId(rs.getInt("beachid"));
                b.setName(rs.getString("name"));
                b.setDescription(rs.getString("description"));
                b.setCounty(rs.getString("county"));
                locationList.add(b);                  
            }
        }
        catch (SQLException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
           // Logger.getLogger(Sql.class.getName()).log(Level.SEVERE, null, e);
            FacesFingerPrintProject.logger.log(Level.SEVERE, "ERROR", e);
        }
        	
        
        return locationList;
    }
    
    public Map<Integer, Beach> getBeachMap() 
    {
        Map<Integer, Beach> locationList=new HashMap<Integer,Beach>();
        try
        {
            //String sqlStmt="Select * from fingerprint";
            String sqlStmt="Select beachid,name,county from beach where voided=0"; //for testing first
            Open();
            Statement st = c.createStatement();
            ResultSet rs = st.executeQuery(sqlStmt);
            while(rs.next())
            {
                    System.out.println("DB Read sucess: Loading Beach names Map");
                    Beach b =new Beach();
                    b.setBeachId(rs.getInt("beachid"));
                    b.setName(rs.getString("name"));
                    b.setCounty(rs.getString("county"));
                    locationList.put(b.getBeachId(),b);                  
            }
        }
        catch (SQLException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            //Logger.getLogger(Sql.class.getName()).log(Level.SEVERE, null, e);
            FacesFingerPrintProject.logger.log(Level.SEVERE, "ERROR", e);
        }
        	
        
        return locationList;
    }
    
    public List<String> getCounty() 
    {
        List<String> locationList=new ArrayList<String>();
        
        try
        {
            //String sqlStmt="Select * from fingerprint";
		String sqlStmt="Select distinct county from beach where voided=0"; //for testing first
                Open();
                Statement st = c.createStatement();
		ResultSet rs = st.executeQuery(sqlStmt);
		while(rs.next())
		{
                    System.out.println("DB Read sucess: Loading County names");
                    locationList.add(rs.getString("county"));                  
		}
        }
         catch (SQLException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            //Logger.getLogger(Sql.class.getName()).log(Level.SEVERE, null, e);
            FacesFingerPrintProject.logger.log(Level.SEVERE, "ERROR", e);
        }
        	
        
        return locationList;
    }
    
    public void insertBeach(Beach beach)
    {
        try
        {
            preppedStmtInsert="INSERT INTO beach (name,description,county,dateCreated,creator) VALUES(?,?,?,?,?)";
            //System.out.println("Check if db print has data: "+ p.getlMiddleFmd());
            PreparedStatement pst= c.prepareStatement(preppedStmtInsert);
            pst.setString(1,beach.getName());
            pst.setString(2,beach.getDescription());
            pst.setString(3,beach.getCounty());
            pst.setTimestamp(4, timestamp);
            pst.setInt(5, MainMenu.gUser.getUserId());
            pst.execute();
        }
         catch (SQLException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
           // Logger.getLogger(Sql.class.getName()).log(Level.SEVERE, null, e);
            FacesFingerPrintProject.logger.log(Level.SEVERE, "ERROR", e);
        }
       
    }
    
    public Beach getBeach(int id) 
    {
        Beach b=null;//new Beach();
        try
        {
            String sqlStmt="Select name,description,county from beach WHERE beachid=" + id + "";
            ResultSet rs=executeQuery(sqlStmt);
            while (rs.next())
            {
                b=new Beach();
                b.setName(rs.getString("name"));  
                b.setDescription(rs.getString("description"));
                b.setCounty(rs.getString("county"));              
                b.setBeachId(rs.getInt("beachid"));
            }
        }
        catch (SQLException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
           // Logger.getLogger(Sql.class.getName()).log(Level.SEVERE, null, e);
            FacesFingerPrintProject.logger.log(Level.SEVERE, "ERROR", e);
        }   
        
        return b;
    }
    public void voidBeach(Beach b)
     {
         try
         {
             String sqlStmt="update beach set voided=1,dateVoided=now(), voidedBy="+MainMenu.gUser.getUserId()+" WHERE beachid=" + b.getBeachId() + "";
            int rs=executeUpdate(sqlStmt);
            if (rs>0)
            {
                 JOptionPane.showMessageDialog(null, "Participant Record delete...");
            }  
         }
         catch (SQLException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
           // Logger.getLogger(Sql.class.getName()).log(Level.SEVERE, null, e);
            FacesFingerPrintProject.logger.log(Level.SEVERE, "ERROR", e);
        }         
         
         
     }
    
    public void updateBeach(Beach b) 
    {
                    
         try
         {
             preppedStmtUpdate="update beach set name=?, description=?, county=?, dateChanged=?, changedBy=? WHERE beachid=?";
             PreparedStatement pst= c.prepareStatement(preppedStmtUpdate);
                pst.setString(1,b.getName());
                pst.setString(2,b.getDescription());
                pst.setString(3,b.getCounty());	
                pst.setTimestamp(4, timestamp);
                pst.setInt(5, MainMenu.gUser.getUserId());
                pst.setInt(6, b.getBeachId());
		pst.executeUpdate();
                
                System.out.println(preppedStmtUpdate);
         }
         catch (SQLException e) 
         {
            // TODO Auto-generated catch block
            e.printStackTrace();
           // Logger.getLogger(Sql.class.getName()).log(Level.SEVERE, null, e);
            FacesFingerPrintProject.logger.log(Level.SEVERE, "ERROR", e);
         }  
         
    }
         
    public void updateParticipant(Participant p) 
     {
          
         try
         {
             preppedStmtUpdate="update participant set identifier=?,fname=?,mname=?,gname=?,nname=?,age=?,gender=?,beachid=?,dateChanged=?, changedBy=? WHERE PTID=?";
            //System.out.println("Check if db print has data: "+ p.getlMiddleFmd());
            PreparedStatement pst= c.prepareStatement(preppedStmtUpdate);
            pst.setString(1, p.getIdentifier());
            pst.setString(2, p.getFamilyName());
            pst.setString(3, p.getMiddleName());
            pst.setString(4, p.getGivenName());
            pst.setString(5, p.getNickName());
            pst.setInt(6, p.getAge());
            pst.setString(7, Character.toString(p.getGender()));//convert xter to string           
            pst.setInt(8, p.getBeachId());
            pst.setTimestamp(9, timestamp); 
            pst.setInt(10, MainMenu.gUser.getUserId());
            pst.setInt(11, p.getParticipant_Id());
            pst.executeUpdate();
        
            int ptid=p.getParticipant_Id();
            if (ptid!=0)
            {
                insertFingerPrint(p,ptid);
            }
            JOptionPane.showMessageDialog(null, "Record Updated...");
         }
         catch (SQLException e) 
         {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Logger.getLogger(Sql.class.getName()).log(Level.SEVERE, null, e);
         } 
         
                //System.out.println(preppedStmtUpdate);
        
    } 
    
    public boolean ParticipantExists(int userID) throws SQLException
    {
	//this statement uses
        String sqlStmt="Select * from participant  WHERE " + participantColumn + "=" + userID + "";
	ResultSet rs=executeQuery(sqlStmt);
	return rs.next();
    }
    
    public int getLastParticipantId() 
    {
        int ptid=0;
        try
        {
            String sqlStmt="Select max(PTID) as ptid from participant;";
            Open();
            Statement st = c.createStatement();
            ResultSet rs=st.executeQuery(sqlStmt);
            while (rs.next())
            {
                ptid=rs.getInt("ptid");
            }
        }
        catch (SQLException e) 
         {
            // TODO Auto-generated catch block
            e.printStackTrace();
            //Logger.getLogger(Sql.class.getName()).log(Level.SEVERE, null, e);
            FacesFingerPrintProject.logger.log(Level.SEVERE, "ERROR", e);
         }
        
        return ptid;
    }
    
    public Participant getParticipant(int participantID)
    {
        Participant p=null;
        try
        {
            String sqlStmt="Select PTID,fname,mname,gname,nname, gender, age, identifier, beachid from participant WHERE " + participantColumn + "=" + participantID + "";
            Open();
            Statement st = c.createStatement();
            ResultSet rs=st.executeQuery(sqlStmt);
            while (rs.next())
            {
                p=new Participant();
                p.setIdentifier(rs.getString("identifier"));  
                p.setFamilyName(rs.getString("fname"));
                p.setGivenName(rs.getString("gname"));
                p.setMiddleName(rs.getString("mname"));
                p.setNickName(rs.getString("nname"));
                p.setGender((rs.getString("gender").charAt(0)));//get string from the db and convert to char type
                p.setAge(rs.getInt("age"));
                p.setBeachId(rs.getInt("beachid"));
                p.setParticipant_Id(rs.getInt("PTID"));
            }
        }
        catch (SQLException e) 
         {
            // TODO Auto-generated catch block
            e.printStackTrace();
            //Logger.getLogger(Sql.class.getName()).log(Level.SEVERE, null, e);
            FacesFingerPrintProject.logger.log(Level.SEVERE, "ERROR", e);
         }
	
        
        return p;
    }
    
    public Participant getParticipant(String identifier) 
    {
        Participant p=null;
        try
        {
            String sqlStmt="Select PTID,fname,mname,gname,nname, gender, age, identifier, beachid from participant WHERE identifier ='" + identifier + "'";
            Open();
            Statement st = c.createStatement();
            ResultSet rs=st.executeQuery(sqlStmt);
            while (rs.next())
            {
                p=new Participant();
                p.setIdentifier(rs.getString("identifier"));  
                p.setFamilyName(rs.getString("fname"));
                p.setGivenName(rs.getString("gname"));
                p.setMiddleName(rs.getString("mname"));
                p.setNickName(rs.getString("nname"));
                p.setGender((rs.getString("gender").charAt(0)));//get string from the db and convert to char type
                p.setAge(rs.getInt("age"));
                p.setBeachId(rs.getInt("beachid"));
                p.setParticipant_Id(rs.getInt("PTID"));
            }
        }
        catch (SQLException e) 
         {
            // TODO Auto-generated catch block
            e.printStackTrace();
           // Logger.getLogger(Sql.class.getName()).log(Level.SEVERE, null, e);
            FacesFingerPrintProject.logger.log(Level.SEVERE, "ERROR", e);
         }
	
        
        return p;
    }
    
     public User getUser(String uName, String pass) 
    {
        User user=null;
        try
        {
            String sqlStmt="Select UserID,lname,username from users WHERE username ='" + uName + "' and pass=password('"+ pass + "');";
            Open();
            Statement statement = c.createStatement();
            ResultSet rs2=statement.executeQuery(sqlStmt);
            while (rs2.next())
            {
                user=new User();
                user.setUserId(rs2.getInt("UserID"));  
                user.setName(rs2.getString("lname"));
                user.setUserName(rs2.getString("username"));              
            }
        }
        catch (SQLException e) 
         {
            // TODO Auto-generated catch block
            e.printStackTrace();
            //Logger.getLogger(Sql.class.getName()).log(Level.SEVERE, null, e);
            FacesFingerPrintProject.logger.log(Level.SEVERE, "ERROR", e);
         }
	
        
        return user;
    }
    
    public int validateIdentifier(String identifier) 
    {
        int p=0;
        try
        {
            String sqlStmt="Select count(PTID) as idcount from participant WHERE identifier ='" + identifier + "'";
            ResultSet rs=executeQuery(sqlStmt);
            while (rs.next())
            {
                p=rs.getInt("idcount");
            }
        }
        catch (SQLException e) 
         {
            // TODO Auto-generated catch block
            e.printStackTrace();
            //Logger.getLogger(Sql.class.getName()).log(Level.SEVERE, null, e);
            FacesFingerPrintProject.logger.log(Level.SEVERE, "ERROR", e);
         }	
        
        return p;
    }
    
    public List<Participant> findParticipant(String search) 
    {
        List<Participant> pList=new ArrayList<Participant>();
        try
        {
            String sqlStmt="Select PTID,fname,mname,gname,nname, gender, age, identifier, beachid from participant WHERE voided=0 and (identifier like '%" + search + "%' or "; //
            sqlStmt=sqlStmt+" fname like '%" + search + "%' or gname like '%" + search + "%' or mname like '%" + search + "%'  or nname like '%" + search + "%')";
            Open();
            Statement st = c.createStatement();
            ResultSet rs=st.executeQuery(sqlStmt);
            while (rs.next())
            {
                Participant p = new Participant();
                p.setIdentifier(rs.getString("identifier"));  
                p.setFamilyName(rs.getString("fname"));
                p.setGivenName(rs.getString("gname"));
                p.setMiddleName(rs.getString("mname"));
                p.setNickName(rs.getString("nname"));
                p.setGender((rs.getString("gender").charAt(0)));//get string from the db and convert to char type
                p.setAge(rs.getInt("age"));
                p.setBeachId(rs.getInt("beachid"));
                p.setParticipant_Id(rs.getInt("PTID"));              
                pList.add(p);
            }
        }
        catch (SQLException e) 
         {
            // TODO Auto-generated catch block
            e.printStackTrace();
            //Logger.getLogger(Sql.class.getName()).log(Level.SEVERE, null, e);
            FacesFingerPrintProject.logger.log(Level.SEVERE, "ERROR", e);
         }
	
        return pList;
    }
    
     public void voidParticipant(Participant p) 
     {
         
         try
         {
            String sqlStmt="update participant set voided=1, voidedBy="+MainMenu.gUser.getUserId()+" WHERE PTID=" + p.getParticipant_Id() + "";
            int rs=executeUpdate(sqlStmt);
         
            sqlStmt="update fingerprint set voided=1,voidedBy="+MainMenu.gUser.getUserId()+" WHERE PTID=" + p.getParticipant_Id() + "";
            rs=executeUpdate(sqlStmt);
         
            if (rs>0)
            {
                JOptionPane.showMessageDialog(null, "Participant Record delete...");
            }
         }
         catch (SQLException e) 
         {
            // TODO Auto-generated catch block
            e.printStackTrace();
           // Logger.getLogger(Sql.class.getName()).log(Level.SEVERE, null, e);
            FacesFingerPrintProject.logger.log(Level.SEVERE, "ERROR", e);
         }        
         
     }
}
