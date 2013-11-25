/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ke.faces.fingerprint;

import com.digitalpersona.uareu.Engine;
import com.digitalpersona.uareu.Fmd;
import com.digitalpersona.uareu.Reader;
import com.digitalpersona.uareu.UareUException;
import com.digitalpersona.uareu.UareUGlobal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import com.digitalpersona.uareu.*;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
/**
 *
 * @author LENOVO USER
 */
public class Identification {
    
    private Fmd fmdToIdentify = null;
    private Sql db;
	
    public List<Sql.Record> m_listOfRecords=new ArrayList<Sql.Record>();
    public List<Fmd> m_fmdList=new ArrayList<Fmd>();
    public Fmd[] m_fmdArray=null;  //Will hold final array of FMDs to identify against
   
    public static boolean found;
   
  
   
   public Identification()
   {
       db=new Sql();
       loadFingerPrints();
   }
   public void loadFingerPrints()
   {
       //return m_fmds and participant Ids;
       try {
                this.m_listOfRecords=db.GetAllFPData();
		for(Sql.Record record:this.m_listOfRecords)
		{
                    
                    Fmd fmd = UareUGlobal.GetImporter().ImportFmd(record.fmdBinary,Fmd.Format.DP_REG_FEATURES,Fmd.Format.DP_REG_FEATURES);
                   // Fmd fmd = UareUGlobal.GetImporter().ImportFmd(record.fmdBinary,Fmd.Format.ANSI_378_2004,Fmd.Format.ANSI_378_2004);
                    this.m_fmdList.add(fmd);
                    System.out.println("Memory Load Sucess");
		}
		m_fmdArray=new Fmd[this.m_fmdList.size()];
		this.m_fmdList.toArray(m_fmdArray);
	} catch (SQLException e1) 
        {
            // TODO Auto-generated catch block
            MessageBox.DpError("Failed to load FMDs from database.  Please check connection string in code.",null);			
            return;
	} catch (UareUException e1)
        {
            // TODO Auto-generated catch block
            JOptionPane.showMessageDialog(null, "Error importing fmd data.");
            return;
	}
	
   }
   
   public boolean ProcessCaptureResult(CaptureThread.CaptureEvent evt){
       
       boolean bCanceled = false;
        
       if(null != evt.capture_result)
       {
           if(null != evt.capture_result.image && Reader.CaptureQuality.GOOD == evt.capture_result.quality)
           {
                     
               System.out.println("Extracted Image Quality "+evt.capture_result.quality);
               //extract features
               Fmd fmdToIdentify = null;               
                Engine engine = UareUGlobal.GetEngine();
		try{
                    //m_imagePanel.showImage(evt.capture_result.image);
                    //we use DP_VER_FEATURES - I guess this verifers to finger print verication features
			fmdToIdentify = engine.CreateFmd(evt.capture_result.image, Fmd.Format.DP_VER_FEATURES);
                         System.out.println("Identify Image Captured size "+fmdToIdentify.getData().length);
                         
		}
		catch(UareUException e)
                { 
                    e.printStackTrace();
                    MessageBox.DpError("Engine.CreateFmd()", e); 
                }
                found=false;				
		
                try{
                    //target false positive identification rate: 0.00001
                    //for a discussion of setting the threshold as well as the statistical validity of the dissimilarity score and error rates, consult the Developer Guide.
                    int falsepositive_rate = Engine.PROBABILITY_ONE / 1000000; 
			
                    //get the matching finger print
                    //Engine.Candidate[] vCandidates = engine.Identify(fmdToIdentify, 0, m_fmds, falsepositive_rate,m_fmds.length);
                    //Perform identification            
                            
                    Engine.Candidate[] vCandidates = engine.Identify(fmdToIdentify, 0, m_fmdArray, falsepositive_rate,m_fmdArray.length);
                    if(0 != vCandidates.length)
                    {
                        //optional: to get false match rate compare with the top candidate
			int falsematch_rate = engine.Compare(fmdToIdentify, 0, m_fmdArray[vCandidates[0].fmd_index], vCandidates[0].view_index);
								
			String str = String.format("Fingerprint identified, %s\n", vCandidates[0].fmd_index);
                        System.out.println("Identified Participant Id: "+ m_listOfRecords.get(vCandidates[0].fmd_index).getPTID());        
                        System.out.println(str);
			//m_text.append(str);
			str = String.format("dissimilarity score: 0x%x.\n", falsematch_rate);
                        System.out.println(str);
				//m_text.append(str);
			str = String.format("false match rate: %e.\n\n\n", (double)(falsematch_rate / Engine.PROBABILITY_ONE));
                        System.out.println(str);
				//m_text.append(str);
                       try
                       {
                           //get existing participant details
                           Participant p=db.getParticipant(m_listOfRecords.get(vCandidates[0].fmd_index).getPTID());
                           Registration.oldParticipant=p;
                           Registration.txt_Fname.setText(p.getFamilyName());
                           Registration.txt_Gname.setText(p.getGivenName());
                           Registration.txt_Mname.setText(p.getMiddleName());
                           Registration.txt_Nname.setText(p.getNickName());
                           Registration.txt_Age.setText(Integer.toString(p.getAge()));
                           if(p.getGender()=='M')
                           {
                               Registration.cboGender.setSelectedIndex(0);
                           }
                           else
                           {
                                Registration.cboGender.setSelectedIndex(1);
                           }
                           //get the participant beach name and county
                           //the hash map contains the key of the beach and the value which is of type Beach
                           //This allow me to get the be Beach Id and all the related info abt a given beach
                           
                           Beach b;
                           b=(Beach)MainMenu.beachMap.get(p.getBeachId());
                           Registration.cboLocation.setSelectedItem(b.getName());
                           Registration.cboCty.setSelectedItem(b.getCounty());
                           Registration.txt_Identifier.setText(p.getIdentifier());
                           found=true;
                           String msg="Participant ("+p.getGivenName()+" " + p.getFamilyName()+") is Already Enrolled in the Database";
                           JOptionPane.showMessageDialog(null, msg);
                       }
                       catch(SQLException ex)
                       {
                           ex.printStackTrace();
                       }
                        
                     }
                     else{
                            String str="Participant was not identified.\n\n\n";
                            System.out.println(str);
			}
		} 
                catch(UareUException ex)
                    { 
                        ex.printStackTrace();
                        MessageBox.DpError("Engine.Identify()", ex);
                    }
						
           }
           else if(Reader.CaptureQuality.CANCELED == evt.capture_result.quality)
           {
               //capture or streaming was canceled, just quit
		bCanceled = true;
            }
            else{
                	//bad quality
			MessageBox.BadQuality(evt.capture_result.quality);
		}
	}
	else if(null != evt.exception){
            //exception during capture
            MessageBox.DpError("Capture", evt.exception);
            bCanceled = true;
		}
	else if(null != evt.reader_status){
            //reader failure
            MessageBox.BadStatus(evt.reader_status);
            bCanceled = true;
	}

		return !bCanceled;
	}
   
