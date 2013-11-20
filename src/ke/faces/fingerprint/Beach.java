/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ke.faces.fingerprint;

import java.util.logging.Logger;

/**
 *
 * @author LENOVO USER
 */
public class Beach {
    
    private int beachId;
    private String name;
    private String description;
    private String county;

    private static final Logger LOG = Logger.getLogger(Beach.class.getName());
    
     public Beach()
     {
         this.name=null;
         this.description=null;
         this.county=null;
     }
    public Beach(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Beach(String name, String description, String county) {
        this.name = name;
        this.description = description;
        this.county = county;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }
    
    
    
    public int getBeachId() {
        return beachId;
    }

    public void setBeachId(int beachId) {
        this.beachId = beachId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Beach{" + "name=" + name + ", description=" + description + '}';
    }
   
    
    
    
}
