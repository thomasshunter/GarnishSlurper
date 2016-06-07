package com.populosa.clientfinder.louisiana.parish.neworleans.address;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
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
        String wholeName                = defendantWithIncompleteAddress.getName();
        String address1                 = defendantWithIncompleteAddress.getAddress1();
        String city                     = defendantWithIncompleteAddress.getCity();
        String state                    = (defendantWithIncompleteAddress.getState().trim().length() > 1) ? defendantWithIncompleteAddress.getState().trim() : "LA"; 
        Aliases thisDefendantsAliases   = new Aliases( wholeName );
        
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
        
        String[] aliases        = thisDefendantsAliases.getAliases();
        ChromeDriver driver     = null;
        
        for( int i = 0; i < aliases.length; i++ )
        {
            String[] firstAndLast = thisDefendantsAliases.breakIntoFirstAndLast( i );     

            try
            {
                System.setProperty( "webdriver.chrome.driver", "/Users/tomhunter/DEV/workspaceGarnishSlurper/chromedriver" );
                
                driver                              = new ChromeDriver();
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
                
                try
                {
                    List<WebElement> allFullnameElements        = driver.findElements( By.className( "fullname" ) );
                    Iterator<WebElement> allFullnameElementsIt  = allFullnameElements.iterator();
                    
                    List<WebElement> allAddressElements         = driver.findElements( By.className( "address" ) );
                    Iterator<WebElement> allAddressElementsIt   = allAddressElements.iterator();
                    
                    boolean foundSomethingWorthyOfScreenCapping = false;
                    
                    while( allAddressElementsIt.hasNext() )
                    {
                        WebElement fullnameElement      = allFullnameElementsIt.next();
                        String fullnameFromWebSearch    = fullnameElement.getText();
                        
                        WebElement addressElement       = allAddressElementsIt.next();
                        String addressFromWebSearch     = addressElement.getText();
                       
                        defendantWithIncompleteAddress.addNameAddressFromWebSearch( fullnameFromWebSearch + " ==> " + addressFromWebSearch );
                        System.out.println( "For firstAndLast=" + java.util.Arrays.toString( firstAndLast ) + ", found fullnameElement, addressFromWebSearch=" + fullnameElement + ", " + addressFromWebSearch );
                        foundSomethingWorthyOfScreenCapping = true;
                    }

                    if( foundSomethingWorthyOfScreenCapping )
                    {
                        byte[] screepcap                    = driver.getScreenshotAs( OutputType.BYTES );
                        defendantWithIncompleteAddress.setScreepcap( screepcap );                        
                    }
                }
                catch( Exception e )
                {
                    AddressSuss.LOG.info( "addressElement threw an Exception e=" + e );
                }
            }
            catch( Exception ee )
            {
                AddressSuss.LOG.error( "AddressSuss.findDefendantAddress() threw an Exception, ee=" + ee );
            }
            finally
            {
                if( driver != null )
                {
                    //Close the browser
                    driver.quit();   
                }
            }            
        } // end 'for'
    }
    

}
