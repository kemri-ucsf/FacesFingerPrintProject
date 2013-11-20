/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ke.faces.fingerprint;



/**
 *
 * @author LENOVO USER
 */
public class FacesFingerPrintProject {

    /**
     * @param args the command line arguments
     */
    
    
    public static void main(String[] args) {
        // TODO code application logic here
        
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {                
               MainMenu.createAndShowGUI();
            }
    });
                
                
    }
    
    
    
    
    
    
    
}
