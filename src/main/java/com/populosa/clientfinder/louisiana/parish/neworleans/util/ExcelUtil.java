package com.populosa.clientfinder.louisiana.parish.neworleans.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.populosa.clientfinder.louisiana.parish.neworleans.bean.Defendant;

public class ExcelUtil
{
    private DateFormat df                                       = new SimpleDateFormat( "yyyy_MM_dd" );
    private NewOrleansProperties newOrleansProperties;
    
    public static final String ATTORNEY_SETTINGS_PATH_WIN       = "C:/";
    public static final String ATTORNEY_SETTINGS_PATH_MAC       = "/Users/tomhunter/";
    
    private XSSFWorkbook wb;    
    private XSSFSheet sheet;
   
    public ExcelUtil( NewOrleansProperties newOrleansProperties, List<Defendant> defendants )
    {
        this.newOrleansProperties = newOrleansProperties;
        
        try  
        {
            this.wb                 = new XSSFWorkbook();
            this.sheet              = wb.createSheet( "All of these contain the keyword " + this.newOrleansProperties.getPropertyByKey( "keywordFragmentToSearch" ) );

            this.buildKeywordHeaderRow();
            
            int rowIndex                = 0;
            Iterator<Defendant> defsIt  = defendants.iterator();
            
            while( defsIt.hasNext() )
            {
                Defendant aDefendant    = defsIt.next();
                XSSFRow aRow            = this.sheet.createRow( rowIndex );
                rowIndex++;
                buildKeywordDetailRow( aRow, aDefendant );
            }
            
            writeResultsToFile();
        }
        catch( Exception e )
        {
            System.out.println( e );
        }
    }
    
    private void writeResultsToFile()
    {
        FileOutputStream fos    = null;
        String outputFile       = null;
        
        try
        {   
            outputFile   = getOutputFile();
            fos          = new FileOutputStream( outputFile, true );
            wb.write( fos );
            fos.flush();
            
            System.out.println( "\nExcelUtilsuccessfully completed its run. File: " + outputFile );
        }
        catch( Exception e )
        {
            System.out.println( e );
        }
        finally
        {
            try
            {
                fos.close();
            }
            catch( Exception ee )
            {
                System.out.println( "Unable to Close file=" + outputFile + ", ee=" + ee );
            }
        }        
    }

    
    private void buildKeywordHeaderRow()
    {
        XSSFRow headerRow       = sheet.createRow( (short)0 );

        XSSFCell headerCell0    = headerRow.createCell( (short)0 );
        headerCell0.setCellType( XSSFCell.CELL_TYPE_STRING );
        headerCell0.setCellValue( "Name" );

        XSSFCell headerCell1    = headerRow.createCell( (short)1 );
        headerCell1.setCellType( XSSFCell.CELL_TYPE_STRING );
        headerCell1.setCellValue( "Address1" );

        XSSFCell headerCell2    = headerRow.createCell( (short)2 );
        headerCell2.setCellType( XSSFCell.CELL_TYPE_STRING );
        headerCell2.setCellValue( "Address2" );

        XSSFCell headerCell3    = headerRow.createCell( (short)3 );
        headerCell3.setCellType( XSSFCell.CELL_TYPE_STRING );
        headerCell3.setCellValue( "City" );

        XSSFCell headerCell4    = headerRow.createCell( (short)4 );
        headerCell4.setCellType( XSSFCell.CELL_TYPE_STRING );
        headerCell4.setCellValue( "State" );

        XSSFCell headerCell5    = headerRow.createCell( (short)5 );
        headerCell5.setCellType( XSSFCell.CELL_TYPE_STRING );
        headerCell5.setCellValue( "Zip" );
        
        XSSFCell headerCell6    = headerRow.createCell( (short)6 );
        headerCell6.setCellType( XSSFCell.CELL_TYPE_STRING );
        headerCell6.setCellValue( "Case Number" );
    }   
    
    private void buildKeywordDetailRow( XSSFRow aRow, Defendant aDefendant )
    {
        XSSFDataFormat df           = wb.createDataFormat();
        
        XSSFCellStyle cellStyleDate = wb.createCellStyle();
        cellStyleDate.setDataFormat( df.getFormat("dd-MMM-yy") );
        
        XSSFCell aCell0             = aRow.createCell( (short)0 );
        aCell0.setCellValue( aDefendant.getName() );
        
        XSSFCell aCell1             = aRow.createCell( (short)1 );
        aCell1.setCellValue( aDefendant.getAddress1() );
        
        XSSFCell aCell2             = aRow.createCell( (short)2 );
        aCell2.setCellValue( aDefendant.getAddress2() );

        XSSFCell aCell3             = aRow.createCell( (short)3 );
        aCell3.setCellValue( aDefendant.getCity() );
        
        XSSFCell aCell4             = aRow.createCell( (short)4 );
        aCell4.setCellStyle( cellStyleDate );
        aCell4.setCellValue( aDefendant.getState() );

        XSSFCell aCell5             = aRow.createCell( (short)5 );
        aCell5.setCellStyle( cellStyleDate );
        aCell5.setCellValue( aDefendant.getZip() );

        XSSFCell aCell6             = aRow.createCell( (short)6 );
        aCell6.setCellStyle( cellStyleDate );
        aCell6.setCellValue( aDefendant.getCaseNumber() );
    }
        
        
    public String getOutputFile() throws IOException
    {
        Date today              = new Date();
        String date             = df.format( today );
        String filePath         = "";
        
        if( isMacOs() )
        {
            filePath = ExcelUtil.ATTORNEY_SETTINGS_PATH_MAC + "DEV/attorney_settings/KeywordSlurper_" + date.trim() + ".xlsx ";
        }
        else
        {
            filePath = ExcelUtil.ATTORNEY_SETTINGS_PATH_WIN + "DEV/attorney_settings/KeywordSlurper_" + date.trim() +  ".xlsx ";            
        }
        
        File file   = new File( filePath );
        
        if( !file.exists() ) 
        {
            file.createNewFile();
        } 
        
        return filePath;
    }

    
    public static boolean isMacOs()
    {
        String osName   = System.getProperty( "os.name" ).toLowerCase();
        boolean isMacOs = osName.startsWith( "mac os x" );
        
        return isMacOs;
    }

}
