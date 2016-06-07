package com.populosa.clientfinder.louisiana.parish.neworleans.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Resource( name="newOrleansProperties")
@ComponentScan(basePackages = "com.populosa.clientfinder")
@PropertySource("file:/etc/myco/mywar.properties")
@SuppressWarnings("serial")
public class NewOrleansProperties implements Serializable
{
    private static Logger LOG                                                   = Logger.getLogger( NewOrleansProperties.class );   
    public static final String ORLEANSDC_REMOTE_ACCESS_FIRST_CITY               = "http://remoteaccess.orleanscdc.com/fcc-ra//login.asp";
    public static final String ORLEANSDC_NEW_SEARCH                             = "http://remoteaccess.orleanscdc.com/fcc-ra//search_page.asp";
    public static final String ATTORNEY_SETTINGS_PATH_WIN                       = "C:/";
    public static final String ATTORNEY_SETTINGS_PATH_MAC                       = "/Users/tomhunter/";
    
    public static final String SEARCH_TYPE_ALL_0                                = "0";
    public static final String DEFENDANT                                        = "Defendant";
    public static final String PLAINTIFF                                        = "Plaintiff";
    public static final String DETAILS_LINK_CAPTION                             = "Details";
    public static final String SEARCH_HYPHEN                                    = "-";
    
    private Properties properties;
    
    public NewOrleansProperties()
    {
        properties                      = new Properties();
        InputStream is                  = null;
        String pathToAttorneySettings   = "";
        String settings                 = "DEV/attorney_settings/settingsLouisiana.txt";

        if( NewOrleansProperties.isMacOs() )
        {
            pathToAttorneySettings = NewOrleansProperties.ATTORNEY_SETTINGS_PATH_MAC + settings;
        }
        else
        {
            pathToAttorneySettings = NewOrleansProperties.ATTORNEY_SETTINGS_PATH_WIN + settings;          
        }
        
        try
        {
            is = new FileInputStream( pathToAttorneySettings );
            properties.load(is);
        }
        catch( Exception e )
        {
            NewOrleansProperties.LOG.error( "NewOrleansProperties() threw an Exception e=" + e );
        }
        finally
        {
            try
            {
                is.close();
            }
            catch( Exception cl )
            {
                NewOrleansProperties.LOG.error( "NewOrleansProperties() threw an Exception while attempting to close resources, cl=" + cl );
            }
        }
    }
    
    public static boolean isMacOs()
    {
        String osName   = System.getProperty( "os.name" ).toLowerCase();
        boolean isMacOs = osName.startsWith( "mac os x" );
        
        return isMacOs;
    }
    
    
    public String getPropertyByKey( String key )
    {
        String value = this.properties.getProperty( key );
        
        return value;
    }
    
    @SuppressWarnings("unused")
    public static void main( String[] args )
    {
        NewOrleansProperties props = new NewOrleansProperties();
    }

    public Properties getProperties()
    {
        return properties;
    }
    
    /*
    
    private void populateAttorneySettings()
    {
        InputStream is                  = null;
        String pathToAttorneySettings   = "";
        String settings                 = "DEV/attorney_settings/settingsLouisiana.txt";

        if( ExcelGeneratorLouisiana.isMacOs() )
        {
            pathToAttorneySettings = SuperSlurperLouisiana.ATTORNEY_SETTINGS_PATH_MAC + settings;
        }
        else
        {
            pathToAttorneySettings = SuperSlurperLouisiana.ATTORNEY_SETTINGS_PATH_WIN + settings;          
        }

        try
        {
            is                         = new FileInputStream( pathToAttorneySettings );
            attorneySettings.load( is );
            
            searchStartDateMMDDYYYY     = attorneySettings.getProperty( SuperSlurperLouisiana.SEARCH_START_DATE_MMDDYYYY );
            searchEndDateMMDDYYYY       = attorneySettings.getProperty( SuperSlurperLouisiana.SEARCH_END_DATE_MMDDYYYY );
            myCaseInGovUsername         = attorneySettings.getProperty( SuperSlurperLouisiana.USERNAME );
            myCaseInGovPassword         = attorneySettings.getProperty( SuperSlurperLouisiana.PASSWORD ); 
            searchRegEvictions          = attorneySettings.getProperty( SuperSlurperLouisiana.SEARCH_REGULAR_EVICTIONS_KEY );
            searchHANO                  = attorneySettings.getProperty( SuperSlurperLouisiana.SEARCH_HANO_KEY );
            searchSmallClaims           = attorneySettings.getProperty( SuperSlurperLouisiana.SEARCH_SMALL_CLAIMS_KEY );
            searchFirstCity             = attorneySettings.getProperty( SuperSlurperLouisiana.SEARCH_FIRST_CITY_KEY );
            searchCityTax               = attorneySettings.getProperty( SuperSlurperLouisiana.SEARCH_CITY_TAX_KEY );
            searchStateTax              = attorneySettings.getProperty( SuperSlurperLouisiana.SEARCH_STATE_TAX_KEY );
            
            executeCajunSlurpFirstCity  = Boolean.parseBoolean( attorneySettings.getProperty( SuperSlurperLouisiana.FLAG_SEARCH_FIRST_CITY ) );
            executeCajunSlurpCivil      = Boolean.parseBoolean( attorneySettings.getProperty( SuperSlurperLouisiana.FLAG_SEARCH_CIVIL ) );
            executeCajunSlurpConvey     = Boolean.parseBoolean( attorneySettings.getProperty( SuperSlurperLouisiana.FLAG_SEARCH_CONVEY ) );
            executeCajunSlurpMortgage   = Boolean.parseBoolean( attorneySettings.getProperty( SuperSlurperLouisiana.FLAG_SEARCH_MORTGAGE ) );       
            String startOverride        = attorneySettings.getProperty( SuperSlurperLouisiana.START_PAGE_OVERRIDE );
            
            if( startOverride != null && startOverride.trim().length() > 0 )
            {
                try
                {
                    startPageOverride =  Integer.parseInt( startOverride.trim() );
                }
                catch( NumberFormatException nfe )
                {
                    System.out.println( "SuperSlurperLouisiana.populateAttorneySettings() threw a NumberFormatException, check 'startPageOverride',\n nfe=" + nfe );            
                }
            }
        }
        catch( Exception e )
        {
            System.out.println( "SuperSlurperLouisiana.populateAttorneySettings() threw an Exception, e=" + e );
        }
        finally
        {
            try
            {
                is.close();
            }
            catch( Exception e )
            {
            }
        }   
    }

    */
}
