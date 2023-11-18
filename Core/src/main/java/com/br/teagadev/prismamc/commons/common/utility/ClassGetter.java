package com.br.teagadev.prismamc.commons.common.utility;

import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassGetter {
   public static ArrayList<Class<?>> getClassesForPackage(Object instance, String packageName) {
      CodeSource source = instance.getClass().getProtectionDomain().getCodeSource();
      ArrayList<Class<?>> classes = new ArrayList();
      ArrayList<Class<?>> processedClasses = new ArrayList();
      ArrayList<String> names = new ArrayList();
      if (source != null) {
         processJarfile(source.getLocation(), packageName, classes);
      }

      Iterator var6 = classes.iterator();

      while(var6.hasNext()) {
         Class<?> simpleClass = (Class)var6.next();
         names.add(simpleClass.getSimpleName());
         processedClasses.add(simpleClass);
      }

      classes.clear();
      Collections.sort(names, String.CASE_INSENSITIVE_ORDER);
      var6 = names.iterator();

      while(true) {
         while(var6.hasNext()) {
            String name = (String)var6.next();
            Iterator var8 = processedClasses.iterator();

            while(var8.hasNext()) {
               Class<?> simpleClass = (Class)var8.next();
               if (simpleClass.getSimpleName().equals(name)) {
                  classes.add(simpleClass);
                  break;
               }
            }
         }

         processedClasses.clear();
         processedClasses = null;
         names.clear();
         names = null;
         source = null;
         return classes;
      }
   }

   private static Class<?> loadClass(String className) {
      try {
         return Class.forName(className);
      } catch (ClassNotFoundException var2) {
         throw new RuntimeException("Unexpected ClassNotFoundException loading class '" + className + "'");
      }
   }

   private static void processJarfile(URL resource, String packageName, ArrayList<Class<?>> classes) {
      String relPath = packageName.replace('.', '/');
      String resPath = resource.getPath().replace("%20", " ");
      String jarPath = resPath.replaceFirst("[.]jar!.*", ".jar").replaceFirst("file:", "");

      JarFile jarFile;
      try {
         jarFile = new JarFile(jarPath);
      } catch (IOException var12) {
         throw new RuntimeException("Unexpected IOException reading JAR File '" + jarPath + "'", var12);
      }

      Enumeration entries = jarFile.entries();

      while(entries.hasMoreElements()) {
         JarEntry entry = (JarEntry)entries.nextElement();
         String entryName = entry.getName();
         String className = null;
         if (entryName.endsWith(".class") && entryName.startsWith(relPath) && entryName.length() > relPath.length() + "/".length()) {
            className = entryName.replace('/', '.').replace('\\', '.').replace(".class", "");
         }

         if (className != null) {
            classes.add(loadClass(className));
         }
      }

      try {
         jarFile.close();
      } catch (IOException var11) {
         var11.printStackTrace();
      }

   }
}