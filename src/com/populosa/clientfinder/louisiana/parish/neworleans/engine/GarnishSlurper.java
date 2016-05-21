package com.populosa.clientfinder.louisiana.parish.neworleans.engine;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;

import com.populosa.clientfinder.louisiana.parish.neworleans.util.NewOrleansProperties;

public class GarnishSlurper
{
    private static Logger LOG       = Logger.getLogger( GarnishSlurper.class );
    
    private NewOrleansProperties newOrleansProperties;
    
    public GarnishSlurper()
    {
        this.newOrleansProperties   = new NewOrleansProperties();
        
        // Create a new instance of the Firefox driver
        // Notice that the remainder of the code relies on the interface, 
        // not the implementation.
        System.setProperty( "webdriver.chrome.driver", "/Users/tomhunter/DEV/workspaceGarnishSlurper/chromedriver" );
        
        WebDriver driver            = new ChromeDriver();

        goToOrleansDcRemoteAccessFirstCityAndLogin( driver );
        
        executeOrleansDCSearch( driver );
        
        
        
        //WebElement element = driver.findElement(By.name("q"));

        // Enter something to search for
        //element.sendKeys("Cheese!");

        // Now submit the form. WebDriver will find the form for us from the element
        //element.submit();

        // Check the title of the page
        System.out.println("Page title is: " + driver.getTitle());
        
        // Google's search is rendered dynamically with JavaScript.
        // Wait for the page to load, timeout after 10 seconds
        (new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() 
        {
            public Boolean apply(WebDriver d) 
            {
                return d.getTitle().toLowerCase().startsWith("cheese!");
            }
        });

        // Should see: "cheese! - Google Search"
        System.out.println("Page title is: " + driver.getTitle());
        
        //Close the browser
        driver.quit();        
    }
    
    private void goToOrleansDcRemoteAccessFirstCityAndLogin( WebDriver driver )
    {
        try
        {
            driver.navigate().to( NewOrleansProperties.ORLEANSDC_REMOTE_ACCESS_FIRST_CITY );
        
            WebElement usernameElement  = driver.findElement( By.id( "txtLogin" ) );
            usernameElement.sendKeys( this.newOrleansProperties.getPropertyByKey( "username" ));

            WebElement passwordElement  = driver.findElement( By.id( "txtPassword" ) );
            passwordElement.sendKeys( this.newOrleansProperties.getPropertyByKey( "password" ) );
            
            WebElement submitElement    = driver.findElement( By.id( "cmdSubmit" ) );
            submitElement.click();
            
            // xpath=/html/body/form[@id='frmLogin']/table[2]/tbody/tr[2]/td/table/tbody/tr[3]/td/input[@id='cmdSubmit']"
        }
        catch( Exception e )
        {
            GarnishSlurper.LOG.error( "GarnishSlurper.goToOrleansDcRemoteAccessFirstCityAndLogin() threw an Exception e=" + e );
        }
    }
    
    private void executeOrleansDCSearch( WebDriver driver )
    {        
        String attorneySettingsStartDate    = this.newOrleansProperties.getPropertyByKey( "searchStartDateMMDDYYYY" );
        String attorneySettingsEndDate      = this.newOrleansProperties.getPropertyByKey( "searchEndDateMMDDYYYY" );
        
        String iBM                          = attorneySettingsStartDate.substring( 0, 2 );
        String iBD                          = attorneySettingsStartDate.substring( 2, 4 );
        String iBY                          = attorneySettingsStartDate.substring( 4,  attorneySettingsStartDate.length() );
        
        String iEM                          = attorneySettingsEndDate.substring( 0, 2 );
        String iED                          = attorneySettingsEndDate.substring( 2, 4 );
        String iEY                          = attorneySettingsEndDate.substring( 0, attorneySettingsEndDate.length() );
        
        driver.navigate().to( NewOrleansProperties.ORLEANSDC_NEW_SEARCH );
                
        WebElement startDay                 = driver.findElement( By.name( "iBD" ) );
        startDay.sendKeys( iBD );
        
        WebElement startMonth               = driver.findElement( By.name( "iBM" ) );
        startMonth.sendKeys( iBM );
        
        WebElement startYear                = driver.findElement( By.name( "iBY" ) );
        startYear.sendKeys( iBY );
        
        WebElement endDay                   = driver.findElement( By.name( "iED" ) );
        endDay.sendKeys( iED );
        
        WebElement endMonth                 = driver.findElement( By.name( "iEM" ) );
        endMonth.sendKeys( iEM );
        
        WebElement endYear                  = driver.findElement( By.name( "iEY" ) );
        endYear.sendKeys( iEY );
        
        WebElement typeOfSearch             = driver.findElement( By.name( "iType" ) );
        typeOfSearch.sendKeys( NewOrleansProperties.SEARCH_TYPE_ALL_0 );
        
        
        System.out.println( "wait" );
    }
    
    
    @SuppressWarnings("unused")
    public static void main(String[] args)
    {
        GarnishSlurper solo = new GarnishSlurper();
    }

    @Autowired
    public void setNewOrleansProperties(NewOrleansProperties newOrleansProperties)
    {
        this.newOrleansProperties = newOrleansProperties;
    }

}
