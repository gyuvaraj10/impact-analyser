package com.sample.test2;

import com.sample.tests.BaseTest;
import com.sample.tests.HomePage;
import cucumber.api.PendingException;
import cucumber.api.java.en.Given;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 * Created by Yuvaraj on 02/03/2018.
 */
public class ANotherStepDef extends BaseTest {
    HomePage page;

    @Given("^I have this$")
    public void iHaveThis() throws Throwable {
        WebDriver driver = new ChromeDriver();
        page = new HomePage(driver);
        page.getOnlineCheckInHeaderText1();
        page.getOnlineCheckInHeaderText();
    }

    @Given("^I Test \"([^\"]*)\" SKiped$")
    public void i_Test_SKiped(String arg1) throws Throwable {
        System.out.println(arg1);
    }


}
