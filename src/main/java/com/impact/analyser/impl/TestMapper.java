package com.impact.analyser.impl;

import com.google.inject.Inject;
import com.impact.analyser.interfaces.IPageInformation;
import com.impact.analyser.interfaces.ITestMapper;
import com.impact.analyser.report.MethodInfo;
import com.impact.analyser.report.PageInfo;
import com.impact.analyser.report.TestReport;
import com.impact.analyser.rules.PageRules;
import com.impact.analyser.rules.TestRules;
import com.impact.analyser.utils.ClassUtils;
import org.objectweb.asm.tree.*;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

/**
 * Created by Yuvaraj on 15/03/2018.
 */
public class TestMapper implements ITestMapper {

    private static final Logger logger = Logger.getLogger(TestMapper.class.getName());

    @Inject
    private static ClassUtils classUtils;

    @Inject
    private IPageInformation pageInformation;

    private PageRules pageRules;
    private TestRules testRules;

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

    /**
     * mapping method for test classes
     * @param testClasses
     * @param testClassAndMethods
     * @param pageClasses
     * @param pageAndElements
     * @param pageAndMethods
     * @return
     */
    @Override
    public Map<String, List<TestReport>> map(List<Class<?>> testClasses, Map<Class<?>, Set<MethodNode>> testClassAndMethods,
                                             Set<Class<?>> pageClasses,
                                             Map<Class<?>, Set<String>> pageAndElements,
                                             Map<Class<?>, Set<String>> pageAndMethods) {
        Map<String, List<TestReport>> testClassesReport = new HashMap<>();
        testClassAndMethods.entrySet().forEach(testClass -> {
            testClassesReport.put(testClass.getKey().getName(),
                    map(testClass.getValue(), pageAndElements));
        });
        return testClassesReport;
    }

    /**
     * mapping method for test class
     * @param testMethods
     * @param pageAndElements
     * @return
     */
    private List<TestReport> map(Set<MethodNode> testMethods, Map<Class<?>, Set<String>> pageAndElements) {
        List<TestReport> testReports = new ArrayList<>();
        testMethods.forEach(testMethod -> {
            TestReport testReport = map(testMethod, pageAndElements);
            if(testReport != null) {
                testReports.add(testReport);
            }
        });
        return testReports;
    }

    /**
     * mapping method for test method
     * @param testMethod
     * @param pageAndElements
     * @return
     */
    private TestReport map(MethodNode testMethod, Map<Class<?>, Set<String>> pageAndElements) {
        String methodName = testMethod.name;
        logger.log(Level.INFO, "collecting the test report from test method: "+ methodName);
        TestReport testReport = new TestReport();
        for(AbstractInsnNode abstractInsnNode: testMethod.instructions.toArray()) {
            if (abstractInsnNode.getType() == AbstractInsnNode.FIELD_INSN) {
                FieldInsnNode tin = (FieldInsnNode) abstractInsnNode;
                // ignoring the fields that are directly called in test methods, but logging them in the log files
                logger.info(methodName + " test has "+ tin.name +" field with type " + tin.desc);
            }
            if(abstractInsnNode.getType() == AbstractInsnNode.METHOD_INSN) {
                MethodInsnNode min = (MethodInsnNode)abstractInsnNode;

                if(pageRules.getPageClassPackages().stream().anyMatch(x->min.owner.replace("/",".").startsWith(x))) {
                    Set<MethodInfo> pageMethods = expediteThroughMethodNodes(min, pageAndElements);
                    Set<MethodInfo> reportMethodInfos = testReport.getMethodInfos();
                    if(pageMethods.size() !=0) {
                        testReport.setTestName(methodName);
                        if (reportMethodInfos == null || reportMethodInfos.size() == 0) {
                            testReport.setMethodInfos(pageMethods);
                        } else {
                            reportMethodInfos.addAll(pageMethods);
                        }
                    }
                }
            }
        }
        if(testReport.getMethodInfos() == null && testReport.getMethodInfos().size() ==0){
             return null;
        } else {
            //code to remove duplicate methods
            Set<MethodInfo> removedDuplicates = new HashSet<>();
            for(MethodInfo m: testReport.getMethodInfos()) {
             removedDuplicates.add(m);
            }
            testReport.setMethodInfos(removedDuplicates);
            return testReport;
        }
    }

