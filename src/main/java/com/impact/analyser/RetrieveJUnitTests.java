package com.impact.analyser;

import com.impact.analyser.rules.PageRules;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.*;

import static org.objectweb.asm.Type.getInternalName;

/**
 * Created by Yuvaraj on 27/02/2018.
 */
public class RetrieveJUnitTests {

    private final PageRules pageRules;

    public RetrieveJUnitTests(PageRules pageRules) {
        this.pageRules = pageRules;
    }

    public List<MethodNode> getJUnitTests(Class<?> testClass) throws Exception {
        List<MethodNode> methodNodeList = new ArrayList<>();
        if(pageRules.isStandardDefinition()) {
            ClassReader classR = new ClassReader(getInternalName(testClass));
            ClassNode classNode = new ClassNode();
            classR.accept(classNode, 0);
            RetrievePageNames retrievePageNames = new RetrievePageNames();
            for (MethodNode methodNode : classNode.methods) {
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
        }
        return methodNodeList;
    }

    public Set<MethodNode> getTestNGTests(Class<?> testClass) throws Exception {
        ClassReader classR = new ClassReader(getInternalName(testClass));
        ClassNode classNode = new ClassNode();
        classR.accept(classNode,0);
        RetrievePageNames retrievePageNames = new RetrievePageNames();
        Set<MethodNode> methodNodeList = new HashSet<>();
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
