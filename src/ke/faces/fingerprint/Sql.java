/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ke.faces.fingerprint;

/**
 *
 * @author LENOVO USER
 */
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
 * Manage All database access calls
 */
public class Sql {
    
    private static final String participantColumn="PTID";
    private static final String print1Column="print1"; 
   
    
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
    
    /**
     * Open connection to the database
     */
    public  void Open() 
    {
	try
        {
            c=DriverManager.getConnection(URL, USERNAME, PASSWORD);
            FacesFingerPrintProject.logger.info("Opening Connection to the Database");
        }
        catch(SQLException ex)
        {
            //Logger.getLogger(Sql.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, ex);
            //Log Error messages to the log file
            FacesFingerPrintProject.logger.log(Level.SEVERE, "ERROR", ex);
        }
        
    }
    
    /**
     * return a statement to be use for executing the query
     * @return statement
     */
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
            JOptionPane.showMessageDialog(null, ex);
            FacesFingerPrintProject.logger.log(Level.SEVERE, "ERROR", ex);
        }
        return statement;
    }
    
    /**
     * Return a result set given an sql statement
     * @param String query 
     * @return ResultSet
     */
    public  ResultSet executeQuery(String query){
        ResultSet rs=null;
        try
        {
             Statement statement = createStatement();
            //FacesFingerPrintProject.logger.info("Executing Query: " + query);
            rs=statement.executeQuery(query);            //statement.
            //rs.next();
            
        }
        catch(SQLException ex)
        {
            //Logger.getLogger(Sql.class.getName()).log(Level.SEVERE, null, ex);
             JOptionPane.showMessageDialog(null, ex);
            FacesFingerPrintProject.logger.log(Level.SEVERE, "ERROR", ex);
        }
       
        return rs;
    }
    
    /**
     * return the number of record updated after executing an update query
     * @param String query
     * @return number of rows updated
     */
    public  int executeUpdate(String query) throws SQLException {
        Statement statement = createStatement();
        FacesFingerPrintProject.logger.info("Executing an Update Query: "+ query);
        return statement.executeUpdate(query);
    }
    
    /**
     * Close open database connection
     */
    public void Close()
    {
	try 
        {
            FacesFingerPrintProject.logger.info("Closing Connection to the Database");
            c.close();
	}
        catch (SQLException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            //Logger.getLogger(Sql.class.getName()).log(Level.SEVERE, null, e);
            JOptionPane.showMessageDialog(null, e);
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
            JOptionPane.showMessageDialog(null, e);
            FacesFingerPrintProject.logger.log(Level.SEVERE, "ERROR", e);
        }
        
    }
    
   /**
    * Append Participant to the database
    * @param participant 
    */
    
    public void insertParticipant(Participant p) 
    {
            
            try
            {
                //log infor
                FacesFingerPrintProject.logger.info("Append Participant Details to the db...");
                preppedStmtInsert="INSERT INTO participant (identifier,fname,mname,gname,nname,age,gender,beachid,dateCreated,creator) VALUES(?,?,?,?,?,?,?,?,?,?)";
                //System.out.println("Check if db print has data: "+ p.getlMiddleFmd());
            
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
                JOptionPane.showMessageDialog(null, e);
                FacesFingerPrintProject.logger.log(Level.SEVERE, "ERROR", e);
            }
		
    }
    
    /**
     * Append Audit trail to the database
     * @param fieldList
     * @param className
     * @param userId 
     */
   
    public void insertTrail(Map<String,String> fieldList,String className,int userId)
    {
        try
        {
            //Log Info
            FacesFingerPrintProject.logger.info("Append FingerPrint for Participant: "+getLastParticipantId()+"...");
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
            JOptionPane.showMessageDialog(null, e);
            FacesFingerPrintProject.logger.log(Level.SEVERE, "ERROR", e);
        }
        
    }
    
    /**
     * Update Audit trail to the database
     * @param oldFieldList
     * @param newFieldList
     * @param className
     * @param userId
     * @param rcd_id 
     */
   
    public void updateTrail(Map<String,String> oldFieldList,Map<String,String> newFieldList,String className,int userId,int rcd_id) 
    {
        //int i=getLastParticipantId();
        try
        {   //log Info
            FacesFingerPrintProject.logger.info("Audit Trail for record: "+rcd_id + " updated....");
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
            JOptionPane.showMessageDialog(null, e);
            FacesFingerPrintProject.logger.log(Level.SEVERE, "ERROR", e);
        }
        
    }
    
    /**
     * log void record audit trail
     * @param className
     * @param userId
     * @param rcd_id 
     */
    public void voidAuditTrail(String className,int userId,int rcd_id) 
    {
        //int i=getLastParticipantId(); 
        try
        {
            //Log Info
            FacesFingerPrintProject.logger.info("Void Record Audit Trail for: "+ className);
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
            FacesFingerPrintProject.logger.info("Audit Trail for record: "+rcd_id + " voided....");
        }
        catch (SQLException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            //Logger.getLogger(Sql.class.getName()).log(Level.SEVERE, null, e);
            JOptionPane.showMessageDialog(null, e);
            FacesFingerPrintProject.logger.log(Level.SEVERE, "ERROR", e);
        }            
        
    }
   
    /**
     * Append finger print to the database
     * @param participant
     * @param ptid 
     */
    public void insertFingerPrint(Participant p,int ptid) 
    {       
            
        try
        {
            if(!p.getLstFingerPrints().isEmpty())
            {
                //Log Info
                FacesFingerPrintProject.logger.info("Append Finger Prints for Id: "+ptid + " Started....");
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
            //Logger.getLogger(Sql.class.getName()).log(Level.SEVERE, "ERROR", e);
            //Log Error
            JOptionPane.showMessageDialog(null, e);
            FacesFingerPrintProject.logger.log(Level.SEVERE, "ERROR", e);
        }        
		
    }
    
    /**
     * Encrypt password
     * @param pass
     * @return encPass
     */
    public String encryptPass(String pass)
    {
        String encPass=pass;
        String sqlStmt="Select password('"+pass+"')"; //for testing first
             
        try
        {
            Open();
            Statement statement = c.createStatement();
            ResultSet rs = statement.executeQuery(sqlStmt);
            while(rs.next())
            {
               encPass=rs.getString(0) ;
            }
        }           
        catch (SQLException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            //Logger.getLogger(Sql.class.getName()).log(Level.SEVERE, "ERROR", e);
            //log Error
            JOptionPane.showMessageDialog(null, e);
            FacesFingerPrintProject.logger.log(Level.SEVERE, "ERROR", e);
        }
        
        return encPass;
    }
    
    /**
     * Append User to the database
     * @param user 
     */
    public void insertUser(User user)
    {
            
        try
        {
            //log info
            FacesFingerPrintProject.logger.info("Append User Details ");
            preppedStmtInsert="INSERT INTO user (lname, username,pass, dateCreated) VALUES(?,?,?,?)";
            //System.out.println("Check if db print has data: "+ p.getlMiddleFmd());
            PreparedStatement pst= c.prepareStatement(preppedStmtInsert);
            pst.setString(1, user.getName());
            pst.setString(2, user.getUserName());
            pst.setString(3, encryptPass(user.getPassword()));
            pst.setTimestamp(4, timestamp);
            pst.execute();
        }
        catch (SQLException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
           // Logger.getLogger(Sql.class.getName()).log(Level.SEVERE, null, e);
            JOptionPane.showMessageDialog(null, e);
            FacesFingerPrintProject.logger.log(Level.SEVERE, "ERROR", e);
        }
        
    }
    
    /**
     * Get all Finger prints in the database and load  in memory
     * @return list of all fingerprints
     */
    public List<Sql.Record> GetAllFPData()
    {
	List<Sql.Record> listParticipants=new ArrayList<Sql.Record>();
        try
        {
            //log info
            FacesFingerPrintProject.logger.info("Loading Finger prints in the memory for first searches....  ");
            //get all finger prints including the voided if any
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
            JOptionPane.showMessageDialog(null, e);
            FacesFingerPrintProject.logger.log(Level.SEVERE, "ERROR", e);
        }
		
	return listParticipants;
    }
    
    /**
     * Load all Beach data details
     * @return list of all beaches
     */
    public List<Beach> getAllBeachData() 
    {
        List<Beach> locationList=new ArrayList<Beach>();
        try
        {
            //log info
            FacesFingerPrintProject.logger.info("Loading all beach data in memory ");
            //String sqlStmt="Select * from fingerprint";
            String sqlStmt="Select beachid,name,county,description from beach where voided=0 order by name asc;"; //for testing first
           // Open();
          //  Statement statement = c.createStatement();
            ResultSet rs = executeQuery(sqlStmt);
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
            JOptionPane.showMessageDialog(null, e);
            FacesFingerPrintProject.logger.log(Level.SEVERE, "ERROR", e);
        }
        	
        
        return locationList;
    }
    
    /**
     * Create Hash map for quick access
     * @return Map of all Beaches
     */
    public Map<Integer, Beach> getBeachMap() 
    {
        Map<Integer, Beach> locationList=new HashMap<Integer,Beach>();
        try
        {
            //log info
            FacesFingerPrintProject.logger.info("Loading Beach Map in Memory.... ");
            //String sqlStmt="Select * from fingerprint";
            String sqlStmt="Select beachid,name,county from beach where voided=0"; //for testing first
           // Open();
           // Statement st = c.createStatement();
            ResultSet rs = executeQuery(sqlStmt);
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
            JOptionPane.showMessageDialog(null, e);
            FacesFingerPrintProject.logger.log(Level.SEVERE, "ERROR", e);
        }
        	
        
        return locationList;
    }
    
    /**
     * Load County details in an Array List
     * @return List of all counties
     */
    public List<String> getCounty() 
    {
        List<String> locationList=new ArrayList<String>();
        
        try
        {
            //log info
            FacesFingerPrintProject.logger.info("Loading County Details in Memory... ");
            //String sqlStmt="Select * from fingerprint";
            String sqlStmt="Select distinct county from beach where voided=0"; //for testing first
            //Open();
           // Statement st = c.createStatement();
            ResultSet rs = executeQuery(sqlStmt);
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
            JOptionPane.showMessageDialog(null, e);
            FacesFingerPrintProject.logger.log(Level.SEVERE, "ERROR", e);
        }
        	
        
        return locationList;
    }
    
    /**
     * Append Beach details to the database
     * @param beach 
     */
    public void insertBeach(Beach beach)
    {
        try
        {
            //log info
            FacesFingerPrintProject.logger.info("Append Beach Details... ");
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
            JOptionPane.showMessageDialog(null, e);
            FacesFingerPrintProject.logger.log(Level.SEVERE, "ERROR", e);
        }
       
    }
    
    /**
     * Get Beach given Beach Id
     * @param id
     * @return Beach 
     */
    public Beach getBeach(int id) 
    {
        Beach b=null;//new Beach();
        try
        {
            //log info
            FacesFingerPrintProject.logger.info("Get Beach Details Given Beach id ");
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
            JOptionPane.showMessageDialog(null, e);
            FacesFingerPrintProject.logger.log(Level.SEVERE, "ERROR", e);
        }   
        
        return b;
    }
    
    /**
     * Void Beach
     * @param b 
     */
    public void voidBeach(Beach b)
     {
         try
         {
             //log info
             FacesFingerPrintProject.logger.info("Void Beach Details record ");
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
            JOptionPane.showMessageDialog(null, e);
            FacesFingerPrintProject.logger.log(Level.SEVERE, "ERROR", e);
        }         
         
         
     }
    
    /**
     * Update beach Details
     * @param beach 
     */
    public void updateBeach(Beach b) 
    {
                    
         try
         {
             //log info
             FacesFingerPrintProject.logger.info("Updating Details... ");
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
            JOptionPane.showMessageDialog(null, e);
            FacesFingerPrintProject.logger.log(Level.SEVERE, "ERROR", e);
         }  
         
    }
         
    /**
     * Update Participant details
     * @param participant 
     */
    public void updateParticipant(Participant p) 
     {
          
         try
         {
             //log info
             FacesFingerPrintProject.logger.info("Updating Participant Details... ");
             //create a prepared statement
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
            //Logger.getLogger(Sql.class.getName()).log(Level.SEVERE, null, e);
            //Log error
            FacesFingerPrintProject.logger.log(Level.SEVERE, "ERROR", e);
         }         
    } 
    
    /**
     * 
     * @param userID
     * @return true/false
     *  
     */
    public boolean ParticipantExists(int userID)
    {
	boolean found=false;
        try
        {
            //this statement uses
            String sqlStmt="Select * from participant  WHERE " + participantColumn + "=" + userID + "";
            ResultSet rs=executeQuery(sqlStmt);
            found=rs.next();
        }
        catch (SQLException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e);
            //Logger.getLogger(Sql.class.getName()).log(Level.SEVERE, null, e);
             //Log error
            FacesFingerPrintProject.logger.log(Level.SEVERE, "ERROR", e);
         } 
        
	return found;
    }
    
    public int getLastParticipantId() 
    {
        int ptid=0;
        try
        {
            //log info
            FacesFingerPrintProject.logger.info("Geting the Last Participant Internal Id... ");
            String sqlStmt="Select max(PTID) as ptid from participant;";
           // Open();
           // Statement st = c.createStatement();
            ResultSet rs=executeQuery(sqlStmt);
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
            JOptionPane.showMessageDialog(null, e);
            FacesFingerPrintProject.logger.log(Level.SEVERE, "ERROR", e);
         }
        
        return ptid;
    }
    
    /**
     * Get Participant Details given the Internal db Id
     * @param participantID
     * @return Participant
     */
    public Participant getParticipant(int participantID)
    {
        Participant p=null;
        try
        {
            //log info
            FacesFingerPrintProject.logger.info("Getting Participant Details given the internal DB id... ");
            String sqlStmt="Select PTID,fname,mname,gname,nname, gender, age, identifier, beachid from participant WHERE " + participantColumn + "=" + participantID + "";
            //Open();
           // Statement st = c.createStatement();
            ResultSet rs=executeQuery(sqlStmt);
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
            JOptionPane.showMessageDialog(null, e);
            FacesFingerPrintProject.logger.log(Level.SEVERE, "ERROR", e);
         }
	
        
        return p;
    }
    
    /**
     * get Participant given identifier
     * @param identifier
     * @return participant
     */
    public Participant getParticipant(String identifier) 
    {
        Participant p=null;
        try
        {
            //log info
            FacesFingerPrintProject.logger.info("Getting Participant Details given Identifier... ");
            String sqlStmt="Select PTID,fname,mname,gname,nname, gender, age, identifier, beachid from participant WHERE identifier ='" + identifier + "'";
           // Open();
            //Statement st = c.createStatement();
            ResultSet rs=executeQuery(sqlStmt);
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
            JOptionPane.showMessageDialog(null, e);
            FacesFingerPrintProject.logger.log(Level.SEVERE, "ERROR", e);
         }        
        return p;
    }
    
    /**
     * get User given the user name and password
     * @param uName
     * @param pass
     * @return user
     */
    public User getUser(String uName, String pass) 
    {
        User user=null;
        try
        {
            //log info
            FacesFingerPrintProject.logger.info("Getting user Details given username and pass... ");
            String sqlStmt="Select UserID,lname,username from users WHERE username ='" + uName + "' and pass=password('"+ pass + "');";
           // Open();
           // Statement statement = c.createStatement();
            ResultSet rs2=executeQuery(sqlStmt);
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
            JOptionPane.showMessageDialog(null, e);
            FacesFingerPrintProject.logger.log(Level.SEVERE, "ERROR", e);
         }
	
        
        return user;
    }
    
    /**
     * Check if Identifier already exists
     * @param identifier
     * @return identifier count
     */
    public int validateIdentifier(String identifier) 
    {
        int p=0;
        try
        {
            //log info
            FacesFingerPrintProject.logger.info("Validating Participant Identifier... ");
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
            JOptionPane.showMessageDialog(null, e);
            FacesFingerPrintProject.logger.log(Level.SEVERE, "ERROR", e);
         }	
        
        return p;
    }
    
    /**
     * Get a list of participants given the search criteria
     * @param search
     * @return List of participants
     */
    public List<Participant> findParticipant(String search) 
    {
        List<Participant> pList=new ArrayList<Participant>();
        try
        {
            //log info
            FacesFingerPrintProject.logger.info("Get List of participants given the search criteria... ");
            String sqlStmt="Select PTID,fname,mname,gname,nname, gender, age, identifier, beachid from participant WHERE voided=0 and (identifier like '%" + search + "%' or "; //
            sqlStmt=sqlStmt+" fname like '%" + search + "%' or gname like '%" + search + "%' or mname like '%" + search + "%'  or nname like '%" + search + "%') order by identifier asc;";
           // Open();
           // Statement st = c.createStatement();
            ResultSet rs=executeQuery(sqlStmt);
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
            JOptionPane.showMessageDialog(null, e);
            FacesFingerPrintProject.logger.log(Level.SEVERE, "ERROR", e);
         }
	
        return pList;
    }
    
    /**
     * Void Participant
     * @param participant 
     */
     public void voidParticipant(Participant p) 
     {
         
         try
         {
             //log info
            FacesFingerPrintProject.logger.info("Voiding Participant Record No: "+p.getParticipant_Id());
            String sqlStmt="update participant set voided=1, voidedBy="+MainMenu.gUser.getUserId()+" WHERE PTID=" + p.getParticipant_Id() + "";
            int rs=executeUpdate(sqlStmt);
         
            sqlStmt="update fingerprint set voided=1,voidedBy="+MainMenu.gUser.getUserId()+" WHERE PTID=" + p.getParticipant_Id() + "";
            rs=executeUpdate(sqlStmt);
         
            if (rs>0)
            {
                JOptionPane.showMessageDialog(null, "Participant Record deleted...");
            }
         }
         catch (SQLException e) 
         {
            // TODO Auto-generated catch block
            e.printStackTrace();
           // Logger.getLogger(Sql.class.getName()).log(Level.SEVERE, null, e);
            JOptionPane.showMessageDialog(null, e);
            FacesFingerPrintProject.logger.log(Level.SEVERE, "ERROR", e);
         }        
         
     }
}
