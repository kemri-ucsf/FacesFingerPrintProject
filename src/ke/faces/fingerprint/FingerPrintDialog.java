/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ke.faces.fingerprint;

import com.digitalpersona.uareu.Fid;
import com.digitalpersona.uareu.Fmd;
import com.digitalpersona.uareu.Reader;
import com.digitalpersona.uareu.UareUException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 *
 * @author LENOVO USER
 */
public class FingerPrintDialog extends JPanel implements ActionListener{
    
    private static final String ACT_BACK = "back";
    private static final String ACT_IDENTIFY = "identify";
    private static final String ACT_ENROLL = "enrollment";
    private static final String ACT_RIGHTINDEX = "right_index_finger";
    private static final String ACT_RIGHTRING = "right_ring_finger";
    private static final String ACT_RIGHTMIDDLE = "right_finger_finger";
    private static final String ACT_RIGHTTHUMB = "right_thumb_finger";
    private static final String ACT_RIGHTLITTLE = "right_little_finger";
    
    private static final String ACT_LEFTINDEX = "left_index_finger";
    private static final String ACT_LEFTRING = "left_ring_finger";
    private static final String ACT_LEFTMIDDLE = "left_finger_finger";
    private static final String ACT_LEFTTHUMB = "left_thumb_finger";
    private static final String ACT_LEFTLITTLE = "left_little_finger";
    
    private static final int LEFTTHUMB = 1;
    private static final int LEFTINDEX = 2;
    private static final int LEFTMIDDLE = 3;
    private static final int LEFTRING = 4;
    private static final int LEFTLITTLE = 5;
    
    private static final int RIGHTLITTLE = 6;
    private static final int RIGHTRING = 7;
    private static final int RIGHTMIDDLE = 8;
    private static final int RIGHTINDEX = 9;
    private static final int RIGHTTHUMB = 10;
    
    public FingerPrint selectedPrint;
    public FingerPrint capturedPrints[];
    //selected finger
    public static int finger;
    private Identification identify;
    
    private CaptureThread.CaptureEvent evt1;  
    private ImagePanel imagePanel;
    private JDialog       dlgFingerPrintDialog;
    private CaptureThread m_capture;
    private Reader        reader;
    private boolean       bStreaming;
    
    private JLabel lbl_Title;
    private JLabel lbl_Quality;
    private JLabel lbl_Bad;
    private JLabel lbl_Prompt;
    
    JButton btnEnroll;
    JButton btnIdentify;
    JButton btnBack;
    
    //Images
    private ImageIcon imageGood=new ImageIcon("images\\CheckMark.png");
    private ImageIcon imageBad=new ImageIcon("images\\XMark.png");    
    
    //option buttons
    JRadioButton rdLeftThumb;
    JRadioButton rdLeftIndex;
    JRadioButton rdLeftMiddle;
    JRadioButton rdLeftRing;
    JRadioButton rdLeftLittle;
    
