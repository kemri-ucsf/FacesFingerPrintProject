/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ke.faces.fingerprint;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author LENOVO USER
 */
public class Reporting {
    private static Workbook workbook;
    
    
    public Reporting()            
    {
        try
        {
            FileInputStream file = new FileInputStream(new File("reportTest.xls"));
            workbook = new HSSFWorkbook(file);
            
            Workbook wb = new HSSFWorkbook();
            FileOutputStream fileOut = new FileOutputStream("Testbook.xls");
            wb.write(fileOut);
            fileOut.close();
        }
         catch (IOException ex) {
              ex.printStackTrace();
            Logger.getLogger(Reporting.class.getName()).log(Level.SEVERE, null, ex);
        }
               
    }
    
    public void generateReport() throws IOException
    {
        System.out.println("Test Report Gen");
        Sheet sheet = workbook.getSheet("sheet1");
        
        try{
            String strSql="Select * from participant;";
            ResultSet rs=Sql.executeQuery(strSql);
            ResultSetMetaData rsMeta = rs.getMetaData();
            
            Row row = sheet.createRow(0);
             Cell cell;
            //get table columns which will act as title columns
            for (int i = 1; i <= rsMeta.getColumnCount(); i++) 
            {
                String colName = rsMeta.getColumnName(i);
                int dataType = rsMeta.getColumnType(i);
                cell = row.createCell(i-1);
                cell.setCellValue(colName);
            }
            
            int i=1;
            while (rs.next())
            {
                //create row to write data to
                row = sheet.createRow(i);
                 //create cells for writing data to
                for(int c=1;c <= rsMeta.getColumnCount(); c++)
                {
                    cell = row.createCell(c-1);
                    String colName = rsMeta.getColumnName(c);
                    cell.setCellValue(rs.getString(colName));
                }
               
                i++;
            }
            
            JOptionPane.showMessageDialog(null, "Excel Report of All Participants Generated");
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
        }
        
      
        

        
       /// Write the output to a file
        FileOutputStream fileOut = new FileOutputStream("reportTest.xls");
        workbook.write(fileOut);
        fileOut.close();
    }
            
    
}
