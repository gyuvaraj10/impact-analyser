package com.impact.analyser.impl;

import com.google.inject.Inject;
import com.impact.analyser.interfaces.IPageInformation;
import com.impact.analyser.interfaces.ITestDefInformation;
import com.impact.analyser.interfaces.ITestMapper;
import com.impact.analyser.report.MethodInfo;
import com.impact.analyser.report.PageInfo;
import com.impact.analyser.report.TestReport;
import com.impact.analyser.rules.PageRules;
import com.impact.analyser.rules.TestRules;
import com.impact.analyser.utils.ClassUtils;
import org.objectweb.asm.tree.*;

import java.util.*;
/**
 * Created by Yuvaraj on 15/03/2018.
 */
public class TestMapper implements ITestMapper {

//    private static final Logger logger = Logger.getLogger(TestMapper.class.getName());

    @Inject
    private static ClassUtils classUtils;

    @Inject
    private IPageInformation pageInformation;

    @Inject
    private ITestDefInformation testInformation;

    private PageRules pageRules;
    private TestRules testRules;
    private Map<Class<?>, ClassNode> pageClassNodeMap;

    public void setPageClasses(Set<Class<?>> pageClasses) {
        this.pageClasses = pageClasses;
    }

    Set<Class<?>> pageClasses;

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

    private Map<Class<?>, ClassNode> testClassNodes;
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
        Map<String, List<TestReport>> testClassesReport = new HashMap<>();
        testClassAndMethods.entrySet().forEach(testClass -> {
            testClassesReport.put(testClass.getKey().getName(),
                    map(testClass.getValue()));
        });
        return testClassesReport;
    }

    /**
     * mapping method for test class
     * @param testMethods
     * @return
     */
    private List<TestReport> map(Set<MethodNode> testMethods) {
        List<TestReport> testReports = new ArrayList<>();
        testMethods.forEach(testMethod -> {
            TestReport testReport = map(testMethod);
            if(testReport != null) {
                testReports.add(testReport);
            }
        });
        return testReports;
    }

    /**
     * mapping method for test method
     * @param testMethod
     * @return
     */
    private TestReport map(MethodNode testMethod) {
        String methodName = testMethod.name;
//        logger.log(Level.INFO, "collecting the test report from test method: "+ methodName);
        TestReport testReport = new TestReport();
        for(AbstractInsnNode abstractInsnNode: testMethod.instructions.toArray()) {
            if (abstractInsnNode.getType() == AbstractInsnNode.FIELD_INSN) {
                FieldInsnNode tin = (FieldInsnNode) abstractInsnNode;
                // ignoring the fields that are directly called in test methods, but logging them in the log files
//                logger.info(methodName + " test has "+ tin.name +" field with type " + tin.desc);
            }
            if(abstractInsnNode.getType() == AbstractInsnNode.METHOD_INSN) {
                MethodInsnNode min = (MethodInsnNode)abstractInsnNode;
                if(pageRules.getPageClassPackages().stream().anyMatch(x->min.owner.replace("/",".").startsWith(x)) ||
                        testRules.getTestClassPackages().stream().anyMatch(x->min.owner.replace("/",".").startsWith(x))) {
                    Set<MethodInfo> pageMethods = expediteThroughMethodNodes(min);
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

    private Set<MethodInfo> expediteThroughMethodNodes(MethodInsnNode min) {
        String methodName = min.name;
        Set<MethodInfo> classMethodInfoSet = new HashSet<>();
        if (!methodName.contains("<init>")) {
            String methodOwner = min.owner.replace("/", ".");
            Class<?> ownerClass = classUtils.getClass(methodOwner);
            Class<?> superClass = ownerClass;
            String superClassName = superClass.getName();
            Optional<Map.Entry<Class<?>, ClassNode>> classNodeMap = pageClassNodeMap.entrySet().stream()
                    .filter(x->x.getKey().getName().equals(ownerClass.getName())).findFirst();
            ClassNode node = null;
            if(classNodeMap.isPresent()) {
                node =  classNodeMap.get().getValue();
            }
            ClassNode classNode = null;
            while(superClass != Object.class) {
                Optional<Map.Entry<Class<?>, ClassNode>> mapCN = testClassNodes.entrySet().stream()
                        .filter(c->c.getKey().getName().equals(superClassName)).findFirst();
                if(node!= null && node.methods.stream().anyMatch(x -> x.name.equals(methodName))) {
                    classNode = node;
                } else if(mapCN.isPresent()) {
                    classNode = mapCN.get().getValue();
                }
                if(classNode != null) {
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
                                if(verifyMethodIsAPageMethod(mi) || verifyMethodIsATestMethod(mi)) {
                                    Set<MethodInfo> childMethods = expediteThroughMethodNodes(mi);
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
                                    try {
                                        String fieldItem = tin.desc.substring(1, tin.desc.length() - 1).replace("/", ".");
                                        Class<?> fieldType = classUtils.getClass(fieldItem);
                                        Class<?> fieldsOwnerClass = classUtils.getClass(tin.owner.replace("/", "."));
                                        Optional<Map.Entry<Class<?>, Set<String>>> pageElementMap = pageAndElements.entrySet()
                                                .stream().filter(x -> x.getKey().getName().equals(fieldsOwnerClass.getName())
                                                        && x.getValue().contains(tin.name)).findFirst();
                                        if (fieldType != null && fieldsOwnerClass != null && pageElementMap.isPresent()) {
//                                            logger.info("Identified an element " + tin.name + " of type " + tin.desc + " from " + tin.owner);
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
//                                            logger.info("Ignoring this element " + tin.name + " " + tin.desc);
                                        }
                                    } catch (IndexOutOfBoundsException ex) {
//                                        logger.severe("Exception for : "+tin.desc+" :" +ex.getMessage());
                                    }
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


    private boolean verifyMethodIsAPageMethod(MethodInsnNode min) {
        return (pageAndMethods.entrySet().stream().anyMatch(x->min.owner.replace("/",".").equals(x.getKey().getName()))&&
                pageAndMethods.entrySet().stream().anyMatch(x->x.getValue().contains(min.name)));
    }

    private boolean verifyMethodIsATestMethod(MethodInsnNode min) {
        return testRules.getTestClassPackages().stream().anyMatch(x->min.owner.replace("/",".").startsWith(x));
    }
}
