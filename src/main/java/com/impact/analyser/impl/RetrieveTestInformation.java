package com.impact.analyser.impl;

import com.google.inject.Inject;
import com.impact.analyser.utils.ClassUtils;
import com.impact.analyser.interfaces.ITestDefInformation;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.objectweb.asm.Type.getInternalName;

/**
 * Created by Yuvaraj on 14/03/2018.
 */
public class RetrieveTestInformation implements ITestDefInformation {

    private static final Logger logger = Logger.getLogger(RetrieveTestInformation.class.getName());

    @Inject
    private static ClassUtils classUtils;

    /**
     * returns test class and test methods map
     * @param testClasses
     * @return
     */
    @Override
    public Map<Class<?>, Set<MethodNode>> getTestClassAndTestMethod(List<Class<?>> testClasses) {
        Map<Class<?>, Set<MethodNode>> classMethodMap = new HashMap<>();
        testClasses.forEach(x -> {
            Set<MethodNode> methodNodes = getTestNGTests(x);
            classMethodMap.put(x, methodNodes);
            logger.log(Level.INFO, "Collected {0} Methods from test class {1}", new Object[]{methodNodes.size(), testClasses});
        });
        return classMethodMap;
    }

    /**
     * Collect All the Test Classes
     * @param testsPackages
     * @return
     */
    @Override
    public List<Class<?>> getTestClasses(String[] testsPackages) {
        List<Class<?>> testClasses = new ArrayList<>();
        for(String testPackage: testsPackages) {
            getAllTestTypesInPackages(Collections.singletonList(testPackage)).forEach(type->{
                if(isTestClass(type)) {
                    logger.log(Level.INFO, "Found a Test Class {0}", type.getName());
                    testClasses.add(type);
                }
            });
        }
        return testClasses;
    }


    @Override
    public Set<MethodNode> getJUnitTests(Class<?> testClass)  {
        Set<MethodNode> methodNodeList = new HashSet<>();
        ClassReader classR = classUtils.getClassReader(testClass);
        ClassNode classNode = new ClassNode();
        if(classR != null) {
            classR.accept(classNode, 0);
            for (MethodNode methodNode : classNode.methods) {
                if (methodNode.visibleAnnotations != null && methodNode.visibleAnnotations.size() > 0) {
                    Optional opt = methodNode.visibleAnnotations.stream()
                            .filter(x -> classUtils.getClass(x.desc.replace("/", ".")
                                    .replace(";", "").replace("L", ""))
                                    .isAssignableFrom(classUtils.getClass("org.junit.Test")))
                            .findFirst();
                    if (opt != null && opt.isPresent()) {
                        methodNodeList.add(methodNode);
                    }
                }
            }
        }
        return methodNodeList;
    }

    @Override
    public Set<MethodNode> getTestNGTests(Class<?> testClass) {
        ClassReader classR = null;
        try {
            classR = new ClassReader(getInternalName(testClass));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Could not load class {0}", testClass.getName());
            logger.log(Level.FINEST, e.getMessage());
        }
        ClassNode classNode = new ClassNode();
        classR.accept(classNode,0);
        Set<MethodNode> methodNodeList = new HashSet<>();
        for(MethodNode methodNode: classNode.methods) {
            if (methodNode.visibleAnnotations != null && methodNode.visibleAnnotations.size() > 0) {
                Optional opt = methodNode.visibleAnnotations.stream()
                        .filter(x -> classUtils.getClass(x.desc.replace("/", ".")
                                .replace(";", "").replace("L", ""))
                                .isAssignableFrom(classUtils.getClass("org.testng.annotations.Test")))
                        .findFirst();
                if (opt != null && opt.isPresent()) {
                    methodNodeList.add(methodNode);
                }
            }
        }
        return methodNodeList;
    }

    /**
     * verifies if a class is of type Junit test class or TestNG test class
     * @param testClass
     * @return
     */
    public boolean isTestClass(Class<?> testClass) {
        Class<? extends Annotation> jUnitAnno = classUtils.getAnnotationClass("org.junit.Test");
        Class<? extends Annotation> testNGAnnot = classUtils.getAnnotationClass("org.testng.annotations.Test");
        Annotation jUnitANno = null, testNGAnno = null;
        boolean jUnitTestMethodExists = false;
        boolean testNGTestMethodExists = false;
        if(jUnitAnno != null) {
            jUnitANno = testClass.getDeclaredAnnotation(jUnitAnno);
            jUnitTestMethodExists = Arrays.stream(testClass.getDeclaredMethods())
                    .anyMatch(x->(x.getDeclaredAnnotation(jUnitAnno)!=null));
        }
        if(testNGAnnot != null) {
            testNGAnno = testClass.getDeclaredAnnotation(testNGAnnot);
            testNGTestMethodExists = Arrays.stream(testClass.getDeclaredMethods())
                    .anyMatch(x->(x.getDeclaredAnnotation(testNGAnnot)!=null));
        }

        return jUnitANno != null || testNGAnno != null || jUnitTestMethodExists || testNGTestMethodExists;
    }

    /**
     * returns all the test classes as a set from the specified packages
     * @param packages
     * @return set of test classes
     */
    private Set<Class<?>> getAllTestTypesInPackages(List<String> packages) {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setScanners(new MethodAnnotationsScanner())
                .forPackages(packages.toArray(new String[]{})));
        Class<? extends Annotation> jUnitAnno = classUtils.getAnnotationClass("org.junit.Test");
        Class<? extends Annotation> testNGAnno = classUtils.getAnnotationClass("org.testng.annotations.Test");
        Set<Method> allMethods = new HashSet<>();
        if(jUnitAnno != null) {
            allMethods.addAll(reflections.getMethodsAnnotatedWith(jUnitAnno));
        }
        if(testNGAnno != null) {
            allMethods.addAll(reflections.getMethodsAnnotatedWith(testNGAnno));
        }
        Set<Class<?>> testClasses = new HashSet<>();
        for(Method m: allMethods) {
            testClasses.add(m.getDeclaringClass());
        }
        return testClasses.stream().filter(x->packages.stream().anyMatch(x.getName()::startsWith)).collect(Collectors.toSet());
    }
}
