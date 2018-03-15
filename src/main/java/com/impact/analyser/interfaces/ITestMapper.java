package com.impact.analyser.interfaces;

import com.impact.analyser.report.TestReport;
import org.objectweb.asm.tree.MethodNode;

import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Created by Yuvaraj on 15/03/2018.
 */
public interface ITestMapper {

    Map<String, List<TestReport>> map(List<Class<?>> testClasses, Map<Class<?>, Set<MethodNode>> testClassAndMethods,
                                      Set<Class<?>> pageClasses,
                                      Map<Class<?>, Set<String>> pageAndElements,
                                      Map<Class<?>, Set<String>> pageAndMethods);
}
