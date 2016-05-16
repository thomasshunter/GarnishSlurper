package com.populosa.clientfinder.louisiana.parish.neworleans.engine;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
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
        // Create a new instance of the Firefox driver
        // Notice that the remainder of the code relies on the interface, 
        // not the implementation.
        WebDriver driver            = new FirefoxDriver();

        driver.navigate().to( NewOrleansProperties.ORLEANSDC_REMOTE_ACCESS_FIRST_CITY );
        
        WebElement usernameElement  = driver.findElement( By.id( "txtLogin" ) );
        WebElement passwordElement  = driver.findElement( By.id( "txtPassword" ) );
        
        
        
        driver.navigate().to( NewOrleansProperties.ORLEANSDC_NEW_SEARCH );
        
        WebElement element = driver.findElement(By.name("q"));

        // Enter something to search for
        element.sendKeys("Cheese!");

        // Now submit the form. WebDriver will find the form for us from the element
        element.submit();

        // Check the title of the page
        System.out.println("Page title is: " + driver.getTitle());
        
        // Google's search is rendered dynamically with JavaScript.
        // Wait for the page to load, timeout after 10 seconds
        (new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return d.getTitle().toLowerCase().startsWith("cheese!");
            }
        });

        // Should see: "cheese! - Google Search"
        System.out.println("Page title is: " + driver.getTitle());
        
        //Close the browser
        driver.quit();        
    }
    

    public static void main(String[] args)
    {
        // TODO Auto-generated method stub

    }

    @Autowired
    public void setNewOrleansProperties(NewOrleansProperties newOrleansProperties)
    {
        this.newOrleansProperties = newOrleansProperties;
    }

}
