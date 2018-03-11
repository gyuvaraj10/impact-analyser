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
    pageRules.setBasePageClass("pages.BasePage");
    ```
    
1. Element Level Rules: Element Level Rule tells the library 
  1. If you are following a model where you define all the page's webelements in the same page class
  1.  If you are following a model where you define 2 layers of classes for your pages, 1 to define the elements and other to define the actions
  To define the Element Rules, you write the following code
  Example1: Web Elements and page actions are defined in the same class
  ```
   ElementRules elementRules = new ElementRules();
   elementRules.setElementsDefinedWithInPageClassOnly(false);
   elementRules.setElementClassPackages(Arrays.asList("com.sample"));
   elementRules.setPageClassPackages(Arrays.asList("com.sample.test2", "com.sample.tests"));
  ```
  Example2: Web Elements and page actions are defined in the different class (2 layers are defined to maintain pages)
  ```
   ElementRules elementRules = new ElementRules();
   elementRules.setElementsDefinedWithInPageClassOnly(true);
   elementRules.setElementClassPackages(Arrays.asList("pages"));
   elementRules.setPageClassPackages(Arrays.asList("pages"));
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
 public void calculateTheImpact() throws Exception {
     PageRules pageRules = new PageRules();
     pageRules.setBasePageClass("pages.BasePage");
     ElementRules elementRules = new ElementRules();
     elementRules.setElementsDefinedWithInPageClassOnly(true);
     elementRules.setElementClassPackages(Arrays.asList("pages"));
     elementRules.setPageClassPackages(Arrays.asList("pages"));
     BDDCollector bddCollector = new BDDCollector(pageRules, elementRules);
     List<JsonObject> cucumberReports = bddCollector.collectJsonReport(new String[]{"tests.web"},
             "/Users/Yuvaraj/dev/selenium-guice/src/test/resources/features");
     ReportGenerator reportGenerator = new ReportGenerator();
     reportGenerator.generateReport(cucumberReports);
 }
 ```
 Example2: Evaluate the Impact for TestNG/JUnit Framework
 ```
 public void calculateTheImpact() throws Exception {
     PageRules pageRules = new PageRules();
     pageRules.setBasePageClass("com.sample.tests.BaseSeleniumPage");
     ElementRules elementRules = new ElementRules();
     elementRules.setElementsDefinedWithInPageClassOnly(false);
     elementRules.setElementClassPackages(Arrays.asList("com.sample"));
     elementRules.setPageClassPackages(Arrays.asList("com.sample.test2", "com.sample.tests"));
     TDDCollector tddCollector = new TDDCollector(pageRules, elementRules);
     List<JsonObject> jsonObjects = tddCollector.collectJsonReport(new String[]{"com.sample.tests"});
     ReportGenerator reportGenerator = new ReportGenerator();
          reportGenerator.generateReport(cucumberReports);
  }
 ```
 1. Once the test execution is successful, you will see a folder named 'impact' in your project directory in which you will find index.html, report.json files
 The HTML report will look like 
 [HTML REPORT](htmlreport.png)