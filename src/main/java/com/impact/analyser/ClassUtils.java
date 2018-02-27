package com.impact.analyser;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;

import static org.objectweb.asm.Type.getInternalName;

/**
 * Created by Yuvaraj on 27/02/2018.
 */
public class ClassUtils {

    public static Class<?> getClass(String fullyClass) {
        try {
            return Class.forName(fullyClass);
        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
        }
        return Class.class;
    }

    public static ClassNode getClassNode(Class<?> classClass) {
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
}