    private Set<MethodInfo> expediteThroughMethodNodes( MethodInsnNode min, Map<Class<?>, Set<String>> pageAndElements) {
        String methodName = min.name;
        Set<MethodInfo> classMethodInfoSet = new HashSet<>();
        if (!methodName.contains("<init>")) {
            String methodOwner = min.owner.replace("/", ".");
            Class<?> ownerClass = classUtils.getClass(methodOwner);
            Class<?> superClass = ownerClass;
            while(superClass != Object.class) {
                String superClassName = superClass.getName();
                ClassNode classNode = classUtils.getClassNode(superClass);
                Optional<MethodNode> pageMethodNodeOpt = classNode.methods.stream().filter(x -> x.name.equals(methodName)).findFirst();
                if(pageMethodNodeOpt.isPresent()) {
                    MethodNode methodNode = pageMethodNodeOpt.get();
                    for (LocalVariableNode localVariableNode : methodNode.localVariables) {
                        String fieldName = localVariableNode.name;
                        if(!fieldName.equals("this") && localVariableNode.desc.startsWith("L")) {
                            String type = localVariableNode.desc.substring(1, localVariableNode.desc.length()-1).replace("/",".");
                            if(pageInformation.isSeleniumField(type)) {
                                Optional<MethodInfo> optional =classMethodInfoSet.stream()
                                        .filter(x->x.getMethodName().equals(methodName) && x.getMethodClass().equals(methodOwner))
                                        .findFirst();
                                if(optional.isPresent()){
                                    optional.get().getFieldAndFieldClassName().put(fieldName, superClass.getName());
                                } else {
                                    MethodInfo methodInfo = new MethodInfo();
                                    methodInfo.setMethodName(methodName);
                                    methodInfo.setMethodClass(methodOwner);
                                    Map<String, String> fieldAndClass = new HashMap<>();
                                    fieldAndClass.put(fieldName,superClass.getName());
                                    methodInfo.setFieldAndFieldClassName(fieldAndClass);
                                    methodInfo.setPageItem(true);
                                    classMethodInfoSet.add(methodInfo);
                                }
                            }
                        }
                    }
                    for (AbstractInsnNode minN : methodNode.instructions.toArray()) {
                        if (minN.getType() == AbstractInsnNode.METHOD_INSN) {
                            MethodInsnNode mi = (MethodInsnNode)minN;
                            String owner  = mi.owner.replace("/",".");
                            if(pageRules.getPageClassPackages().stream().anyMatch(x->owner.startsWith(x))
                                    || testRules.getTestClassPackages().stream().anyMatch(x->owner.startsWith(x))) {
                                Set<MethodInfo> childMethods = expediteThroughMethodNodes(mi, pageAndElements);
                                for(MethodInfo minfo: childMethods) {
                                    // do not duplicate methods to the set
                                    if(!classMethodInfoSet.stream().anyMatch(x->x.getMethodName().equals(minfo.getMethodName())
                                            && x.getMethodClass().equals(minfo.getMethodClass()))) {
                                        classMethodInfoSet.add(minfo);
                                    }
                                }
                            }
                        } else if (minN.getType() == AbstractInsnNode.FIELD_INSN) {
                            FieldInsnNode tin = (FieldInsnNode) minN;
                            if (!tin.name.equals("this") && !tin.name.equals("<init>()")) {
                                Class<?> fieldType = classUtils.getClass(tin.desc.substring(1, tin.desc.length() - 1).replace("/", "."));
                                Class<?> fieldsOwnerClass = classUtils.getClass(tin.owner.replace("/", "."));
                                Optional<Map.Entry<Class<?>, Set<String>>> pageElementMap = pageAndElements.entrySet()
                                        .stream().filter(x -> x.getKey().getName().equals(fieldsOwnerClass.getName())
                                                && x.getValue().contains(tin.name)).findFirst();
                                if (fieldType != null && fieldsOwnerClass != null && pageElementMap.isPresent()) {
                                    logger.info("Identified an element " + tin.name + " of type " + tin.desc + " from " + tin.owner);
                                    Optional<MethodInfo> optional =classMethodInfoSet.stream().filter(x->x.getMethodName().equals(methodName)
                                            && x.getMethodClass().equals(methodOwner))
                                            .findFirst();
                                    if(optional.isPresent()){
                                        optional.get().getFieldAndFieldClassName().put(tin.name, fieldsOwnerClass.getName());
                                    } else {
                                        MethodInfo methodInfo = new MethodInfo();
                                        methodInfo.setMethodName(methodName);
                                        methodInfo.setMethodClass(methodOwner);
                                        Map<String, String> fieldAndClass = new HashMap<>();
                                        fieldAndClass.put(tin.name,fieldsOwnerClass.getName());
                                        methodInfo.setFieldAndFieldClassName(fieldAndClass);
                                        methodInfo.setPageItem(true);
                                        classMethodInfoSet.add(methodInfo);
                                    }
                                } else {
                                    logger.info("Ignoring this element " + tin.name + " " + tin.desc);
                                }
                            }
                        }
                    }
                }
                superClass = superClass.getSuperclass();
            }
        }
        return classMethodInfoSet;
    }
}