    JRadioButton rdRightThumb;
    JRadioButton rdRightIndex;
    JRadioButton rdRightMiddle;
    JRadioButton rdRightRing;
    JRadioButton rdRightLittle;
    
    
    FingerPrintDialog(Reader reader)
    {
        //store an array of captured prints
        //load all the exi
        capturedPrints=new FingerPrint[10];
        identify=new Identification();
        this.reader = reader;
	bStreaming = false;
        finger=1;
        m_capture = new CaptureThread(reader, bStreaming, Fid.Format.ANSI_381_2004, Reader.ImageProcessing.IMG_PROC_DEFAULT);
        
        dlgFingerPrintDialog= new JDialog((JDialog)null, "PARTICIPANT REGISTRATION", true);
        dlgFingerPrintDialog.setLayout(null);
        dlgFingerPrintDialog.setBounds(200, 0,600, 550);
        
        //image panel display
        imagePanel = new ImagePanel();        
        imagePanel.setBounds(10,50,300,350);//some random value that I know is in my dialog
        imagePanel.setBorder(BorderFactory.createTitledBorder(""));
        dlgFingerPrintDialog.add(imagePanel);
        
        //display image quality
        lbl_Quality = new JLabel();
        lbl_Quality.setBounds(450,360,120,120);//some random value that I know is in my dialog
        lbl_Quality.setHorizontalAlignment(JLabel.CENTER);
        lbl_Quality.setVerticalAlignment(JLabel.CENTER);
        lbl_Quality.setVisible(false);
        lbl_Quality.setIcon(imageGood);
        dlgFingerPrintDialog.add(lbl_Quality);
        
        lbl_Bad = new JLabel();
        lbl_Bad.setBounds(450,360,120,120);//some random value that I know is in my dialog
        lbl_Bad.setHorizontalAlignment(JLabel.CENTER);
        lbl_Bad.setVerticalAlignment(JLabel.CENTER);
        lbl_Bad.setVisible(false);
        lbl_Bad.setIcon(imageBad);
        dlgFingerPrintDialog.add(lbl_Bad);
        
        lbl_Prompt = new JLabel ("Put your Left Thumb finger on the reader");
        lbl_Prompt.setBounds(10,5,300,60);//some random value that I know is in my dialog
        dlgFingerPrintDialog.add(lbl_Prompt);
        
        lbl_Title = new JLabel ("Select the Finger to be Read/Scan");
        lbl_Title.setBounds(350,5,200,20);//some random value that I know is in my dialog
        dlgFingerPrintDialog.add(lbl_Title);
        
        btnIdentify = new JButton("Identify");
        btnIdentify.setBounds(10,420,100,40);
	btnIdentify.setActionCommand(ACT_IDENTIFY);
	btnIdentify.addActionListener(this);
        btnIdentify.setEnabled(false);
        dlgFingerPrintDialog.add(btnIdentify);
         
        btnEnroll = new JButton("Enroll");
        btnEnroll.setBounds(150,420,100,40);
	btnEnroll.setActionCommand(ACT_ENROLL);
	btnEnroll.addActionListener(this);
        btnEnroll.setEnabled(false);
        dlgFingerPrintDialog.add(btnEnroll);
        
        btnBack = new JButton("Back");
        btnBack.setBounds(300,420,120,40);
	btnBack.setActionCommand(ACT_BACK);
	btnBack.addActionListener(this);
        dlgFingerPrintDialog.add(btnBack);
        
        rdLeftThumb = new JRadioButton("Left Thumb Finger");
        rdLeftThumb.setBounds(350,40,200,40);
	rdLeftThumb.setActionCommand(ACT_LEFTTHUMB);
	rdLeftThumb.addActionListener(this);
        rdLeftThumb.setSelected(true);
        dlgFingerPrintDialog.add(rdLeftThumb);
        
        rdLeftIndex = new JRadioButton("Left Index Finger");
        rdLeftIndex.setBounds(350,70,200,40);
	rdLeftIndex.setActionCommand(ACT_LEFTINDEX);
	rdLeftIndex.addActionListener(this);
        dlgFingerPrintDialog.add(rdLeftIndex);
        
        rdLeftMiddle = new JRadioButton("Left Middle Finger");
        rdLeftMiddle.setBounds(350,100,200,40);
	rdLeftMiddle.setActionCommand(ACT_LEFTMIDDLE);
	rdLeftMiddle.addActionListener(this);
        dlgFingerPrintDialog.add(rdLeftMiddle);
        
        rdLeftRing = new JRadioButton("Left Ring Finger");
        rdLeftRing.setBounds(350,130,200,40);
	rdLeftRing.setActionCommand(ACT_LEFTRING);
	rdLeftRing.addActionListener(this);
        dlgFingerPrintDialog.add(rdLeftRing);
        
        rdLeftLittle = new JRadioButton("Left Little Finger");
        rdLeftLittle.setBounds(350,160,200,40);
	rdLeftLittle.setActionCommand(ACT_LEFTLITTLE);
	rdLeftLittle.addActionListener(this);
        dlgFingerPrintDialog.add(rdLeftLittle);
        
        rdRightLittle = new JRadioButton("Right Little Finger");
        rdRightLittle.setBounds(350,190,200,40);
	rdRightLittle.setActionCommand(ACT_LEFTLITTLE);
	rdRightLittle.addActionListener(this);
        dlgFingerPrintDialog.add(rdRightLittle);
        
        rdRightRing = new JRadioButton("Right Ring Finger");
        rdRightRing.setBounds(350,220,200,40);
	rdRightRing.setActionCommand(ACT_RIGHTRING);
	rdRightRing.addActionListener(this);
        dlgFingerPrintDialog.add(rdRightRing);
        
        rdRightMiddle = new JRadioButton("Right Middle Finger");
        rdRightMiddle.setBounds(350,250,200,40);
	rdRightMiddle.setActionCommand(ACT_RIGHTMIDDLE);
	rdRightMiddle.addActionListener(this);
        dlgFingerPrintDialog.add(rdRightMiddle);
        
        rdRightIndex = new JRadioButton("Right Index Finger");
        rdRightIndex.setBounds(350,280,200,40);
	rdRightIndex.setActionCommand(ACT_RIGHTINDEX);
	rdRightIndex.addActionListener(this);
        dlgFingerPrintDialog.add(rdRightIndex);
        
        rdRightThumb = new JRadioButton("Right Thumb Finger");
        rdRightThumb.setBounds(350,310,200,40);
	rdRightThumb.setActionCommand(ACT_RIGHTTHUMB);
	rdRightThumb.addActionListener(this);
        dlgFingerPrintDialog.add(rdRightThumb);
                
         //Group the radio buttons.
        ButtonGroup group = new ButtonGroup();
        group.add(rdLeftRing);
        group.add(rdLeftIndex);
        group.add(rdLeftMiddle);
        group.add(rdLeftLittle);
        group.add(rdLeftThumb);
        
        group.add(rdRightRing);
        group.add(rdRightIndex);
        group.add(rdRightMiddle);
        group.add(rdRightLittle);
        group.add(rdRightThumb);
        
        validate();
    }
    
