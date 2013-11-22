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
    private static final String URL="jdbc:mysql://localhost:3308/fingerPrintTest?autoReconnect=true";
    private static final String PASSWORD="openmrs";
    private static final String USERNAME="openmrs";
    
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
    public static void Open() throws SQLException
    {
	c=DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
    public static Statement createStatement() throws SQLException {
        //c = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        Open();
        Statement statement = c.createStatement();
        return statement;
    }
    public static ResultSet executeQuery(String query) throws SQLException {
        Statement statement = createStatement();
        ResultSet rs=statement.executeQuery(query);
        //statement.
        //rs.next();
        return rs;
    }
    
    public static int executeUpdate(String query) throws SQLException {
        Statement statement = createStatement();
        return statement.executeUpdate(query);
    }
    
    public void Close() throws SQLException
    {
	 c.close();	
    }
    
    public void finalize()
    {
        try {
                c.close();
	} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void insertParticipant(Participant p) throws SQLException
    {
            preppedStmtInsert="INSERT INTO participant (identifier,fname,mname,gname,nname,age,gender,beachid,dateCreated) VALUES(?,?,?,?,?,?,?,?,?)";
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
    public void insertTrail(Map<String,String> fieldList,String className,int userId) throws SQLException
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
    
    public void updateTrail(Map<String,String> oldFieldList,Map<String,String> newFieldList,String className,int userId,int rcd_id) throws SQLException
    {
        //int i=getLastParticipantId();
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
    public void insertParticipantTrail(Participant p) throws SQLException
    {
            
        //Field[] fields=p.getClass().getFields();
        Class classP=p.getClass(); //create an object of type class to get the class details
        Field[] fields=classP.getDeclaredFields();//get declared fields in a class
        for(int i=0;i<fields.length;i++)
        {
             System.out.println("Field no. :"+i+1);
             preppedStmtInsert="INSERT INTO audittrail (datetime,userid,formname,recordid,fieldname,newvalue) VALUES(?,?,?,?,?,?)";
             PreparedStatement pst= c.prepareStatement(preppedStmtInsert);
             try
             {
                 Field   field= classP.getDeclaredField(fields[i].getName()); //get field names
                 if(!field.toString().contains("finger")||!field.toString().contains("Finger"))
                 {                  
                  pst.setTimestamp(1, timestamp);
                  pst.setInt(2, 5);
                  pst.setString(3, p.getClass().getName());
                  pst.setInt(4, p.getParticipant_Id());
                  pst.setString(5, fields[i].getName());
                  pst.setString(6, (String)field.get(p));
                  pst.execute();
                  System.out.println("Field Name: "+ fields[i].getName()+" Value: "+(String)field.get(p));
                   
                 }         
                 
             }
             catch (NoSuchFieldException e) {
                System.out.println(e);
            } 
            catch (SecurityException e) {
                System.out.println(e);
            } 
            catch (IllegalAccessException e) {
                System.out.println(e);
      }
         
         }              
            //System.out.println("Check if db print has data: "+ p.getlMiddleFmd());
         Close();//ensure all records are persisted on the db before saving prints
         Open();
            
		
    }
    
    public void insertFingerPrint(Participant p,int ptid) throws SQLException
    {       
            
        if(!p.getLstFingerPrints().isEmpty())
        {
            for(FingerPrint fp: p.getLstFingerPrints())
            {
                 preppedStmtInsert="INSERT INTO fingerprint (PTID,print1,printIndex,dateCreated) VALUES(?,?,?,?)";
                //System.out.println("Check if db print has data: "+ p.getlMiddleFmd());
                
		PreparedStatement pst= c.prepareStatement(preppedStmtInsert);
		pst.setInt(1, ptid);
               	pst.setBytes(2, fp.getFmd());
                pst.setInt(3, fp.getIndex());
                pst.setTimestamp(4, timestamp);
                pst.execute();
            }
            
        }
        if (p.getFingerPrint()!=null)
            {
                 preppedStmtInsert="INSERT INTO fingerprint (PTID,print1,printIndex,dateCreated) VALUES(?,?,?,?)";
            //System.out.println("Check if db print has data: "+ p.getlMiddleFmd());
            
		PreparedStatement pst= c.prepareStatement(preppedStmtInsert);
		pst.setInt(1, ptid);
               	pst.setBytes(2, p.getFingerPrint().getFmd());
                pst.setInt(3, p.getFingerPrint().getIndex());
                pst.setTimestamp(4, timestamp);
                pst.execute();
            }
            
             if (p.getFingerPrint2()!=null)
            {
                 preppedStmtInsert="INSERT INTO fingerprint (PTID,print1,printIndex,dateCreated) VALUES(?,?,?,?)";
            //System.out.println("Check if db print has data: "+ p.getlMiddleFmd());
            
		PreparedStatement pst= c.prepareStatement(preppedStmtInsert);
		pst.setInt(1, ptid);
               	pst.setBytes(2, p.getFingerPrint2().getFmd());
                pst.setInt(3, p.getFingerPrint2().getIndex());
                pst.setTimestamp(4, timestamp);
                pst.execute();
            }
        
       
                
		
    }
    public void InsertUser(Participant p) throws SQLException
    {
            preppedStmtInsert="INSERT INTO " + tableName + "(" + familyNameColumn + "," + print1Column + ") VALUES(?,?)";
            //System.out.println("Check if db print has data: "+ p.getlMiddleFmd());
		PreparedStatement pst= c.prepareStatement(preppedStmtInsert);
		pst.setString(1, p.getFamilyName());
		//pst.setBytes(2, p.getlMiddleFmd());
		pst.execute();
    }
    
    public List<Sql.Record> GetAllFPData() throws SQLException
    {
		List<Sql.Record> listParticipants=new ArrayList<Sql.Record>();
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
		return listParticipants;
    }
    
    public List<Beach> getAllBeachData() throws SQLException
    {
        List<Beach> locationList=new ArrayList<Beach>();
        //String sqlStmt="Select * from fingerprint";
		String sqlStmt="Select beachid,name,county,description from beach where voided=0"; //for testing first
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
        
        return locationList;
    }
    
    public Map<Integer, Beach> getBeachMap() throws SQLException
    {
        Map<Integer, Beach> locationList=new HashMap<Integer,Beach>();
        //String sqlStmt="Select * from fingerprint";
		String sqlStmt="Select beachid,name,county from beach where voided=0"; //for testing first
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
        
        return locationList;
    }
    
    public List<String> getCounty() throws SQLException
    {
        List<String> locationList=new ArrayList<String>();
        //String sqlStmt="Select * from fingerprint";
		String sqlStmt="Select distinct county from beach where voided=0"; //for testing first
		ResultSet rs = executeQuery(sqlStmt);
		while(rs.next())
		{
                    System.out.println("DB Read sucess: Loading County names");
                    locationList.add(rs.getString("county"));                  
		}	
        
        return locationList;
    }
    
    public void insertBeach(Beach beach)throws SQLException
    {
        preppedStmtInsert="INSERT INTO beach (name,description,county,dateCreated) VALUES(?,?,?,?)";
            //System.out.println("Check if db print has data: "+ p.getlMiddleFmd());
		PreparedStatement pst= c.prepareStatement(preppedStmtInsert);
		pst.setString(1,beach.getName());
                pst.setString(2,beach.getDescription());
                pst.setString(3,beach.getCounty());
                pst.setTimestamp(4, timestamp);
		pst.execute();
    }
    
    public Beach getBeach(int id) throws SQLException
    {
        Beach b=new Beach();
	String sqlStmt="Select name,description,county from beach WHERE beachid=" + id + "";
	ResultSet rs=executeQuery(sqlStmt);
	while (rs.next())
        {
              b.setName(rs.getString("name"));  
              b.setDescription(rs.getString("description"));
              b.setCounty(rs.getString("county"));              
              b.setBeachId(rs.getInt("beachid"));
        }
        
        return b;
    }
    public void voidBeach(Beach b) throws SQLException
     {
         
         String sqlStmt="updated beach set voided=1,dateVoided=now() WHERE beachid=" + b.getBeachId() + "";
         int rs=executeUpdate(sqlStmt);
         
                  
         if (rs>0)
         {
             JOptionPane.showMessageDialog(null, "Participant Record delete...");
         }
         
     }
    
    public void updateBeach(Beach b) throws SQLException
     {
                    
         preppedStmtUpdate="update beach set name=?, description=?, county=?, dateChanged=? WHERE beachid=?";
         PreparedStatement pst= c.prepareStatement(preppedStmtUpdate);
		pst.setString(1,b.getName());
                pst.setString(2,b.getDescription());
                pst.setString(3,b.getCounty());	
                pst.setTimestamp(4, timestamp);
                pst.setInt(5, b.getBeachId());
		pst.executeUpdate();
                
                System.out.println(preppedStmtUpdate);
    }
         
    public void updateParticipant(Participant p) throws SQLException
     {
          preppedStmtUpdate="update participant set identifier=?,fname=?,mname=?,gname=?,nname=?,age=?,gender=?,beachid=?,dateChanged=? WHERE PTID=?";
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
        pst.setInt(10, p.getParticipant_Id());
        pst.executeUpdate();
        
        JOptionPane.showMessageDialog(null, "Record Updated...");
                //System.out.println(preppedStmtUpdate);
    } 
    
    public boolean ParticipantExists(int userID) throws SQLException
    {
	//this statement uses
        String sqlStmt="Select * from participant  WHERE " + participantColumn + "=" + userID + "";
	ResultSet rs=executeQuery(sqlStmt);
	return rs.next();
    }
    
    public int getLastParticipantId() throws SQLException
    {
        int ptid=0;
        String sqlStmt="Select max(PTID) as ptid from participant;";
	ResultSet rs=executeQuery(sqlStmt);
	while (rs.next())
        {
            ptid=rs.getInt("ptid");
        }
        return ptid;
    }
    
    public Participant getParticipant(int participantID) throws SQLException
    {
        Participant p=new Participant();
	String sqlStmt="Select PTID,fname,mname,gname,nname, gender, age, identifier, beachid from participant WHERE " + participantColumn + "=" + participantID + "";
	ResultSet rs=executeQuery(sqlStmt);
	while (rs.next())
        {
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
        
        return p;
    }
    
    public Participant getParticipant(String identifier) throws SQLException
    {
        Participant p=new Participant();
	String sqlStmt="Select PTID,fname,mname,gname,nname, gender, age, identifier, beachid from participant WHERE identifier ='" + identifier + "'";
	ResultSet rs=executeQuery(sqlStmt);
	while (rs.next())
        {
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
        
        return p;
    }
    
    public int validateIdentifier(String identifier) throws SQLException
    {
        int p=0;
	String sqlStmt="Select count(PTID) as idcount from participant WHERE identifier ='" + identifier + "'";
	ResultSet rs=executeQuery(sqlStmt);
	while (rs.next())
        {
              p=rs.getInt("idcount");
        }
        
        return p;
    }
    
    public List<Participant> findParticipant(String search) throws SQLException
    {
        List<Participant> pList=new ArrayList<Participant>();
	String sqlStmt="Select PTID,fname,mname,gname,nname, gender, age, identifier, beachid from participant WHERE identifier like '%" + search + "%' or "; //
        sqlStmt=sqlStmt+" fname like '%" + search + "%' or gname like '%" + search + "%' or mname like '%" + search + "%'  or nname like '%" + search + "%'";
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
        
        return pList;
    }
    
     public void voidParticipant(Participant p) throws SQLException
     {
         
         String sqlStmt="updated participant set voided=1 WHERE PTID=" + p.getParticipant_Id() + "";
         int rs=executeUpdate(sqlStmt);
         
         sqlStmt="updated participant set voided=1 WHERE PTID=" + p.getParticipant_Id() + "";
         rs=executeUpdate(sqlStmt);
         
         if (rs>0)
         {
             JOptionPane.showMessageDialog(null, "Participant Record delete...");
         }
         
     }
}
