**Impact Analyser: **

It is a known fact that Automation Engineers do frequently asked to calculate the impact on the automated regression test suite when the business wants to change the UI of their application.
In such requirements, we all look into the framework to identify the list of pages being used in each test case to collect the information and notify to project managers about the impact. I sometimes feel so much
 irritated to go though all the list of tests and collect the pages used in each test. 
 So, for those who hate analysing all the tests manually to collect the test to page mapping information to identify the impact of a UI change on the automation test pack here is a solution.
 Using this library we can start calculating the number of tests impacted by a change in the UI.

Rules: To use this library, you must have a base page class that must be inherited by all your page classes.

Basic Business Rules: There are 2 types of business rules available with this library.

1. Page Level Rules: Page Level Rule tells this library to collect all the pages that inherit the specified page. Using this page level rules you can specify the BasePage class's fully qualified name.  
    To define the PageRules, you write the following code
    ```
     PageRules pageRules = new PageRules();
     pageRules.setBasePageClass("com.sample.tests.BaseSeleniumPage");
     pageRules.setPageClassPackages(Arrays.asList("com.sample"));
    ```
  
1. Test Level Rules: Page Level Rule tells this library to collect all the pages that inherit the specified page. Using this page level rules you can specify the BasePage class's fully qualified name.  
    To define the PageRules, you write the following code
    ```
    TestRules testRules = new TestRules();
    testRules.setBaseTestClass("com.sample.tests.BaseTest");
    testRules.setTestClassPackages(Arrays.asList("com.sample"));
    ```
  
_**How to use this library:**_ 

_Following the steps to calculate the impact_

 1. Add the following maven dependency to your pom.xml.
```
<dependency>
    <groupId>ui-impact-analyser</groupId>
    <artifactId>impact.analyser</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```
 1. Write a unit test in your framework by providing some basic business rules. This library supports cucumber BDD, TestNG, JUnit frameworks.
 
 Example1: Evaluate the Impact for Cucumber Framework
 ```
  @Test
     public void testBDDCollector() {
         try {
             PageRules pageRules = new PageRules();
             pageRules.setBasePageClass("com.sample.tests.BaseSeleniumPage");
             pageRules.setPageClassPackages(Arrays.asList("com.sample"));
             TestRules testRules = new TestRules();
             testRules.setBaseTestClass("com.sample.tests.BaseTest");
             testRules.setTestClassPackages(Arrays.asList("com.sample"));
             Injector injector = Collector.getInjector();
             BDDCollector bddCollector = injector.getInstance(BDDCollector.class);
             bddCollector.setPageRules(pageRules);
             bddCollector.setTestRules(testRules);
             bddCollector.collectTrace(new String[]{"com.sample"}, "/Users/Yuvaraj/dev/impact-analyser/src/test/resources");
             ReportGenerator reportGenerator = new ReportGenerator();
             reportGenerator.generateReport();
         } catch (Exception ex) {
             ex.printStackTrace();
         }
     }
 ```
 Example2: Evaluate the Impact for TestNG/JUnit Framework
```
 @Test
    public void testTDDCollector() {
        try {
            PageRules pageRules = new PageRules();
            pageRules.setBasePageClass("com.sample.tests.BaseSeleniumPage");
            pageRules.setPageClassPackages(Arrays.asList("com.sample"));
            TestRules testRules = new TestRules();
            testRules.setBaseTestClass("com.sample.tests.BaseSeleniumTest");
            testRules.setTestClassPackages(Arrays.asList("com.sample.tests"));
            Injector injector = Collector.getInjector();
            injector.injectMembers(pageRules);
            injector.injectMembers(testRules);
            TDDCollector tddCollector = injector.getInstance(TDDCollector.class);
            tddCollector.setPageRules(pageRules);
            tddCollector.setTestRules(testRules);
            tddCollector.collectTrace(new String[]{"com.sample"});
            ReportGenerator reportGenerator = new ReportGenerator();
            reportGenerator.generateReport();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
```
 1. Once the test execution is successful, you will see a folder named 'impact' in your project directory in which you will find index.html, report.json files
 The HTML report will look like 
 [HTML REPORT](htmlreport.png)

**Rules:**
Your page should not contain any test method
Your page class should extend a base class
Your Test class should extend a base class

**limitations: **
Rule1: may not give you the results if the methods are from interface
Rule2: All the selenium elements that are available in the methods must be from page classes

**ToDos:**
Include validators to check if the basePageClass, baseTesClass are not null
Make TestRules, PageRules are injectable by using guice injectMembers