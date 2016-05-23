package com.populosa.clientfinder.louisiana.parish.neworleans.engine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
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
        String keywordFragmentToSearch      = this.newOrleansProperties.getPropertyByKey( "keywordFragmentToSearch" );
        
        String iBM                          = attorneySettingsStartDate.substring( 0, 2 );
        String iBD                          = attorneySettingsStartDate.substring( 3, 5 );
        String iBY                          = attorneySettingsStartDate.substring( 6,  attorneySettingsStartDate.length() );
        
        String iEM                          = attorneySettingsEndDate.substring( 0, 2 );
        String iED                          = attorneySettingsEndDate.substring( 3, 5 );
        String iEY                          = attorneySettingsEndDate.substring( 6, attorneySettingsEndDate.length() );
        
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
        
        WebElement searchButton             = driver.findElement( By.name( "search" ) );
        searchButton.click();
        
        boolean keepSearching               = true;
       
        while( keepSearching )
        {
            searchOnePageOfResults( driver, keywordFragmentToSearch, iBY, iEY );
                        
            WebElement nextPage = driver.findElement( By.name( "next" ) );
            
            if( nextPage == null )
            {
                keepSearching = false;
            }
            else
            {
                nextPage.click();
            }
        }
        
        
        System.out.println( "wait" );
    }
    
    
    private void searchOnePageOfResults( WebDriver driver, String keywordFragmentToSearch, String iBY, String iEY )
    {
        Set<String> clickedLinks                = new HashSet<String>();
        
        while( clickedLinks.size() < 10 ) // Typically 10 links per page.
        {
            int highestLinkIndex = 999;
            
            for( int currentLinkIndex = 0; currentLinkIndex < highestLinkIndex; currentLinkIndex++ )
            {
                List<WebElement> links  = driver.findElements(By.tagName("a"));
                highestLinkIndex        = links.size();
                
           //     if( currentLinkIndex >= highestLinkIndex )
           //     {
           //         continue;
           //     }

                WebElement anElement    = links.get( currentLinkIndex );
                String anElementsText   = anElement.getText();
                boolean alreadyClicked  = clickedLinks.contains( anElementsText );
            
                if( !alreadyClicked && anElementsText.startsWith( iBY ) || anElementsText.startsWith( iEY ) ) // Cannot support searches whose span touches three years.
                {
                    clickedLinks.add( anElementsText );
                    
                    By anElementTextBy  = By.linkText( anElementsText );
                    
                    try
                    {
                        driver.findElement( anElementTextBy ).click();
                    }
                    catch( Exception e )
                    {
                        GarnishSlurper.LOG.error( "GarnishSlurper.searchOnePageOfResults() failed to find an element, anElementsText=" + anElementsText + ", e=" + e );
                        continue;
                    }
                
                    String pageSource                   = driver.switchTo().frame( "modify_case_events_detail" ).getPageSource();
                    //String pageSource                   = driver.getPageSource();
                    pageSource                          = pageSource.toLowerCase();
                
                    int indexOfKeywordFragmentOnPage    = pageSource.indexOf( keywordFragmentToSearch.toLowerCase() );
                
                    if( indexOfKeywordFragmentOnPage > -1 )
                    {                    
                        pullCaseLitigants( driver );
                    }
                }
            }
        }        
    }
    
    
    private void pullCaseLitigants( WebDriver driver )
    {
        driver.switchTo().defaultContent().findElement( By.linkText( "Case Litigants" ) ).click();
        List<WebElement> links              = driver.findElements(By.tagName( "a" ) );
        Set<Integer> alreadyClickedDetails  = new HashSet<Integer>();
        
        for( int i = 0; i < links.size(); i++ )
        {
            WebElement aLink    = links.get( i );
            String linkText     = aLink.getText();
            int indexOfDetails  = linkText.indexOf( NewOrleansProperties.DETAILS_LINK_CAPTION );
        
            if( indexOfDetails > -1 ) // The link has the word 'Details' in it.
            {
                boolean isNewlyAdded = alreadyClickedDetails.add( new Integer( i ) ); // The 'Details' link has not yet been clicked
                
                if( isNewlyAdded )
                {
                    aLink.click();
                    String possibleLitigantPageSource   = driver.getPageSource();
                    int indexOfPlaintiffFlag            = possibleLitigantPageSource.indexOf( NewOrleansProperties.PLAINTIFF );
                    int indexOfDefendantFlag            = possibleLitigantPageSource.indexOf( NewOrleansProperties.DEFENDANT );
                
                    if( indexOfPlaintiffFlag > -1 )
                    {
                        driver.findElement( By.name( "back") ).click(); // Not a Defendant
                        links               = driver.findElements(By.tagName( "a" ) );
                        String pageTitle    = driver.getTitle();
                    
                        System.out.println( "After 'Back': " + pageTitle );
                    }
                    else if( indexOfDefendantFlag > -1 )
                    {
                        String pageSource = driver.getPageSource();
                        
                        System.out.println( "wait" );
                    }
                }
            }
        }
        
        WebElement backButton               = driver.findElement( By.name( "back" ) );
        backButton.click();

        //driver.navigate().back();
    }
    
    
    private void collectOneDefendant( WebDriver driver )
    {
        System.out.println( "wait");
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
