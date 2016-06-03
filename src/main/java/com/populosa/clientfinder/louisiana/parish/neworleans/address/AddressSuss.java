package com.populosa.clientfinder.louisiana.parish.neworleans.address;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import com.populosa.clientfinder.louisiana.parish.neworleans.bean.Defendant;

public class AddressSuss
{
    private static Logger LOG                                       = Logger.getLogger( AddressSuss.class );
    
    public static final String ADDRESS_SEARCH_URL_ANYWHO            = "http://www.anywho.com/whitepages";
    public static final String ADDRESS_ANYWHO_BYNAME_FIRST_ID       = "by_name_first";
    public static final String ADDRESS_ANYWHO_BYNAME_LAST_ID        = "by_name_last";
    public static final String ADDRESS_ANYWHO_BYNAME_CITY_ID        = "by_name_city_name";
    public static final String ADDRESS_ANYWHO_BYNAME_STATE_ID       = "fap_terms_state_name";
    public static final String ADDRESS_ANYWHO_BYNAME_SUBMIT_ID      = "name-submit";
    
    
    public static final String ADDRESS_SEARCH_URL_SMARTYSTREETS     = "https://smartystreets.com/";
    
    
    public void findDefendantAddress( Defendant defendantWithIncompleteAddress )
    {
        String wholeName    = defendantWithIncompleteAddress.getName();
        String address1     = defendantWithIncompleteAddress.getAddress1();
        String city         = defendantWithIncompleteAddress.getCity();
        String state        = defendantWithIncompleteAddress.getState();
        
        if( wholeName == null )
        {
            AddressSuss.LOG.info( "AddressSuss.findDefendantAddress(): No wholeName, so will NOT attempt defendantWithIncompleteAddress=" + defendantWithIncompleteAddress );
            
            return;
        }
        else if( 
                    wholeName != null && wholeName.trim().length() > 0 
                    && 
                    address1 != null && address1.trim().length() > 0 
                    && 
                    city != null && city.trim().length() > 0 
                    && 
                    state != null && state.trim().length() > 0 
               )
        {
            AddressSuss.LOG.info( "AddressSuss.findDefendantAddress() will not attempt to improve on defendantWithIncompleteAddress=" + defendantWithIncompleteAddress );
            
            return;
        }
        
        String[] firstAndLast = findFirstAndLastNames( wholeName );
        
        try
        {
            System.setProperty( "webdriver.chrome.driver", "/Users/tomhunter/DEV/workspaceGarnishSlurper/chromedriver" );
            
            WebDriver driver                    = new ChromeDriver();
            driver.navigate().to( AddressSuss.ADDRESS_SEARCH_URL_ANYWHO );
            
            WebElement firstNameElement         = driver.findElement( By.id( AddressSuss.ADDRESS_ANYWHO_BYNAME_FIRST_ID ) );
            WebElement lastNameElement          = driver.findElement( By.id( AddressSuss.ADDRESS_ANYWHO_BYNAME_LAST_ID ) );
            WebElement cityElement              = driver.findElement( By.id( AddressSuss.ADDRESS_ANYWHO_BYNAME_CITY_ID ) );
            WebElement stateElement             = driver.findElement( By.id( AddressSuss.ADDRESS_ANYWHO_BYNAME_STATE_ID ) );
            WebElement nameSubmitElement        = driver.findElement( By.id( AddressSuss.ADDRESS_ANYWHO_BYNAME_SUBMIT_ID ) );
            
            firstNameElement.sendKeys( firstAndLast[ 0 ] );
            lastNameElement.sendKeys( firstAndLast[ 1 ] );
            cityElement.sendKeys( city );
            stateElement.sendKeys( state );
            
            nameSubmitElement.click();

            System.out.println( "wait" );
        }
        catch( Exception e )
        {
            AddressSuss.LOG.error( "AddressSuss.findDefendantAddress() threw an Exception, e=" + e );
        }
    }
    
    private String[] findFirstAndLastNames( String wholeName )
    {
        String[] names = wholeName.split( " " );
        
        if( names.length == 1 || names.length == 2 )
        {
            return names;
        }
        else if( names.length > 2 )
        {
            return names;
        }
        else if( names.length == 3 )
        {
            String middle = names[ 1 ];
            
            if( 
                    middle != null 
                    && 
                    (
                            middle.trim().equalsIgnoreCase( "von" )
                            ||
                            middle.trim().equalsIgnoreCase( "der" )
                    )
              )
            {
                String last = middle + " " + names[ 2 ];
                names[ 2 ]  = last;
            }
            
            System.out.println( "middle=" + middle );
        }
        
        return names;
    }
    
}
