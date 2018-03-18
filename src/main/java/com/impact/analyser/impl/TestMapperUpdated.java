package com.impact.analyser.impl;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.impact.analyser.GraphWriter;
import com.impact.analyser.interfaces.IPageInformation;
import com.impact.analyser.interfaces.ITestDefInformation;
import com.impact.analyser.interfaces.ITestMapper;
import com.impact.analyser.report.MethodInfo;
import com.impact.analyser.report.TestReport;
import com.impact.analyser.rules.PageRules;
import com.impact.analyser.rules.TestRules;
import com.impact.analyser.utils.ClassUtils;
import org.apache.commons.io.FileUtils;
import org.objectweb.asm.tree.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by Yuvaraj on 18/03/2018.
 */
public class TestMapperUpdated  implements ITestMapper {


    @Inject
    private static ClassUtils classUtils;

    @Inject
    private IPageInformation pageInformation;

    @Inject
    private ITestDefInformation testInformation;

    @Inject
    private GraphWriter graphWriter;

    //This holds the method parent and it self as a map to make sure
    // the scanning does not happen once the scanning is completed for a flow
    private Map<String, Set<String>> ownerAndItsMethods;

    @Inject
    private PageRules pageRules;
    @Inject
    private TestRules testRules;

    private Map<Class<?>, ClassNode> pageClassNodeMap;
    private Map<Class<?>, Set<MethodNode>> testClassMethodMap;
    private Map<Class<?>, ClassNode> testClassNodes;
    private Map<Class<?>, ClassNode> localNodeMap; //combination of both pageclass and test class nodes
    private Set<Class<?>> pageClasses;

    public void setPageClasses(Set<Class<?>> pageClasses) {
        this.pageClasses = pageClasses;
    }

    @Override
    public void setTestClassMethods(Map<Class<?>, Set<MethodNode>> testClassMethodMap) {
        this.testClassMethodMap = testClassMethodMap;
    }

    public void setPageAndMethods(Map<Class<?>, Set<String>> pageAndMethods) {
        this.pageAndMethods = pageAndMethods;
    }

    private Map<Class<?>, Set<String>> pageAndMethods;

    public void setPageAndElements(Map<Class<?>, Set<String>> pageAndElements) {
        this.pageAndElements = pageAndElements;
    }

    private Map<Class<?>, Set<String>> pageAndElements;

    private PageRules getPageRules() {
        return pageRules;
    }

    public void setPageRules(PageRules pageRules) {
        this.pageRules = pageRules;
    }

    private TestRules getTestRules() {
        return testRules;
    }

    public void setTestRules(TestRules testRules) {
        this.testRules = testRules;
    }

    @Override
    public void setPageClassNodes(Map<Class<?>, ClassNode> pageClassNodes) {
        this.pageClassNodeMap = pageClassNodes;
    }

