package com.framework;

import com.google.gson.Gson;
import com.impact.analyser.PageEngine;
import com.impact.analyser.report.PageInfo;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by Yuvaraj on 12/03/2018.
 */
public class PageEngineTest {

    @Test
    public void testIfAFieldIsSeleniumFieldWhenItsTypeIsWebElement() throws NoSuchMethodException, NoSuchFieldException,
            InvocationTargetException, IllegalAccessException {
        class DummyClass {
            @FindBy(id = "")
            private WebElement element;
        }
        PageEngine pageEngine = new PageEngine();
        Method method = pageEngine.getClass().getDeclaredMethod("isSeleniumField", Field.class);
        method.setAccessible(true);
        Field field = DummyClass.class.getDeclaredField("element");
        field.setAccessible(true);
        boolean result = (boolean)method.invoke(pageEngine, field);
        Assert.assertTrue(result);
    }

    @Test
    public void testIfAFieldIsSeleniumFieldWhenItsTypeIsListAndHasFindByAnnotation() throws NoSuchMethodException, NoSuchFieldException,
            InvocationTargetException, IllegalAccessException {
        class DummyClass {
            @FindBy(id = "")
            private List<WebElement> element;
        }
        PageEngine pageEngine = new PageEngine();
        Method method = pageEngine.getClass().getDeclaredMethod("isSeleniumField", Field.class);
        method.setAccessible(true);
        Field field = DummyClass.class.getDeclaredField("element");
        field.setAccessible(true);
        boolean result = (boolean)method.invoke(pageEngine, field);
        Assert.assertTrue(result);
    }

    @Test
    public void testIfAFieldIsSeleniumFieldWhenItsTypeIsListAndHasFindBysAnnotation() throws NoSuchMethodException, NoSuchFieldException,
            InvocationTargetException, IllegalAccessException {
        class DummyClass {
            @FindBys({@FindBy(id = "")})
            private List<WebElement> element;
        }
        PageEngine pageEngine = new PageEngine();
        Method method = pageEngine.getClass().getDeclaredMethod("isSeleniumField", Field.class);
        method.setAccessible(true);
        Field field = DummyClass.class.getDeclaredField("element");
        field.setAccessible(true);
        boolean result = (boolean)method.invoke(pageEngine, field);
        Assert.assertTrue(result);
    }

    @Test
    public void testIfAFieldIsSeleniumFieldWhenItsTypeIsListAndHasFindAllAnnotation() throws NoSuchMethodException, NoSuchFieldException,
            InvocationTargetException, IllegalAccessException {
        class DummyClass {
            @FindAll({@FindBy(id = "")})
            private List<WebElement> element;
        }
        PageEngine pageEngine = new PageEngine();
        Method method = pageEngine.getClass().getDeclaredMethod("isSeleniumField", Field.class);
        method.setAccessible(true);
        Field field = DummyClass.class.getDeclaredField("element");
        field.setAccessible(true);
        boolean result = (boolean)method.invoke(pageEngine, field);
        Assert.assertTrue(result);
    }

    @Test
    public void testIfAFieldIsSeleniumFieldWhenItsTypeIsBy() throws NoSuchMethodException, NoSuchFieldException,
            InvocationTargetException, IllegalAccessException {
        class DummyClass {
            private By element;
        }
        PageEngine pageEngine = new PageEngine();
        Method method = pageEngine.getClass().getDeclaredMethod("isSeleniumField", Field.class);
        method.setAccessible(true);
        Field field = DummyClass.class.getDeclaredField("element");
        field.setAccessible(true);
        boolean result = (boolean)method.invoke(pageEngine, field);
        Assert.assertTrue(result);
    }

    @Test
    public void testIfAFieldIsSeleniumFieldWhenItsTypeIsNotWebElement() throws NoSuchMethodException, NoSuchFieldException,
            InvocationTargetException, IllegalAccessException {
        class DummyClass {
            private String element;
            private List<String> elements;
        }
        PageEngine pageEngine = new PageEngine();
        Method method = pageEngine.getClass().getDeclaredMethod("isSeleniumField", Field.class);
        method.setAccessible(true);
        Field field = DummyClass.class.getDeclaredField("element");
        field.setAccessible(true);
        Field field1 = DummyClass.class.getDeclaredField("elements");
        field.setAccessible(true);
        boolean result = (boolean)method.invoke(pageEngine, field);
        Assert.assertFalse(result);
        boolean result1 = (boolean)method.invoke(pageEngine, field1);
        Assert.assertFalse(result1);
    }

