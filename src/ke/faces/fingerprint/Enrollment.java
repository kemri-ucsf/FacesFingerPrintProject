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

/**
 *
 * @author LENOVO USER
 */
public class Enrollment implements Engine.EnrollmentCallback{
    
    private CaptureThread  m_capture;
    private boolean m_bCancel;
    private CaptureThread.CaptureEvent  m_evt;
    private Fmd enroll_fmd=null;
    
     public Enrollment(CaptureThread.CaptureEvent evt)
     {
          m_evt=evt;
     }
     
     public Engine.PreEnrollmentFmd GetFmd(Fmd.Format format)
        {
            Engine.PreEnrollmentFmd prefmd = null;

            while(null == prefmd)
            {
                System.out.println("@ Create preFMD Image Quality: "+m_evt.capture_result.quality);
               
		if(null != m_evt.capture_result)
                {
                    if(Reader.CaptureQuality.CANCELED == m_evt.capture_result.quality)
                    {
                        //capture canceled, return null
			break;
                    }
                   
                    else if(null !=m_evt.capture_result.image && Reader.CaptureQuality.GOOD == m_evt.capture_result.quality)
                    {
                        //acquire engine
			Engine engine = UareUGlobal.GetEngine();
			
			try{
                            //extract features
                            Fmd fmd = engine.CreateFmd(m_evt.capture_result.image, Fmd.Format.DP_PRE_REG_FEATURES);
								
                            //return prefmd 
                            prefmd = new Engine.PreEnrollmentFmd();
                            prefmd.fmd = fmd;
                            prefmd.view_index = 0;
								
				//send success
				//SendToListener(ACT_FEATURES, null, null, null, null);
                            }
			catch(UareUException e)
                        { 
                                //send extraction error
				//SendToListener(ACT_FEATURES, null, null, null, e);
                                e.printStackTrace();
                          }
                    }
                    else
                    {
			//send quality result
			//SendToListener(ACT_CAPTURE, null, evt.capture_result, evt.reader_status, evt.exception);
                    }
		}
		else{
			//send capture error
			//SendToListener(ACT_CAPTURE, null, evt.capture_result, evt.reader_status, evt.exception);
                    }
              } // end of the while loop
			
            return prefmd;
        } //end of Get FMD function
    
    public void cancel()
        {
            m_bCancel = true;
            if(null != m_capture) m_capture.cancel();
	}
   public Fmd getEnrollmentFmd()
   {
       return enroll_fmd;
   }
   
    public void run()
        {
            //acquire engine
            Engine engine = UareUGlobal.GetEngine();
			
            try{
                    m_bCancel = false;
                    while(!m_bCancel)
                    {
			//run enrollment
			Fmd fmd = engine.CreateEnrollmentFmd(Fmd.Format.DP_REG_FEATURES, this);
					
			//send result
			if(null != fmd)
                        {
                            //SendToListener(ACT_DONE, fmd, null, null, null);
                            enroll_fmd=fmd;
                            System.out.println(fmd.getData());                          
                            System.out.println("FMD Ready for Enrollment");
                            System.out.println("Size of the FMD: "+ fmd.getData().length);
                            m_bCancel=true;
			}
			else
                        {
                           // SendToListener(ACT_CANCELED, null, null, null, null);
                           
                            break;
			}
                    }//end of the while loop
		}
		catch(UareUException e){ 
                   // SendToListener(ACT_DONE, null, null, null, e);
                    e.printStackTrace();
		}
	}
}
