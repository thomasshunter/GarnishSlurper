package com.populosa.clientfinder.louisiana.parish.neworleans.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFPicture;
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
            this.wb     = new XSSFWorkbook();
            this.sheet  = wb.createSheet( "Contains keyword " + this.newOrleansProperties.getPropertyByKey( "keywordFragmentToSearch" ) );
            this.sheet.setColumnWidth( 1, 300 );
            this.sheet.setColumnWidth( 2, 300 );
            this.sheet.setColumnWidth( 3, 300 );
            this.sheet.setColumnWidth( 4, 300 );
            this.sheet.setColumnWidth( 5, 300 );
            this.sheet.setColumnWidth( 6, 300 );
            this.sheet.setColumnWidth( 7, 500 );
            this.sheet.setColumnWidth( 8, 300 );
            this.sheet.setColumnWidth( 9, 300 );
            
            this.buildKeywordHeaderRow();
            
            int rowIndex                = 1; // Skip the header row
            Iterator<Defendant> defsIt  = defendants.iterator();
            
            while( defsIt.hasNext() )
            {
                Defendant aDefendant    = defsIt.next();
                XSSFRow aRow            = this.sheet.createRow( rowIndex );
                buildKeywordDetailRow( rowIndex, aRow, aDefendant );                
                rowIndex++;
            }
            
            writeResultsToFile();
        }
        catch( Exception e )
        {
            System.out.println( e );
        }
    }
    
    
    private void addScreencapToRow( int rowIndex, byte[] screencap, XSSFCell aCell8 )
    {
        try
        {
            /* Add Picture to workbook and get a index for the picture */
            int pictureId       = this.wb.addPicture( screencap, Workbook.PICTURE_TYPE_JPEG );
            XSSFDrawing drawing = this.sheet.createDrawingPatriarch();
            
            /* Create an anchor point */
            ClientAnchor anchor = new XSSFClientAnchor();
            anchor.setAnchorType( ClientAnchor.MOVE_AND_RESIZE );

            /* Define top left corner, and we can resize picture suitable from there */
            anchor.setCol1(8);
            anchor.setCol2(9);
            anchor.setRow1(rowIndex);
            anchor.setRow2(rowIndex + 1);
                    
            /* Invoke createPicture and pass the anchor point and ID */
            XSSFPicture  picture = drawing.createPicture(anchor, pictureId);
            
            /* Call resize method, which resizes the image */
            double scale = 0.3;
            picture.resize( scale );
        }
        catch( Exception e )
        {
            System.out.println( "e=" + e );
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
        
        XSSFCell headerCell7     = headerRow.createCell( (short)7 );
        headerCell7.setCellType( XSSFCell.CELL_TYPE_STRING );
        headerCell7.setCellValue( "Alternate Addresses" );

        XSSFCell headerCell8     = headerRow.createCell( (short)8 );
        headerCell8.setCellType( XSSFCell.CELL_TYPE_STRING );
        headerCell8.setCellValue( "Results of Web Search for this Address" );
        
        XSSFCell headerCell9    = headerRow.createCell( (short)9 );
        headerCell9.setCellType( XSSFCell.CELL_TYPE_STRING );
        headerCell9.setCellValue( "" );
    }   
    
    private void buildKeywordDetailRow( int rowIndex, XSSFRow aRow, Defendant aDefendant )
    {
        aRow.setHeightInPoints( 200 );
        
        XSSFCell aCell0             = aRow.createCell( (short)0 );
        aCell0.setCellValue( aDefendant.getName() );
        
        XSSFCell aCell1             = aRow.createCell( (short)1 );
        aCell1.setCellValue( aDefendant.getAddress1() );
        
        XSSFCell aCell2             = aRow.createCell( (short)2 );
        aCell2.setCellValue( aDefendant.getAddress2() );

        XSSFCell aCell3             = aRow.createCell( (short)3 );
        aCell3.setCellValue( aDefendant.getCity() );
        
        XSSFCell aCell4             = aRow.createCell( (short)4 );
        aCell4.setCellValue( aDefendant.getState() );

        XSSFCell aCell5             = aRow.createCell( (short)5 );
        aCell5.setCellValue( aDefendant.getZip() );

        XSSFCell aCell6             = aRow.createCell( (short)6 );
        aCell6.setCellValue( aDefendant.getCaseNumber() );
        
        XSSFCell aCell7             = aRow.createCell( (short)7 );
        List<String> webAddresses   = aDefendant.getNameAddressFromWebSearch();
        
        if( webAddresses.size() > 0 )
        {
            String combinedWebAddresses = this.combineAddresses( webAddresses );
            aCell7.setCellValue( combinedWebAddresses );
        }
        else
        {
            aCell7.setCellValue( "" );
        }
            
        @SuppressWarnings("unused")
        XSSFCell aCell8             = aRow.createCell( (short)8 ); // Created to hold screencap image   
        byte[] screencap            = aDefendant.getScreepcap();
        if( screencap != null )
        {
            addScreencapToRow( rowIndex, screencap, aCell8 );
        }
        
        XSSFCell aCell9             = aRow.createCell( (short)9 );
    }
        
    
    public static void hyperlinkScreenshot( XSSFCell cell, String fileAddress )
    {
        XSSFWorkbook wb                 = cell.getRow().getSheet().getWorkbook();
        CreationHelper createHelper     = wb.getCreationHelper();
        CellStyle hlink_style           = wb.createCellStyle();
        
        Font hlink_font                 = wb.createFont();
        hlink_font.setUnderline(Font.U_SINGLE);
        hlink_font.setColor(IndexedColors.BLUE.getIndex());
        hlink_style.setFont(hlink_font);
        
        Hyperlink hp                    = createHelper.createHyperlink(Hyperlink.LINK_FILE);
        
        
        
        fileAddress                     = fileAddress.replace("\\", "/");
        hp.setAddress(fileAddress);
        cell.setHyperlink(hp);
        cell.setCellStyle(hlink_style);
    }
    
    
    private String combineAddresses( List<String> webAddresses )
    {
        StringBuilder addresses         = new StringBuilder();
        Iterator<String> webAddressesIt = webAddresses.iterator();
        boolean needNewline             = false;
        
        while( webAddressesIt.hasNext() )
        {
            String aWebAddress  = webAddressesIt.next();
            addresses.append( aWebAddress );
            
            if( needNewline )
            {
                addresses.append( "\n" );
                needNewline = false;
            }
            
            needNewline = true;
        }
        
        return addresses.toString();
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
