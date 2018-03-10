package com.impact.analyser;

import com.impact.analyser.report.CucumberStepDef;
import com.impact.analyser.rules.PageRules;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.*;
import org.openqa.selenium.WebElement;

import java.io.IOException;
import java.util.*;

import static org.objectweb.asm.Type.getInternalName;

/**
 * Created by Yuvaraj on 27/02/2018.
 */
public class RetrievePageNames {


    public Map<String, Set<String>> getPagesAndMethods(PageRules pageRules, MethodNode testMethodNode) {
        Map<String, Set<String>> pageNames = getPagesUsedInTest(pageRules, testMethodNode);
        for (AbstractInsnNode ain : testMethodNode.instructions.toArray()) {
            if (ain.getType() == AbstractInsnNode.METHOD_INSN) {
                MethodInsnNode min = (MethodInsnNode) ain;
                String methodName = min.name;
                if(methodName.equals("<init>")) {
                    continue;
                }
                String methodOwner = min.owner.replace("/", ".");
                Optional<String> optional = pageNames.keySet().stream().filter(x->x.equalsIgnoreCase(methodOwner)).findFirst();
                if(optional.isPresent()) {
                    String pageName = optional.get();
                    Set<String> pageKey = pageNames.get(pageName);
                    pageKey.add(methodName);
                }
            }
        }
        return pageNames;
    }


    public Map<String, Set<String>> getPagesAndMethods(MethodNode testMethodNode) throws Exception {
        Map<String, Set<String>> pageNames = getPagesUsedInTest(testMethodNode);
        for (AbstractInsnNode ain : testMethodNode.instructions.toArray()) {
            if (ain.getType() == AbstractInsnNode.METHOD_INSN) {
                MethodInsnNode min = (MethodInsnNode) ain;
                String methodName = min.name;
                if(methodName.equals("<init>")) {
                    continue;
                }
                String methodOwner = min.owner.replace("/", ".");
                Optional<String> optional = pageNames.keySet().stream().filter(x->x.equalsIgnoreCase(methodOwner)).findFirst();
                if(optional.isPresent()) {
                    String pageName = optional.get();
                    Set<String> pageKey = pageNames.get(pageName);
                    pageKey.add(methodName);
                }
            }
        }
        return pageNames;
    }

    public Map<String, Set<String>> getPagesUsedInTest(MethodNode testMethodNode) {
        Map<String, Set<String>> pageClasses = new HashMap<>();
        for (AbstractInsnNode ain : testMethodNode.instructions.toArray()) {
            if (ain.getType() == AbstractInsnNode.TYPE_INSN) {
                TypeInsnNode fin = (TypeInsnNode) ain;
                try {
                    Class<?> classFromTestMethod = Class.forName(fin.desc.replace("/", "."));
                    ClassNode classNodeFromTestMethod = getClassNode(classFromTestMethod);
                    List<FieldNode> fieldNodes = classNodeFromTestMethod.fields;
                    Optional optional = fieldNodes.stream().filter(x->getClass(x.desc
                            .replace("/",".").replace("L","").replace(";",""))
                            .isAssignableFrom(WebElement.class)).findFirst();
                    if(optional.isPresent()) {
                        pageClasses.put(classFromTestMethod.getName(), new HashSet<>());
                    }

                } catch (ClassCastException ex) {
                    System.out.println(ex);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return pageClasses;
    }

    private Map<String, Set<String>> getPagesUsedInTest(PageRules pageRules, MethodNode testMethodNode) {
        Class<?> basePageClass = ClassUtils.getClass(pageRules.getBasePageClass());
        Map<String, Set<String>> pageClasses = new HashMap<>();
        for (AbstractInsnNode ain : testMethodNode.instructions.toArray()) {
            if (ain.getType() == AbstractInsnNode.TYPE_INSN) {
                TypeInsnNode fin = (TypeInsnNode) ain;
                try {
                    Class<?> classFromTestMethod = Class.forName(fin.desc.replace("/", "."));
                    Class<?> superClass = classFromTestMethod.getSuperclass();
                    if(superClass!= null) {
                        if (superClass.isAssignableFrom(basePageClass)) {
                            pageClasses.put(classFromTestMethod.getName(), new HashSet<>());
                        }
                    }
                } catch (ClassCastException ex) {
                    System.out.println(ex);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } else if(ain.getType() == AbstractInsnNode.METHOD_INSN) {
                MethodInsnNode min = (MethodInsnNode)ain;
                try {
                    Class<?> classFromTestMethod = Class.forName(min.owner.replace("/", "."));
                    Class<?> superClass = classFromTestMethod.getSuperclass();
                    if(superClass!= null && superClass != Object.class) {
                        if (superClass.isAssignableFrom(basePageClass)) {
                            pageClasses.put(classFromTestMethod.getName(), new HashSet<>());
                        }
                    }
                } catch (ClassCastException ex) {

                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return pageClasses;
    }

    public Class<?> getClass(String fullyClass) {
        try {
            return Class.forName(fullyClass);
        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
        }
        return Class.class;
    }

    public ClassNode getClassNode(Class<?> classClass) {
        ClassReader classR = null;
        try {
            classR = new ClassReader(getInternalName(classClass));
        } catch (IOException e) {
//            e.printStackTrace();
        }
        ClassNode classNode = new ClassNode();
        classR.accept(classNode,0);
        return classNode;
    }


//    public void test() throws Exception {
//        ClassReader classR = new ClassReader(getInternalName(SampleTest.class));
//        ClassNode classNode = new ClassNode();
//        classR.accept(classNode,0);
//        List<MethodNode> methodNodeList = classNode.methods;
//        for(MethodNode methodNode : methodNodeList) {
//            for (AbstractInsnNode ain : methodNode.instructions.toArray()) {
////                if (ain.getType() == AbstractInsnNode.FIELD_INSN) {
////                    FieldInsnNode fin = (FieldInsnNode) ain;
////                    if (HomePage.class.getDeclaredField(fin.name).getType().isAssignableFrom(WebElement.class)) {
////                        System.out.println("Field Node");
////                        System.out.println(fin.name);
////                    }
//////                    System.out.println("Field Node");
////                    System.out.println(fin.name);
////
////                }
//                if (ain.getType() == AbstractInsnNode.TYPE_INSN) {
//                    TypeInsnNode fin = (TypeInsnNode) ain;
//                    try {
//                        System.out.println("Type node");
//                        System.out.println(fin.desc);
//                        System.out.println(Class.forName(fin.desc.replace("/", ".")));
//                    } catch (ClassCastException ex) {
//                        System.out.println(ex);
//                    }
//                }
////                if (ain.getType() == AbstractInsnNode.METHOD_INSN) {
////                    MethodInsnNode min = (MethodInsnNode) ain;
////                    System.out.println("Method Node");
////                    System.out.println(min.name);
////                    System.out.println(min.owner);
////                }
//
//            }
//        }
//    }
}