    public void actionPerformed(ActionEvent e){
        if(e.getActionCommand().equals(ACT_BACK))
        {
            //close this windo
            //StopCaptureThread();
            dlgFingerPrintDialog.setVisible(false);
            //dlgFingerPrintDialog.dispose();
            try
            {
                reader.Close();
            }
            catch(UareUException ex){ MessageBox.DpError("Reader.Close()", ex); }
            
            return;
	}
        else if(e.getActionCommand().equals(ACT_ENROLL))
        {
            //Call method to create enrollment image
            Enrollment enrollment = new Enrollment(evt1);
            
            //check if this participant is already enrolled
            identify.loadFingerPrints();
            identify.ProcessCaptureResult(evt1);
            if (identify.getFoundStatus()==true)
            {
                Registration.btnSave.setEnabled(false);
                Registration.btnCancel.setEnabled(true);
               //close this windo
               StopCaptureThread();
               dlgFingerPrintDialog.setVisible(false);
               
               //close reader
               try
                {
                reader.Close();
                }
                catch(UareUException ex){ MessageBox.DpError("Reader.Close()", ex); }
              
            }
            else
            {
                if(Registration.oldParticipant==null)
                {
                     //clear fields
                    Registration.txt_Identifier.setText("");
                    Registration.txt_Age.setText("");
                    Registration.txt_Fname.setText("");
                    Registration.txt_Gname.setText("");
                    Registration.txt_Mname.setText("");
                    Registration.txt_Nname.setText("");
                    Registration.btnSave.setEnabled(true);
                    Registration.btnCancel.setEnabled(false);                    
                }
               
                 
            }
            
            //Test fmd
            enrollment.GetFmd(Fmd.Format.DP_PRE_REG_FEATURES);
            enrollment.run();
            Fmd fmd=enrollment.getEnrollmentFmd();
           // System.out.println("Fmd Enrolled "+fmd.getData());
            if (fmd!=null)
            {
                selectedPrint=new FingerPrint();
                selectedPrint.setIndex(finger);
                selectedPrint.setFmd(fmd.getData());
                Registration.participant.lstFingerPrints.add(selectedPrint);
                 
                //if (finger==1)
                //{
                 //   selectedPrint.setIndex(finger);                    
                  //  Registration.participant.setFingerPrint(selectedPrint);
               // }
                //else if(finger !=1)
                //{
                 //   selectedPrint.setIndex(finger);                    
                  //  Registration.participant.setFingerPrint2(selectedPrint);  
                //}
               
                System.out.println("Fmd Enrolled Size "+ selectedPrint.getFmd());
                System.out.println("Select Finger "+ selectedPrint.getIndex());              
                         
                
               JOptionPane.showMessageDialog(null, "Finger Sucessfully Enrolled");
               
            }
            //close reader
             //  try
             //   {
              //  reader.Close();
                //}
                //catch(UareUException ex){ MessageBox.DpError("Reader.Close()", ex); }
	}
        else if(e.getActionCommand().equals(ACT_IDENTIFY))
        {
            
            //Call method to identify participant
            
            ///check if this participant is already enrolled
           // identify.loadFingerPrints();
            identify.ProcessCaptureResult(evt1);
            if (identify.getFoundStatus()==true)
            {
                Registration.btnSave.setEnabled(false);
               //close this windo
               ///StopCaptureThread();
               dlgFingerPrintDialog.setVisible(false);
               
               //close reader
               try
                {
                reader.Close();
                }
                catch(UareUException ex){ MessageBox.DpError("Reader.Close()", ex); }
              
            }
            else
            {
                JOptionPane.showMessageDialog(null, "Finger Not Found....");
            }
	}
        else if(e.getActionCommand().equals(ACT_LEFTTHUMB))
        {            
            finger=1;
            lbl_Prompt.setText("Put your Left Thumb finger on the reader");
            System.out.println("Selected Finger: "+ finger);
            //hide quality check image display
            lbl_Bad.setVisible(false);
            lbl_Quality.setVisible(false);
            btnIdentify.setEnabled(false);
            btnEnroll.setEnabled(false);
	}
        else if(e.getActionCommand().equals(ACT_LEFTINDEX))
        {           
            finger=2;
            lbl_Prompt.setText("Put your Left Index finger on the reader");
             System.out.println("Selected Finger: "+ finger);
            //hide quality check image display
            lbl_Bad.setVisible(false);
            lbl_Quality.setVisible(false);
            btnIdentify.setEnabled(false);
            btnEnroll.setEnabled(false);
	}
        else if(e.getActionCommand().equals(ACT_LEFTMIDDLE))
        {           
            finger=3;
            lbl_Prompt.setText("Put your Left Middle finger on the reader");
             System.out.println("Selected Finger: "+ finger);
            //hide quality check image display
            lbl_Bad.setVisible(false);
            lbl_Quality.setVisible(false);
            btnIdentify.setEnabled(false);
            btnEnroll.setEnabled(false);
	}
        else if(e.getActionCommand().equals(ACT_LEFTRING))
        {           
            finger=4;
            lbl_Prompt.setText("Put your Left Ring finger on the reader");
             System.out.println("Selected Finger: "+ finger);
            //hide quality check image display
            lbl_Bad.setVisible(false);
            lbl_Quality.setVisible(false);
            btnIdentify.setEnabled(false);
            btnEnroll.setEnabled(false);
	}
        else if(e.getActionCommand().equals(ACT_LEFTLITTLE))
        {           
            finger=5;
            lbl_Prompt.setText("Put your Left Little finger on the reader");
             System.out.println("Selected Finger: "+ finger);
            //hide quality check image display
            lbl_Bad.setVisible(false);
            lbl_Quality.setVisible(false);
            btnIdentify.setEnabled(false);
            btnEnroll.setEnabled(false);
	}
        else if(e.getActionCommand().equals(ACT_RIGHTLITTLE))
        {           
            finger=6;
            lbl_Prompt.setText("Put your Right Little finger on the reader");
             System.out.println("Selected Finger: "+ finger);
            //hide quality check image display
            lbl_Bad.setVisible(false);
            lbl_Quality.setVisible(false);
            btnIdentify.setEnabled(false);
            btnEnroll.setEnabled(false);
	}
        else if(e.getActionCommand().equals(ACT_RIGHTRING))
        {           
            finger=7;
            lbl_Prompt.setText("Put your Right Ring finger on the reader");
             System.out.println("Selected Finger: "+ finger);
            //hide quality check image display
            lbl_Bad.setVisible(false);
            lbl_Quality.setVisible(false);
            btnIdentify.setEnabled(false);
            btnEnroll.setEnabled(false);
	}
        else if(e.getActionCommand().equals(ACT_RIGHTMIDDLE))
        {           
            finger=8;
            lbl_Prompt.setText("Put your Right Middle finger on the reader");
             System.out.println("Selected Finger: "+ finger);
            //hide quality check image display
            lbl_Bad.setVisible(false);
            lbl_Quality.setVisible(false);
            btnIdentify.setEnabled(false);
            btnEnroll.setEnabled(false);
	}
        else if(e.getActionCommand().equals(ACT_RIGHTINDEX))
        {           
            finger=9;
            lbl_Prompt.setText("Put your Right Index finger on the reader");
             System.out.println("Selected Finger: "+ finger);
            //hide quality check image display
            lbl_Bad.setVisible(false);
            lbl_Quality.setVisible(false);
            btnIdentify.setEnabled(false);
            btnEnroll.setEnabled(false);
	}
        else if(e.getActionCommand().equals(ACT_RIGHTTHUMB))
        {           
            finger=10;
            lbl_Prompt.setText("Put your Right Thumb finger on the reader");
             System.out.println("Selected Finger: "+ finger);
            lbl_Quality.setVisible(false);
            lbl_Bad.setVisible(false);
            btnIdentify.setEnabled(false);
            btnEnroll.setEnabled(false);
	}
        else
        {
            CaptureThread.CaptureEvent evt = (CaptureThread.CaptureEvent)e;
            evt1=evt;
            boolean bCanceled = false;
            //imagePanel.showImage(evt.capture_result.image);
            if(e.getActionCommand().equals(CaptureThread.ACT_CAPTURE))
            {
                if(null != evt.capture_result)
                {
                    
                    if(null !=evt.capture_result.image && Reader.CaptureQuality.FINGER_TOO_HIGH == evt.capture_result.quality)
                    {
                        lbl_Quality.setVisible(false);
                        lbl_Bad.setVisible(true);
                        imagePanel.showImage(evt.capture_result.image);
                        return;
                        
                    }
                    else if(null != evt.capture_result.image && Reader.CaptureQuality.GOOD == evt.capture_result.quality)
                    {
                        //enable button
                        btnIdentify.setEnabled(true);
                        btnEnroll.setEnabled(true);
                        //display image
                        imagePanel.showImage(evt.capture_result.image);
                        lbl_Bad.setVisible(false);
                        lbl_Quality.setVisible(true);
                        System.out.println("Good Quality");
                        
                        
                    }
                    else if(Reader.CaptureQuality.CANCELED == evt.capture_result.quality)
                    {
                        //capture or streaming was canceled, just quit
			bCanceled = true;
                        btnIdentify.setEnabled(false);
                        btnEnroll.setEnabled(false);
                    }
                    else
                    {
                        //bad quality
			MessageBox.BadQuality(evt.capture_result.quality);
                        lbl_Bad.setVisible(true);
                        lbl_Quality.setVisible(false);
                    }				
		
                }
                else if(null != evt.exception)
                {
                    //exception during capture
                    MessageBox.DpError("Capture",  evt.exception);
                    bCanceled = true;
                }
                else if(null != evt.reader_status)
                {
                    MessageBox.BadStatus(evt.reader_status);
                    bCanceled = true;
                }
                if(!bCanceled)
                {
                    if(!bStreaming)
                    {
                        //restart capture thread
			WaitForCaptureThread();
			StartCaptureThread();
                    }
                }
                else
                {
                    //destroy dialog
                    dlgFingerPrintDialog.setVisible(false);
		}
                   
            }
            
            
        }
    }
    
