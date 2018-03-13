package com.sample.tests;

import com.sample.elements.otherclass.ElementsInOtherClass;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

/**
 * Created by Yuvaraj on 27/02/2018.
 */
public class LandingPage extends HomePage {


    public LandingPage(WebDriver driver){
        super(driver);
    }

    @FindBy(css = "#nav-your-amazon")
    private WebElement servicesLink;

    @FindBy(css = "#nav-your-amazon")
    private List<WebElement> servicesLink11;

    @FindBy(css = "#nav-your-amazon")
    private WebElement servicesLink1;

    @FindBy(css = "#nav-your-amazon")
    private WebElement servicesLink2;

    public String getasdkaskd() {
        By younaughty = By.className("asd");
        younaughty.findElement(null);
        if(servicesLink.getText().equals("asdasd")) {
            servicesLink1.click();
        }
        return "";
    }

    public void login() {
        ElementsInOtherClass.userName.toString();
        servicesLink11.get(0).click();
//        baseElement.click();
    }

    public static LandingPage getLandingPage() {
        return new LandingPage(null);
    }


}
