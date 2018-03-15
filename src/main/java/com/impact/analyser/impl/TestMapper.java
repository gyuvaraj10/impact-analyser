package com.impact.analyser.impl;

import com.google.inject.Inject;
import com.impact.analyser.interfaces.ITestMapper;
import com.impact.analyser.report.MethodInfo;
import com.impact.analyser.report.PageInfo;
import com.impact.analyser.report.TestReport;
import com.impact.analyser.utils.ClassUtils;
import org.objectweb.asm.tree.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Yuvaraj on 15/03/2018.
 */
public class TestMapper implements ITestMapper {

    private static final Logger logger = Logger.getLogger(TestMapper.class.getName());

    @Inject
    private static ClassUtils classUtils;

    @Override
    public Map<String, List<TestReport>> map(List<Class<?>> testClasses, Map<Class<?>, Set<MethodNode>> testClassAndMethods,
                                             Set<Class<?>> pageClasses,
                                             Map<Class<?>, Set<String>> pageAndElements,
                                             Map<Class<?>, Set<String>> pageAndMethods) {
        Map<String, List<TestReport>> testClassesReport = new HashMap<>();
        testClassAndMethods.entrySet().forEach(testClass -> {
            testClassesReport.put(testClass.getKey().getName(),
                    map(testClasses, testClass.getValue(), pageClasses, pageAndElements, pageAndMethods));
        });
        return testClassesReport;
    }

    private List<TestReport> map(List<Class<?>> testClasses, Set<MethodNode> testMethods, Set<Class<?>> pageClasses,
                                Map<Class<?>, Set<String>> pageAndElements,
                                Map<Class<?>, Set<String>> pageAndMethods) {
        List<TestReport> testReports = new ArrayList<>();
        testMethods.forEach(testMethod -> {
            testReports.add(map(testClasses, testMethod, pageClasses, pageAndElements, pageAndMethods));
        });

        return testReports;
    }

    private TestReport map(List<Class<?>> testClasses,MethodNode testMethod, Set<Class<?>> pageClasses,
                           Map<Class<?>, Set<String>> pageAndElements,
                           Map<Class<?>, Set<String>> pageAndMethods) {
        logger.log(Level.INFO, "collecting the test report from test method: "+ testMethod.name);
        TestReport testReport = new TestReport();
        testReport.setTestName(testMethod.name);
        List<PageInfo> pageReportList = new ArrayList<>();
        for(AbstractInsnNode abstractInsnNode: testMethod.instructions.toArray()) {
            PageInfo pageInfo = new PageInfo();
            Set<MethodInfo> methodInfos = new HashSet<>();
            if (abstractInsnNode.getType() == AbstractInsnNode.TYPE_INSN) {
                TypeInsnNode tin = (TypeInsnNode) abstractInsnNode;
                Class<?> classFromTestMethod = classUtils.getClass(tin.desc.replace("/", "."));
                logger.info("Type Ins Node is: "+ classFromTestMethod.getName());
            }
            if(abstractInsnNode.getType() == AbstractInsnNode.METHOD_INSN) {
                MethodInsnNode min = (MethodInsnNode)abstractInsnNode;
                //expidate through the method nodes
                expediteThroughMethodNodes(min, testClasses, pageAndMethods, pageInfo, methodInfos);
            }
            if(pageInfo.getPageName() != null) {
                pageReportList.add(pageInfo);

            }
        }
        testReport.setPages(pageReportList);
        return testReport;
    }

    private void expediteThroughMethodNodes(MethodInsnNode min, List<Class<?>> testClasses,
                                            Map<Class<?>, Set<String>> pageAndMethods,
                                            PageInfo pageInfo, Set<MethodInfo> methodInfos) {
        String methodName =min.name;
        MethodInfo methodInfo = new MethodInfo();
        if(!methodName.contains("<init>")) {
            String methodOwner = min.owner.replace("/", ".");
            Class<?> ownerClass = classUtils.getClass(methodOwner);
            //if the owner of the method is the test class then we must identify the base classes that contain this method
            if(testClasses.contains(ownerClass)) {
                //identifying the base classes that contain this method
                Class<?> superClass = ownerClass;
                while(superClass != Object.class) {
                    superClass = superClass.getSuperclass();
                    if(pageAndMethods.containsKey(superClass)) {
                        Set<String> methods = pageAndMethods.get(superClass);
                        if(methods != null && methods.contains(methodName)) {
                            methodInfo.setMethodName(methodName);
                            pageInfo.setPageName(superClass.getName());
                        }
                    }
                }
            } else if(pageAndMethods.containsKey(ownerClass)) {
                //collect method info otherwise
                Set<String> methods = pageAndMethods.get(ownerClass);
                if(methods.contains(methodName)) {
                    ClassNode classNode = classUtils.getClassNode(ownerClass);
                    methodInfo.setMethodName(methodName);
                    pageInfo.setPageName(ownerClass.getName());
                    if(classNode != null) {
                        Optional <MethodNode> pageMethodNodeOpt = classNode.methods.stream().filter(x->x.name.equals(methodName)).findFirst();
                        if(pageMethodNodeOpt.isPresent()) {
                            MethodNode methodNode = pageMethodNodeOpt.get();
                            for(AbstractInsnNode minN: methodNode.instructions.toArray()) {
                                if(minN.getType() == AbstractInsnNode.METHOD_INSN) {
                                    expediteThroughMethodNodes((MethodInsnNode)minN, testClasses, pageAndMethods, pageInfo, methodInfos);
                                }
                            }
                        }
                    }
                }
            }

        }
        if(methodInfo.getMethodName() != null) {
            methodInfos.add(methodInfo);
            pageInfo.setMethodReportList(methodInfos);
        }
    }
}