    /**
     * mapping method for test classes
     * @param testClasses
     * @param testClassAndMethods
     * @return
     */
    @Override
    public Map<String, List<TestReport>> map(List<Class<?>> testClasses, Map<Class<?>, ClassNode> testClassNodes,
                                             Map<Class<?>, Set<MethodNode>> testClassAndMethods) {
        this.testClassNodes = testClassNodes;
        getPageAndTestClassNodeMaps();
        Map<String, List<TestReport>> testClassesReport = new HashMap<>();
        testClassAndMethods.entrySet().forEach(testClass -> {
            try {
                //creating a directory for test class
                graphWriter.init(testClass.getKey().getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
            testClassesReport.put(testClass.getKey().getName(),
                    map(testClass.getValue(), testClass.getKey().getName()));
        });
        return testClassesReport;
    }

    /**
     * mapping method for test class
     * @param testMethods
     * @return
     */
    private List<TestReport> map(Set<MethodNode> testMethods, String testClassName) {
        List<TestReport> testReports = new ArrayList<>();
        testMethods.forEach(testMethod -> {
            scanTestMethod(testMethod);
//            if(testReport != null) {
//                graphWriter.writeTestReport(testReport, testMethod.name, testClassName);
//            }
        });
        return testReports;
    }

    private void scanTestMethod(MethodNode testMethod) {
        String methodName = testMethod.name;
        System.out.println("Following are the page Elements used in test: "+ methodName+ " ----------");
        scanTheMethodInstructions(testMethod);
    }

    private void scanTheMethodVariables(MethodNode anyMethod) {
        for(LocalVariableNode localVariableNode: anyMethod.localVariables) {
            String description = localVariableNode.desc;
            String localVariableName = localVariableNode.name;
            if(!localVariableName.equals("this") && description.startsWith("L")) {
                description = description.replace(";","")
                        .substring(1, description.length()-1)
                        .replace("/",".");
                if(pageInformation.isSeleniumField(description)) {
                    System.out.println(localVariableName);
                }
            }
        }
    }

    private void scanTheMethodInstructions(MethodNode testMethod) {
        scanTheMethodVariables(testMethod);
        InsnList testMethodInstructions = testMethod.instructions;
        for(AbstractInsnNode instruction: testMethodInstructions.toArray()) {
            if(instruction.getType() == AbstractInsnNode.METHOD_INSN) {
                MethodInsnNode min = (MethodInsnNode)instruction;
                boolean isInitMethod = min.name.contains("<init>");
                if(!isInitMethod && canBeScanned(min)) {
                    MethodNode methodNode = getMethodNode(min);
                    if(methodNode != null) {
                        scanTheMethodInstructions(methodNode);
                    }
                    //add to this map when the required condition is met  --- ownerAndItsMethods
                }
            } else if(instruction.getType() == AbstractInsnNode.FIELD_INSN) {
                FieldInsnNode fieldInstruction = (FieldInsnNode) instruction;
                Map<String, String> seleniumElement = getPageElementEntry(fieldInstruction);
                if(seleniumElement != null) {
                    System.out.println(new Gson().toJson(seleniumElement));
                }
                //record the elements
            }
        }
    }

    /**
     * returns a map of single entry with field name as key and class name as value
     * @param fieldInstruction FieldInsnNode type parameter to be passed
     * @return
     */
    private Map<String, String> getPageElementEntry(FieldInsnNode fieldInstruction) {
        String fieldName = fieldInstruction.name;
        boolean isThisField = fieldName.equals("this");
        boolean isInitField = fieldName.equals("<init>()");
        Map<String, String> element = new HashMap<>();
        if(!isInitField && !isThisField) {
            Class<?> fieldsOwnerClass = classUtils.getClass(fieldInstruction.owner.replace("/", "."));
            Optional<Map.Entry<Class<?>, Set<String>>> optionalEntry =
                    pageAndElements.entrySet().stream()
                            .filter(x -> x.getKey().getName().equals(fieldsOwnerClass.getName()))
                            .findFirst();
            if(optionalEntry.isPresent()) {
                Map.Entry<Class<?>,Set<String>> entry = optionalEntry.get();
                element.put(fieldName, entry.getKey().getName());
                return element;
            }
        }
        return null;
    }

    //TODO fix the bug with super classes
    /**
     * returns method node by taking the MethodInsNode as an input
     * @param min
     * @return
     */
    private MethodNode getMethodNode(MethodInsnNode min){
        String methodOwner = min.owner.replace("/",".");
        String methodName = min.name;
        Class<?> methodOwnerClass = ClassUtils.getClass(methodOwner);
        Class<?> baseTestClass = ClassUtils.getClass(testRules.getBaseTestClass());
        Class<?> basePageClass = ClassUtils.getClass(pageRules.getBasePageClass());
        while(methodOwnerClass != Object.class &&
                (baseTestClass.isAssignableFrom(methodOwnerClass) ||
                basePageClass.isAssignableFrom(methodOwnerClass)) ) {
            Optional<Map.Entry<Class<?>, ClassNode>> classNodeMap = localNodeMap.entrySet().stream()
                    .filter(x -> x.getKey().getName().equals(methodOwner))
                    .findFirst();
            if (classNodeMap.isPresent()) {
                Map.Entry<Class<?>, ClassNode> entry = classNodeMap.get();
                Optional<MethodNode> optionalMethodNode = entry.getValue().methods.stream()
                        .filter(x -> x.name.equals(methodName)).findFirst();
                if (optionalMethodNode.isPresent()) {
                    return optionalMethodNode.get();
                } else {
                    //this may happen if the method owner is a super class.
                    // In this case we would need to iterate through each super class of methodOwner
                    methodOwnerClass = methodOwnerClass.getSuperclass();
                }
            }
        }
        return null;
    }

    private Map<Class<?>, ClassNode> getPageAndTestClassNodeMaps() {
        localNodeMap = new HashMap<>();
        localNodeMap.putAll(pageClassNodeMap);
        localNodeMap.putAll(testClassNodes);
        return localNodeMap;
    }
    private boolean canBeScanned(MethodInsnNode min) {
        String methodOwner = min.owner.replace("/",".");
        return (testClassMethodMap.entrySet().stream().anyMatch(x->x.getKey().getName().equals(methodOwner)) ||
                pageClassNodeMap.entrySet().stream().anyMatch(x->x.getKey().getName().equals(methodOwner)));
    }
}
