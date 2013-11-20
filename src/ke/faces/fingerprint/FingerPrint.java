/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ke.faces.fingerprint;

/**
 *
 * @author LENOVO USER
 */
public class FingerPrint {
    private int index; //position of the finger 1 -left thumb, 2 -, 3-middle finger, 4-ring finger 5-index finger
    private byte[] fmd; //finger print minutae data    

    public FingerPrint() {
    }

    public FingerPrint(int index, byte[] fmd) {
        this.index = index;
        this.fmd = fmd;
    }
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public byte[] getFmd() {
        return fmd;
    }

    public void setFmd(byte[] fmd) {
        this.fmd = fmd;
    }
    
    
    
}

