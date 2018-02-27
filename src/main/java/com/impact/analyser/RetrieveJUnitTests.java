package com.impact.analyser;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.objectweb.asm.Type.getInternalName;

/**
 * Created by Yuvaraj on 27/02/2018.
 */
public class RetrieveJUnitTests {

    public List<MethodNode> getJUnitTests(Class<?> testClass) throws Exception {
        ClassReader classR = new ClassReader(getInternalName(testClass));
        ClassNode classNode = new ClassNode();
        classR.accept(classNode,0);
        RetrievePageNames retrievePageNames = new RetrievePageNames();
        List<MethodNode> methodNodeList = new ArrayList<>();
        for(MethodNode methodNode: classNode.methods) {
            if (methodNode.visibleAnnotations != null && methodNode.visibleAnnotations.size() > 0) {
                Optional opt = methodNode.visibleAnnotations.stream()
                        .filter(x -> retrievePageNames.getClass(x.desc.replace("/", ".")
                                .replace(";", "").replace("L", ""))
                                .isAssignableFrom(org.junit.Test.class))
                        .findFirst();
                if (opt != null && opt.isPresent()) {
                    methodNodeList.add(methodNode);
                }
            }
        }
        return methodNodeList;
    }

    public List<MethodNode> getTestNGTests(Class<?> testClass) throws Exception {
        ClassReader classR = new ClassReader(getInternalName(testClass));
        ClassNode classNode = new ClassNode();
        classR.accept(classNode,0);
        RetrievePageNames retrievePageNames = new RetrievePageNames();
        List<MethodNode> methodNodeList = new ArrayList<>();
        for(MethodNode methodNode: classNode.methods) {
            if (methodNode.visibleAnnotations != null && methodNode.visibleAnnotations.size() > 0) {
                Optional opt = methodNode.visibleAnnotations.stream()
                        .filter(x -> retrievePageNames.getClass(x.desc.replace("/", ".")
                                .replace(";", "").replace("L", ""))
                                .isAssignableFrom(org.testng.annotations.Test.class))
                        .findFirst();
                if (opt != null && opt.isPresent()) {
                    methodNodeList.add(methodNode);
                }
            }
        }
        return methodNodeList;
    }
}
