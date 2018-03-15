package com.impact.analyser.interfaces;

import org.objectweb.asm.tree.MethodNode;

import java.util.List;
import java.util.Set;

/**
 * Created by Yuvaraj on 14/03/2018.
 */
public interface ITestDefInformation {

    List<Class<?>> getTestClasses(String[] testsPackages);

    Set<MethodNode> getJUnitTests(Class<?> jUnitTestClass);

    Set<MethodNode> getTestNGTests(Class<?> testngTestClass);
}
