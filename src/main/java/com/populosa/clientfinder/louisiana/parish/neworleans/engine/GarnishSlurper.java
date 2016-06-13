package com.populosa.clientfinder.louisiana.parish.neworleans.engine;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;

import com.populosa.clientfinder.louisiana.parish.neworleans.address.AddressSuss;
import com.populosa.clientfinder.louisiana.parish.neworleans.bean.Defendant;
import com.populosa.clientfinder.louisiana.parish.neworleans.util.ExcelUtil;
import com.populosa.clientfinder.louisiana.parish.neworleans.util.NewOrleansProperties;

public class GarnishSlurper
{    
    private static Logger LOG           = Logger.getLogger( GarnishSlurper.class );
        
    private NewOrleansProperties newOrleansProperties;
    private List<String> casesNumbersWithKeywordMatches;
    private List<Defendant> defendants  = new ArrayList<Defendant>();
    
    public GarnishSlurper()
    {
        GarnishSlurper.setupLog4J();
        
        //System.setProperty("log4j.configuration","/Users/tomhunter/DEV/workspaceGarnishSlurper/GarnishSlurper/src/main/resources/log4j.properties");

        this.newOrleansProperties           = new NewOrleansProperties();
        this.casesNumbersWithKeywordMatches = new ArrayList<String>();
        
        // Create a new instance of the Firefox driver
        // Notice that the remainder of the code relies on the interface, 
        // not the implementation.
        System.setProperty( "webdriver.chrome.driver", "/Users/tomhunter/DEV/workspaceGarnishSlurper/chromedriver" );
        
        WebDriver driver                    = new ChromeDriver();

        goToOrleansDcRemoteAccessFirstCityAndLogin( driver );
        
        executeOrleansDCSearch( driver );
        
        echoSearchFindings();

        pullCaseLitigants( driver );
                
        //Close the browser
        driver.quit();        
        
        sussOutMissingAddressesUsingTheWeb( driver );
          
        writeDefendantsToExcel();
    }
    
    
    @SuppressWarnings("unused")
    private void writeDefendantsToExcel()
    {
        ExcelUtil util = new ExcelUtil( this.newOrleansProperties, this.defendants );
    }
    
    
    private void sussOutMissingAddressesUsingTheWeb( WebDriver driver )
    {
        if( this.defendants == null || this.defendants.size() == 0 )
        {
            GarnishSlurper.LOG.info( "GarnishSlurper.sussOutMissingAddressesUsingTheWeb() is bailing. No Defendants found. Dratski." );
            
            return;
        }
        
        AddressSuss suss                = new AddressSuss();
        Iterator<Defendant> defendants  = this.defendants.iterator();
        
        while( defendants.hasNext() )
        {
            Defendant defendantWithIncompleteAddress = defendants.next();
            
            suss.findDefendantAddress( defendantWithIncompleteAddress );
        }
    }
    
    
    private static void setupLog4J()
    {
        try 
        {
            File file       = new File("//Users/tomhunter/DEV/workspaceGarnishSlurper/GarnishSlurper/src/log4j.properties");
            String filePath = file.toURI().toURL().toString();
            
            System.setProperty("log4j.configuration", filePath );
        } 
        catch (MalformedURLException e) 
        {
            e.printStackTrace();
        }
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
                        
            WebElement nextPage = null;
            
            try
            {
                nextPage = driver.findElement( By.name( "next" ) );
            }
            catch( Exception e )
            {
                GarnishSlurper.LOG.info( "GarnishSlurper.executeOrleansDCSearch() threw an Exception on the last page. No biggie., e=" + e );
            }
            
            if( nextPage == null )
            {
                keepSearching = false;
            }
            else
            {
                nextPage.click();
            }
        }        
    }
    
    private void searchOnePageOfResults( WebDriver driver, String keywordFragmentToSearch, String iBY, String iEY )
    {
        Set<String> clickedLinks                = new HashSet<String>();
        boolean stillMorePages                  = true;
        
        while( stillMorePages && clickedLinks.size() < 10 ) // Typically 10 links per page.
        {
            int highestLinkIndex = 30; // First seven links we don't want. This is just to get into the loopo.
            
            for( int currentLinkIndex = 0; currentLinkIndex < highestLinkIndex; currentLinkIndex++ )
            {
                List<WebElement> links  = driver.findElements(By.tagName("a"));
                highestLinkIndex        = links.size();  
                
                if( currentLinkIndex > highestLinkIndex )
                {
                    System.out.println( "wait" );
                }
                
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
                
                    String pageSource                   = null;
                    
                    try
                    {
                        pageSource = driver.switchTo().frame( "modify_case_events_detail" ).getPageSource();
                    }
                    catch( Exception e )
                    {
                        GarnishSlurper.LOG.error( "\n\nGarnishSlurper.searchOnePageOfResults() failed to find 'modify_case_events_detail', e=" + e ); 
                    }
                    
                    pageSource                          = pageSource.toLowerCase();                
                    int indexOfKeywordFragmentOnPage    = pageSource.indexOf( keywordFragmentToSearch.toLowerCase() );
                
                    if( indexOfKeywordFragmentOnPage > -1 )
                    {  
                        this.casesNumbersWithKeywordMatches.add( anElementsText );
                        
                        //pullCaseLitigants( driver );
                    }
                    
                    driver.navigate().back();
                }
            }
            
            WebElement nextPage = null;
            
            try
            {
                nextPage = driver.findElement( By.name( "next" ) );
            }
            catch( Exception e )
            {
                GarnishSlurper.LOG.info( "No Next available, should be last page. e=" + e );
            }
            
            if( nextPage == null )
            {
                stillMorePages = false;
            }
        }        
    }
    
    private void pullCaseLitigants( WebDriver driver )
    {
        Iterator<String> caseNumbersWithKeywordMatchesIt   = this.casesNumbersWithKeywordMatches.iterator();
        
        while( caseNumbersWithKeywordMatchesIt.hasNext() )
        {
            String aCaseNumberToSearch      = caseNumbersWithKeywordMatchesIt.next();
            if( aCaseNumberToSearch != null )
            {
                int indexOfYearCaseHyphen   = aCaseNumberToSearch.indexOf( NewOrleansProperties.SEARCH_HYPHEN );
                if( indexOfYearCaseHyphen > -1 )
                {
                    String[] yearCase = aCaseNumberToSearch.split( NewOrleansProperties.SEARCH_HYPHEN  );
                    
                    if( yearCase.length == 2 )
                    {
                        driver.navigate().to( NewOrleansProperties.ORLEANSDC_NEW_SEARCH );
                        
                        WebElement yearNumber               = driver.findElement( By.name( "sNum1" ) );
                        yearNumber.sendKeys( yearCase[ 0 ].trim() );

                        WebElement caseNumber               = driver.findElement( By.name( "sNum2" ) );
                        caseNumber.sendKeys( yearCase[ 1 ].trim() );
                        
                        WebElement searchButton             = driver.findElement( By.name( "search" ) );
                        searchButton.click();
                        
                        driver.switchTo().defaultContent().findElement( By.linkText( "Case Litigants" ) ).click();
                        List<WebElement> links              = driver.findElements(By.tagName( "a" ) );
                        Set<Integer> alreadyClickedDetails  = new HashSet<Integer>();
                        boolean foundDefendant              = false;
                        
                        for( int i = 0; i < links.size(); i++ )
                        {
                           if( foundDefendant )
                           {
                               break;
                           }
                                                           
                           links               = driver.findElements(By.tagName( "a" ) );
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
                                       foundDefendant = collectOneDefendant( driver, aCaseNumberToSearch );
                                       
                                       System.out.println( "aCaseNumberToSearch=" + aCaseNumberToSearch + ": foundDefendant=" + foundDefendant );
                                   }
                               }
                           }
                       }       
                   }
               }
            }
        }        
    }
    
    private boolean collectOneDefendant( WebDriver driver, String aCaseNumberToSearch )
    {       
        By nameXpath                        = By.xpath( "//*[@id=\"divResults\"]/p/table/tbody/tr[1]/td[2]/b" );
        WebElement nameDynamicElement       = (new WebDriverWait(driver,10)).until( ExpectedConditions.presenceOfElementLocated( nameXpath ) );
        String name                         = nameDynamicElement.getText();
        
        By address1Xpath                    = By.xpath( "//*[@id=\"divResults\"]/p/table/tbody/tr[3]/td[2]/b" );
        WebElement address1DynamicElement   = (new WebDriverWait(driver,10)).until( ExpectedConditions.presenceOfElementLocated( address1Xpath ) );
        String address1                     = address1DynamicElement.getText();
        
        By address2Xpath                    = By.xpath( "//*[@id=\"divResults\"]/p/table/tbody/tr[4]/td[2]/b" );
        WebElement address2DynamicElement   = (new WebDriverWait(driver,10)).until( ExpectedConditions.presenceOfElementLocated( address2Xpath ) );
        String address2                     = address2DynamicElement.getText();
        
        By cityXpath                        = By.xpath( "//*[@id=\"divResults\"]/p/table/tbody/tr[5]/td[2]/b" );
        WebElement cityDynamicElement       = (new WebDriverWait(driver,10)).until( ExpectedConditions.presenceOfElementLocated( cityXpath ) );
        String city                         = cityDynamicElement.getText();

        By stateXpath                       = By.xpath( "//*[@id=\"divResults\"]/p/table/tbody/tr[5]/td[4]/b" );
        WebElement stateDynamicElement      = (new WebDriverWait(driver,10)).until( ExpectedConditions.presenceOfElementLocated( stateXpath ) );
        String state                        = stateDynamicElement.getText();

        By zipXpath                         = By.xpath( "//*[@id=\"divResults\"]/p/table/tbody/tr[5]/td[6]/b" );
        WebElement zipDynamicElement        = (new WebDriverWait(driver,10)).until( ExpectedConditions.presenceOfElementLocated( zipXpath ) );
        String zip                          = zipDynamicElement.getText();

        
        Defendant aDefendant                = new Defendant();
        aDefendant.setCaseNumber( aCaseNumberToSearch );
        aDefendant.setName( name );
        aDefendant.setAddress1( address1 );
        aDefendant.setAddress2( address2 );
        aDefendant.setCity( city );
        aDefendant.setState( state );
        aDefendant.setZip( zip );
        
        this.defendants.add( aDefendant );
        
        GarnishSlurper.LOG.info( "Just added aDefendant=" + aDefendant );
        
        return true;
    }

    private void echoSearchFindings()
    {
        String attorneySettingsStartDate    = this.newOrleansProperties.getPropertyByKey( "searchStartDateMMDDYYYY" );
        String attorneySettingsEndDate      = this.newOrleansProperties.getPropertyByKey( "searchEndDateMMDDYYYY" );
        String keywordFragmentToSearch      = this.newOrleansProperties.getPropertyByKey( "keywordFragmentToSearch" );

        GarnishSlurper.LOG.info( "GarnishSlurper.echoSearchFindings: (From " 
                                    + attorneySettingsStartDate 
                                    + " to " 
                                    + attorneySettingsEndDate 
                                    + ", keyword=" 
                                    + keywordFragmentToSearch 
                                    + ", Found " 
                                    + this.casesNumbersWithKeywordMatches.size()
                                    + " Case Numbers: " 
                                    + this.casesNumbersWithKeywordMatches.toString()
                                    + 
                                    ". Getting details for the cases above. Hang on... " );
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
