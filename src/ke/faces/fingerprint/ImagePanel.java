/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ke.faces.fingerprint;

/**
 *
 * @author LENOVO USER
 */
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import com.digitalpersona.uareu.*;
import com.digitalpersona.uareu.Fid.Fiv;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImagePanel 
	extends JPanel
{
	private static final long serialVersionUID = 5;
	private BufferedImage m_image;
	
	public void showImage(Fid image){
		Fiv view = image.getViews()[0];
		m_image = new BufferedImage(view.getWidth(), view.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		m_image.getRaster().setDataElements(0, 0, view.getWidth(), view.getHeight(), view.getImageData());
		repaint();
	} 
	
	public void paint(Graphics g) {
		g.drawImage(m_image, 0, 0, null);
	}
        
        public void loadImage()
        {
            try{
                m_image=ImageIO.read(new File("images/fingerprintimageLarge.jpg"));
            }
            catch(IOException ex)
            {
                ex.printStackTrace();
            }
        }
        

}