    @Test
    public void testIfAFieldIsSeleniumFieldWhenItsTypeIsListOfBy() throws NoSuchMethodException, NoSuchFieldException,
            InvocationTargetException, IllegalAccessException {
        class DummyClass {
            private List<By> element;
        }
        PageEngine pageEngine = new PageEngine();
        Method method = pageEngine.getClass().getDeclaredMethod("isSeleniumField", Field.class);
        method.setAccessible(true);
        Field field = DummyClass.class.getDeclaredField("element");
        field.setAccessible(true);
        boolean result = (boolean)method.invoke(pageEngine, field);
        Assert.assertTrue(result);
    }

    @Test
    public void testIfSeleniumFieldsAreReturnedFromPageClass()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        class DummyClass {
            @FindBy(id = "")
            private WebElement userName;
        }
        class DummyClass1 {
            @FindBy(id = "")
            private WebElement userNampae;
            private String userNameV;
        }
        class DummyClass2 {
            private String userNameV;
        }
        Set<Class<?>> classes = new HashSet<>();
        classes.add(DummyClass.class);
        classes.add(DummyClass1.class);
        classes.add(DummyClass2.class);
        PageEngine pageEngine = new PageEngine();
        Method getSeleniumFieldsFromClasses = pageEngine.getClass().getDeclaredMethod("getSeleniumFieldsFromClasses", Set.class);
        getSeleniumFieldsFromClasses.setAccessible(true);
        Map<String, List<String>> seleniumFields=  (Map<String, List<String>>) getSeleniumFieldsFromClasses.invoke(pageEngine, classes);
        Map<String, List<String>> expectedData = new HashMap<>();
        expectedData.put("com.framework.PageEngineTest$8DummyClass", Arrays.asList("userName"));
        expectedData.put("com.framework.PageEngineTest$1DummyClass1", Arrays.asList("userNampae"));
        Gson gson = new Gson();
        String actual = gson.toJson(seleniumFields);
        String expected = gson.toJson(expectedData);
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void testIfgetPageReportsForElementsWithInPageClassesOnlyReturnsPageReports()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        class DummyClass {
            @FindBy(id = "")
            private WebElement userName;
            @FindBy(id = "")
            private List<WebElement> anotherField;

            public void login() {
                userName.sendKeys("asdasd");
            }

            public void login2() {
                anotherField.clear();
            }
        }
        class DummyClass1 {
            @FindBy(id = "")
            private WebElement userNampae;
            @FindBy(id = "")
            private WebElement password;

            private List<By> anotherFields;

            private String userNameV;

            public void setText() {
                userNampae.sendKeys("asdasd");
                privateMethod();
            }
            private void privateMethod() {
                userNampae.click();
                anotherFields.clear();
            }

            public void pppp() {
                anotherFields.get(0).findElement(null);
            }
        }
        class DummyClass2 {
            private String userNameV;
        }
        Set<Class<?>> classes = new HashSet<>();
        classes.add(DummyClass.class);
        classes.add(DummyClass1.class);
        classes.add(DummyClass2.class);
        PageEngine pageEngine = new PageEngine();
        Method getSeleniumFieldsFromClasses = pageEngine.getClass()
                .getDeclaredMethod("getPageReportsForElementsWithInPageClassesOnly", Set.class);
        getSeleniumFieldsFromClasses.setAccessible(true);
        List<PageInfo>  pageReports =
                (List<PageInfo> ) getSeleniumFieldsFromClasses.invoke(pageEngine, classes);
        String expectedData = "[{\"pageName\":\"com.framework.PageEngineTest$9DummyClass\",\"methodReportList\":[{\"methodName\":\"login\",\"fieldAndFieldClassName\":{\"userName\":\"com.framework.PageEngineTest$9DummyClass\"}},{\"methodName\":\"login2\",\"fieldAndFieldClassName\":{\"anotherField\":\"com.framework.PageEngineTest$9DummyClass\"}}]},{\"pageName\":\"com.framework.PageEngineTest$2DummyClass1\",\"methodReportList\":[{\"methodName\":\"pppp\",\"fieldAndFieldClassName\":{\"anotherFields\":\"com.framework.PageEngineTest$2DummyClass1\"}},{\"methodName\":\"setText\",\"privateMethods\":[\"privateMethod\"],\"fieldAndFieldClassName\":{\"userNampae\":\"com.framework.PageEngineTest$2DummyClass1\"}},{\"methodName\":\"privateMethod\",\"fieldAndFieldClassName\":{\"anotherFields\":\"com.framework.PageEngineTest$2DummyClass1\",\"userNampae\":\"com.framework.PageEngineTest$2DummyClass1\"}}]}]";
        Gson gson = new Gson();
        String actual = gson.toJson(pageReports);
        Assert.assertTrue(actual.contains(expectedData));
    }
}