    private void StartCaptureThread()
    {
        m_capture = new CaptureThread(reader, bStreaming, Fid.Format.ANSI_381_2004, Reader.ImageProcessing.IMG_PROC_DEFAULT);
	m_capture.start(this);
    }

    private void StopCaptureThread()
    {
        if(null != m_capture) m_capture.cancel();
    }
	
    private void WaitForCaptureThread()
    {
	if(null != m_capture) m_capture.join(1000);
    }
    
    public void startReader()
    {
        try{
            
            reader.Open(Reader.Priority.COOPERATIVE);
	}
	catch(UareUException e){ MessageBox.DpError("Reader.Open()", e); }
		
	//m_enrollment.start();
        boolean bOk = true;
	if(bStreaming)
        {
            //check if streaming supported
            Reader.Capabilities rc = reader.GetCapabilities();
            if(!rc.can_stream)
            {
                MessageBox.Warning("This reader does not support streaming");
		bOk = false;
            }
	}
        
        if(bOk)
        {
            //start capture thread
            StartCaptureThread();
            dlgFingerPrintDialog.setVisible(true);
            dlgFingerPrintDialog.dispose();
			
            //cancel capture
            StopCaptureThread();
			
            //wait for capture thread to finish
            WaitForCaptureThread();
	}
                        
	
    }
    
    public static void Run(Reader reader)
    {
    	FingerPrintDialog fingerprintDialog = new FingerPrintDialog(reader);
    	fingerprintDialog.startReader();
    }
}
