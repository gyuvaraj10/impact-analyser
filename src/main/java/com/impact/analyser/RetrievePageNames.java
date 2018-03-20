package com.impact.analyser;

import com.impact.analyser.rules.PageRules;
import com.impact.analyser.utils.ClassUtils;
import org.objectweb.asm.tree.*;
import org.openqa.selenium.WebElement;

import java.util.*;

/**
 * Created by Yuvaraj on 27/02/2018.
 */
public class RetrievePageNames {


    public Map<String, Set<String>> getPagesAndMethods(PageRules pageRules, MethodNode testMethodNode, Class<?> testStepDefClass) {
        Map<String, Set<String>> pageNames = getPagesUsedInTest(pageRules, testMethodNode, testStepDefClass);
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

    public Map<String, Set<String>> getPagesUsedInTest(MethodNode testMethodNode) {
        Map<String, Set<String>> pageClasses = new HashMap<>();
        for (AbstractInsnNode ain : testMethodNode.instructions.toArray()) {
            if (ain.getType() == AbstractInsnNode.TYPE_INSN) {
                TypeInsnNode fin = (TypeInsnNode) ain;
                try {
                    Class<?> classFromTestMethod = Class.forName(fin.desc.replace("/", "."));
                    ClassNode classNodeFromTestMethod = ClassUtils.getClassNode(classFromTestMethod);
                    List<FieldNode> fieldNodes = classNodeFromTestMethod.fields;
                    Optional optional = fieldNodes.stream().filter(x->ClassUtils.getClass(x.desc
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

    private Map<String, Set<String>> getPagesUsedInTest(PageRules pageRules, MethodNode testMethodNode, Class<?> testStepClass) {
        Class<?> basePageClass = ClassUtils.getClass(pageRules.getBasePageClass());
        Map<String, Set<String>> pageClasses = new HashMap<>();
        for (AbstractInsnNode ain : testMethodNode.instructions.toArray()) {
            if (ain.getType() == AbstractInsnNode.TYPE_INSN) {
                TypeInsnNode fin = (TypeInsnNode) ain;
                try {
                    Class<?> classFromTestMethod = Class.forName(fin.desc.replace("/", "."));
                    Class<?> superClass = classFromTestMethod.getSuperclass();
                    if(superClass!= null && superClass != Object.class) {
                        if (superClass.isAssignableFrom(basePageClass)||classFromTestMethod.asSubclass(basePageClass) !=null) {
                            pageClasses.put(classFromTestMethod.getName(), new HashSet<>());
                        }
                    }
                } catch (ClassCastException ex) {
                } catch (ClassNotFoundException e) {
                }
            } else if(ain.getType() == AbstractInsnNode.METHOD_INSN) {
                MethodInsnNode min = (MethodInsnNode)ain;
                try {
                    Class<?> classFromTestMethod = Class.forName(min.owner.replace("/", "."));
                    Class<?> superClass = classFromTestMethod.getSuperclass();
                    if(superClass!= null && superClass != Object.class) {
                        if (superClass.isAssignableFrom(basePageClass)||classFromTestMethod.asSubclass(basePageClass) !=null) {
                            if(testStepClass == classFromTestMethod) {
                                classFromTestMethod = superClass;
                            }
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

    private Map<String, Set<String>> getPagesUsedInTest(PageRules pageRules, MethodNode testMethodNode) {
        Class<?> basePageClass = ClassUtils.getClass(pageRules.getBasePageClass());
        Map<String, Set<String>> pageClasses = new HashMap<>();
        for (AbstractInsnNode ain : testMethodNode.instructions.toArray()) {
            if (ain.getType() == AbstractInsnNode.TYPE_INSN) {
                TypeInsnNode fin = (TypeInsnNode) ain;
                try {
                    Class<?> classFromTestMethod = Class.forName(fin.desc.replace("/", "."));
                    Class<?> superClass = classFromTestMethod.getSuperclass();
                    if(superClass!= null && superClass != Object.class) {
                        if (superClass.isAssignableFrom(basePageClass)||classFromTestMethod.asSubclass(basePageClass) !=null) {
                            pageClasses.put(classFromTestMethod.getName(), new HashSet<>());
                        }
                    }
                } catch (ClassCastException ex) {
                } catch (ClassNotFoundException e) {
                }
            } else if(ain.getType() == AbstractInsnNode.METHOD_INSN) {
                MethodInsnNode min = (MethodInsnNode)ain;
                try {
                    Class<?> classFromTestMethod = Class.forName(min.owner.replace("/", "."));
                    Class<?> superClass = classFromTestMethod.getSuperclass();
                    if(superClass!= null && superClass != Object.class) {
                        if (superClass.isAssignableFrom(basePageClass)||classFromTestMethod.asSubclass(basePageClass) !=null) {
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
}
