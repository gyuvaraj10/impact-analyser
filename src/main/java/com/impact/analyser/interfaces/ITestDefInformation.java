package com.impact.analyser.interfaces;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Yuvaraj on 14/03/2018.
 */
public interface ITestDefInformation {

    List<Class<?>> getTestClasses(String[] testsPackages);

    Map<Class<?>, ClassNode> getTestClassAndNode(String[] testsPackages);

    Map<Class<?>, ClassNode> getCucumberClassAndNode(String[] glue);

    List<Class<?>> getCucumberStepDefClasses(String[] glue);

    Set<MethodNode> getJUnitTests(Class<?> jUnitTestClass);

    Set<MethodNode> getTestNGTests(Class<?> testngTestClass);

    Map<Class<?>, Set<MethodNode>> getTestClassAndTestMethod(List<Class<?>> testClasses);

    Map<Class<?>, Set<MethodNode>> getStepDefClassAndMethod(List<Class<?>> steDefClasses);

}