   public boolean identifyParticipant(Fmd fmdToIdentify)
   {
       
       boolean bFound = false;
        
       if(null != fmdToIdentify)
       {              
            Engine engine = UareUGlobal.GetEngine();             				
		
                try{
                    //target false positive identification rate: 0.00001
                    //for a discussion of setting the threshold as well as the statistical validity of the dissimilarity score and error rates, consult the Developer Guide.
                    int falsepositive_rate = Engine.PROBABILITY_ONE / 100000; 
			
                    //get the matching finger print
                    //Engine.Candidate[] vCandidates = engine.Identify(fmdToIdentify, 0, m_fmds, falsepositive_rate,m_fmds.length);
                    //Perform identification            
                            
                    Engine.Candidate[] vCandidates = engine.Identify(fmdToIdentify, 0, m_fmdArray, falsepositive_rate,m_fmdArray.length);
                    if(0 != vCandidates.length)
                    {
                        //optional: to get false match rate compare with the top candidate
                        bFound=true;
			int falsematch_rate = engine.Compare(fmdToIdentify, 0, m_fmdArray[vCandidates[0].fmd_index], vCandidates[0].view_index);
								
			String str = String.format("Fingerprint identified, %s\n", vCandidates[0].fmd_index);
                        System.out.println("Identified Participant Id: "+ m_listOfRecords.get(vCandidates[0].fmd_index).getPTID());        
                        System.out.println(str);
			//m_text.append(str);
			str = String.format("dissimilarity score: 0x%x.\n", falsematch_rate);
                        System.out.println(str);
				//m_text.append(str);
			str = String.format("false match rate: %e.\n\n\n", (double)(falsematch_rate / Engine.PROBABILITY_ONE));
                        System.out.println(str);
				//m_text.append(str);
                     }
                     else{
                            String str="Fingerprint was not identified.\n\n\n";
                            System.out.println(str);
                            JOptionPane.showMessageDialog(null, str);
			}
		} 
                catch(UareUException ex)
                    { 
                        ex.printStackTrace();
                        MessageBox.DpError("Engine.Identify()", ex);
                    }
       }
						
           return bFound;
	}
   
   public boolean getFoundStatus()
   {
       return found;
   }
}

