package com.impact.analyser.impl;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.impact.analyser.GraphWriter;
import com.impact.analyser.interfaces.IPageInformation;
import com.impact.analyser.interfaces.ITestDefInformation;
import com.impact.analyser.interfaces.ITestMapper;
import com.impact.analyser.report.FieldReport;
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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Yuvaraj on 18/03/2018.
 */
public class TestMapperUpdated  implements ITestMapper {


    @Inject
    private Logger logger;

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

    public void setPageRules(PageRules pageRules) {
        this.pageRules = pageRules;
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
     * @param testClassAndMethods
     * @return
     */
    @Override
    public void map(Map<Class<?>, ClassNode> testClassNodes,
                                             Map<Class<?>, Set<MethodNode>> testClassAndMethods) throws IOException {
        this.testClassNodes = testClassNodes;
        getPageAndTestClassNodeMaps();
        for(Map.Entry<Class<?>, Set<MethodNode>> entry: testClassAndMethods.entrySet()) {
            //creating a directory for test class
            graphWriter.init(entry.getKey().getName());
            map(entry.getValue(), entry.getKey().getName());
        }
    }

    /**
     * mapping method for test class
     * @param testMethods
     * @return
     */
    private List<TestReport> map(Set<MethodNode> testMethods, String testClassName) {
        List<TestReport> testReports = new ArrayList<>();
        testMethods.forEach(testMethod -> {
            TestReport testReport = new TestReport();
            testReport.setTestName(testMethod.name);
            testReport.setTestClassName(testClassName);
            testReport.setFieldReports(scanTestMethod(testMethod));
            graphWriter.writeTestReport(testReport, testMethod.name, testClassName);
        });
        return testReports;
    }

    private Set<FieldReport> scanTestMethod(MethodNode testMethod) {
        return scanTheMethodInstructions(testMethod);
    }

    private Set<FieldReport> scanTheMethodVariables(MethodNode anyMethod) {
        Set<FieldReport> fieldReports = new HashSet<>();
        for(LocalVariableNode localVariableNode: anyMethod.localVariables) {
            String description = localVariableNode.desc;
            String localVariableName = localVariableNode.name;
            if(!localVariableName.equals("this") && description.startsWith("L")) {
                description = description.replace(";","")
                        .substring(1, description.length()-1)
                        .replace("/",".");
                if(pageInformation.isSeleniumField(description)) {
                    logger.log(Level.INFO, localVariableName);
                    FieldReport fieldReport = new FieldReport();
                    fieldReport.setFieldName(localVariableName);
                    fieldReport.setFieldClass(""); // set the field's owner class Name
                    fieldReports.add(fieldReport);
                }
            }
        }
        return fieldReports;
    }

    private Set<FieldReport> scanTheMethodInstructions(MethodNode testMethod) {
        Set<FieldReport> fieldReports = new HashSet<>();
        fieldReports.addAll(scanTheMethodVariables(testMethod));
        InsnList testMethodInstructions = testMethod.instructions;
        for(AbstractInsnNode instruction: testMethodInstructions.toArray()) {
            if(instruction.getType() == AbstractInsnNode.METHOD_INSN) {
                MethodInsnNode min = (MethodInsnNode)instruction;
                boolean isInitMethod = min.name.contains("<init>");
                if(!isInitMethod && canBeScanned(min)) {
                    MethodNode methodNode = getMethodNode(min);
                    if(methodNode != null) {
                        fieldReports.addAll(scanTheMethodInstructions(methodNode));
                    }
                    //add to this map when the required condition is met  --- ownerAndItsMethods
                }
            } else if(instruction.getType() == AbstractInsnNode.FIELD_INSN) {
                FieldInsnNode fieldInstruction = (FieldInsnNode) instruction;
                Map<String, String> seleniumElement = getPageElementEntry(fieldInstruction);
                if(seleniumElement != null && !seleniumElement.isEmpty()) {
                    FieldReport fieldReport = new FieldReport();
                    for(Map.Entry<String, String> entrySet: seleniumElement.entrySet()){
                        fieldReport.setFieldName(entrySet.getKey());
                        fieldReport.setFieldClass(entrySet.getValue());
                    }
                    fieldReports.add(fieldReport);
                    logger.log(Level.INFO, new Gson().toJson(seleniumElement));
                }
            }
        }
        return fieldReports;
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

    /**
     * returns method node by taking the MethodInsNode as an input
     * @param min
     * @return
     */
    private MethodNode getMethodNode(MethodInsnNode min){
        String methodOwner = min.owner.replace("/",".");
        String methodName = min.name;
        String methodDescription = min.desc;
        Class<?> methodOwnerClass = classUtils.getClass(methodOwner);
        Class<?> baseTestClass = classUtils.getClass(testRules.getBaseTestClass());
        Class<?> basePageClass = classUtils.getClass(pageRules.getBasePageClass());
        while(methodOwnerClass != Object.class &&
                (baseTestClass.isAssignableFrom(methodOwnerClass) ||
                basePageClass.isAssignableFrom(methodOwnerClass)) ) {
            ClassNode classNode = null;
            for(Map.Entry<Class<?>, ClassNode> localNodeEntry: localNodeMap.entrySet()) {
                String className = localNodeEntry.getKey().getName();
                if(className.equals(methodOwner)) {
                    classNode = localNodeEntry.getValue();
                    break;
                }
            }
            if (classNode != null) {
                Optional<MethodNode> optionalMethodNode = classNode.methods.stream()
                        .filter(x -> x.name.equals(methodName) && x.desc.equals(methodDescription)).findFirst();
                if (optionalMethodNode.isPresent()) {
                    return optionalMethodNode.get();
                }
            }
            //this may happen if the method owner is a super class.
            // In this case we would need to iterate through each super class of methodOwner
            methodOwnerClass = methodOwnerClass.getSuperclass();
            methodOwner = methodOwnerClass.getName();
        }
        return null;
    }

    private Map<Class<?>, ClassNode> getPageAndTestClassNodeMaps() {
        localNodeMap = new HashMap<>();
        localNodeMap.putAll(pageClassNodeMap);
        localNodeMap.putAll(testClassNodes);
        return localNodeMap;
    }

    /**
     * verifies if the method is eligible for scanning by checking if the methods owner/class is from test classes
     * or page classes
     * @param min
     * @return
     */
    private boolean canBeScanned(MethodInsnNode min) {
        String methodOwner = min.owner.replace("/",".");
        return (testClassMethodMap.entrySet().stream().anyMatch(x->x.getKey().getName().equals(methodOwner)) ||
                pageClassNodeMap.entrySet().stream().anyMatch(x->x.getKey().getName().equals(methodOwner)));
    }
}
