package com.sample.tests;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

/**
 * Created by Yuvaraj on 16/02/2018.
 */
public class HomePage extends BaseSeleniumPage {

    public HomePage(WebDriver driver){
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = "#nav-your-amazon")
    private WebElement servicesLink;

    @FindBy(css = "#nav-your-amazon")
    private List<WebElement> servicesLink11;

    @FindBy(css = "#nav-your-amazon")
    private WebElement servicesLink1;

    @FindBy(css = "#nav-your-amazon")
    private WebElement servicesLink2;

    public String getOnlineCheckInHeaderText() {
         if(servicesLink.getText().equals("asdasd")) {
            servicesLink1.click();
         } else {
             getOnlineCheckInHeaderText1();
         }
         return "";
    }

    public void getOnlineCheckInHeaderText1() {
        servicesLink11.get(0).click();
    }

}
