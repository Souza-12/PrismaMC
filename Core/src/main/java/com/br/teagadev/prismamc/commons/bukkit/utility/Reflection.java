package com.br.teagadev.prismamc.commons.bukkit.utility;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;

public final class Reflection {
   private static final String OBC_PREFIX = Bukkit.getServer().getClass().getPackage().getName();
   private static final String NMS_PREFIX;
   private static final String VERSION;
   private static final Pattern MATCH_VARIABLE;

   private Reflection() {
   }

   public static void setValue(String field, Class<?> clazz, Object instance, Object value) {
      try {
         Field f = clazz.getDeclaredField(field);
         f.setAccessible(true);
         f.set(instance, value);
      } catch (Exception var5) {
         var5.printStackTrace();
      }

   }

   public static Object getValue(String field, Class<?> clazz, Object instance) {
      try {
         Field f = clazz.getDeclaredField(field);
         f.setAccessible(true);
         return f.get(instance);
      } catch (Exception var4) {
         var4.printStackTrace();
         return null;
      }
   }

   public static void setValue(String field, Object instance, Object value) {
      try {
         Field f = instance.getClass().getDeclaredField(field);
         f.setAccessible(true);
         f.set(instance, value);
      } catch (Exception var4) {
         var4.printStackTrace();
      }

   }

   public static Object getValue(String field, Object instance) {
      try {
         Field f = instance.getClass().getDeclaredField(field);
         f.setAccessible(true);
         return f.get(instance);
      } catch (Exception var3) {
         var3.printStackTrace();
         return null;
      }
   }

   public static <T> Reflection.FieldAccessor<T> getField(Class<?> target, String name, Class<T> fieldType) {
      return getField(target, name, fieldType, 0);
   }

   public static <T> Reflection.FieldAccessor<T> getField(String className, String name, Class<T> fieldType) {
      return getField(getClass(className), name, fieldType, 0);
   }

   public static <T> Reflection.FieldAccessor<T> getField(Class<?> target, Class<T> fieldType, int index) {
      return getField(target, (String)null, fieldType, index);
   }

   public static <T> Reflection.FieldAccessor<T> getField(String className, Class<T> fieldType, int index) {
      return getField(getClass(className), fieldType, index);
   }

   private static <T> Reflection.FieldAccessor<T> getField(Class<?> target, String name, Class<T> fieldType, int index) {
      Field[] var4 = target.getDeclaredFields();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         final Field field = var4[var6];
         if ((name == null || field.getName().equals(name)) && fieldType.isAssignableFrom(field.getType()) && index-- <= 0) {
            field.setAccessible(true);
            return new Reflection.FieldAccessor<T>() {
               public T get(Object target) {
                  try {
                     return (T) field.get(target);
                  } catch (IllegalAccessException var3) {
                     throw new RuntimeException("Cannot access reflection.", var3);
                  }
               }

               public void set(Object target, Object value) {
                  try {
                     field.set(target, value);
                  } catch (IllegalAccessException var4) {
                     throw new RuntimeException("Cannot access reflection.", var4);
                  }
               }

               public boolean hasField(Object target) {
                  return field.getDeclaringClass().isAssignableFrom(target.getClass());
               }
            };
         }
      }

      if (target.getSuperclass() != null) {
         return getField(target.getSuperclass(), name, fieldType, index);
      } else {
         throw new IllegalArgumentException("Cannot find field with type " + fieldType);
      }
   }

   public static Reflection.MethodInvoker getMethod(String className, String methodName, Class<?>... params) {
      return getTypedMethod(getClass(className), methodName, (Class)null, params);
   }

   public static Reflection.MethodInvoker getMethod(Class<?> clazz, String methodName, Class<?>... params) {
      return getTypedMethod(clazz, methodName, (Class)null, params);
   }

   public static Reflection.MethodInvoker getTypedMethod(Class<?> clazz, String methodName, Class<?> returnType, Class<?>... params) {
      Method[] var4 = clazz.getDeclaredMethods();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         final Method method = var4[var6];
         if ((methodName == null || method.getName().equals(methodName)) && returnType == null || method.getReturnType().equals(returnType) && Arrays.equals(method.getParameterTypes(), params)) {
            method.setAccessible(true);
            return new Reflection.MethodInvoker() {
               public Object invoke(Object target, Object... arguments) {
                  try {
                     return method.invoke(target, arguments);
                  } catch (Exception var4) {
                     throw new RuntimeException("Cannot invoke method " + method, var4);
                  }
               }
            };
         }
      }

      if (clazz.getSuperclass() != null) {
         return getMethod(clazz.getSuperclass(), methodName, params);
      } else {
         throw new IllegalStateException(String.format("Unable to find method %s (%s).", methodName, Arrays.asList(params)));
      }
   }

   public static Reflection.ConstructorInvoker getConstructor(String className, Class<?>... params) {
      return getConstructor(getClass(className), params);
   }

   public static Reflection.ConstructorInvoker getConstructor(Class<?> clazz, Class<?>... params) {
      Constructor[] var2 = clazz.getDeclaredConstructors();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         final Constructor<?> constructor = var2[var4];
         if (Arrays.equals(constructor.getParameterTypes(), params)) {
            constructor.setAccessible(true);
            return new Reflection.ConstructorInvoker() {
               public Object invoke(Object... arguments) {
                  try {
                     return constructor.newInstance(arguments);
                  } catch (Exception var3) {
                     throw new RuntimeException("Cannot invoke constructor " + constructor, var3);
                  }
               }
            };
         }
      }

      throw new IllegalStateException(String.format("Unable to find constructor for %s (%s).", clazz, Arrays.asList(params)));
   }

   public static Class<Object> getUntypedClass(String lookupName) {
      Class<Object> clazz = (Class<Object>) getClass(lookupName);
      return clazz;
   }

   public static Class<?> getClass(String lookupName) {
      return getCanonicalClass(expandVariables(lookupName));
   }

   public static Class<?> getMinecraftClass(String name) {
      return getCanonicalClass(NMS_PREFIX + "." + name);
   }

   public static Class<?> getCraftBukkitClass(String name) {
      return getCanonicalClass(OBC_PREFIX + "." + name);
   }

   private static Class<?> getCanonicalClass(String canonicalName) {
      try {
         return Class.forName(canonicalName);
      } catch (ClassNotFoundException var2) {
         throw new IllegalArgumentException("Cannot find " + canonicalName, var2);
      }
   }

   private static String expandVariables(String name) {
      StringBuffer output = new StringBuffer();

      Matcher matcher;
      String replacement;
      for(matcher = MATCH_VARIABLE.matcher(name); matcher.find(); matcher.appendReplacement(output, Matcher.quoteReplacement(replacement))) {
         String variable = matcher.group(1);
         replacement = "";
         if ("nms".equalsIgnoreCase(variable)) {
            replacement = NMS_PREFIX;
         } else if ("obc".equalsIgnoreCase(variable)) {
            replacement = OBC_PREFIX;
         } else {
            if (!"version".equalsIgnoreCase(variable)) {
               throw new IllegalArgumentException("Unknown variable: " + variable);
            }

            replacement = VERSION;
         }

         if (replacement.length() > 0 && matcher.end() < name.length() && name.charAt(matcher.end()) != '.') {
            replacement = replacement + ".";
         }
      }

      matcher.appendTail(output);
      return output.toString();
   }

   public static String getVersion() {
      String name = Bukkit.getServer().getClass().getPackage().getName();
      return name.substring(name.lastIndexOf(46) + 1) + ".";
   }

   public static Class<?> getNMSClass(String className) {
      String fullName = "net.minecraft.server." + getVersion() + className;
      Class clazz = null;

      try {
         clazz = Class.forName(fullName);
      } catch (Exception var4) {
         var4.printStackTrace();
      }

      return clazz;
   }

   public static Class<?> getNMSClassWithException(String className) throws Exception {
      String fullName = "net.minecraft.server." + getVersion() + className;
      return Class.forName(fullName);
   }

   public static Class<?> getOBCClass(String className) {
      String fullName = "org.bukkit.craftbukkit." + getVersion() + className;
      Class clazz = null;

      try {
         clazz = Class.forName(fullName);
      } catch (Exception var4) {
         var4.printStackTrace();
      }

      return clazz;
   }

   public static Object getHandle(Object obj) {
      try {
         return getMethod(obj.getClass(), "getHandle").invoke(obj);
      } catch (Exception var2) {
         var2.printStackTrace();
         return null;
      }
   }

   public static Field getField(Class<?> clazz, String name) {
      try {
         Field field = clazz.getDeclaredField(name);
         field.setAccessible(true);
         return field;
      } catch (Exception var3) {
         var3.printStackTrace();
         return null;
      }
   }

   public static Field getFieldWithException(Class<?> clazz, String name) throws Exception {
      Field field = clazz.getDeclaredField(name);
      field.setAccessible(true);
      return field;
   }

   public static boolean ClassListEqual(Class<?>[] l1, Class<?>[] l2) {
      boolean equal = true;
      if (l1.length != l2.length) {
         return false;
      } else {
         for(int i = 0; i < l1.length; ++i) {
            if (l1[i] != l2[i]) {
               equal = false;
               break;
            }
         }

         return equal;
      }
   }

   static {
      NMS_PREFIX = OBC_PREFIX.replace("org.bukkit.craftbukkit", "net.minecraft.server");
      VERSION = OBC_PREFIX.replace("org.bukkit.craftbukkit", "").replace(".", "");
      MATCH_VARIABLE = Pattern.compile("\\{([^}]+)}");
   }

   public interface FieldAccessor<T> {
      T get(Object var1);

      void set(Object var1, Object var2);

      boolean hasField(Object var1);
   }

   public interface MethodInvoker {
      Object invoke(Object var1, Object... var2);
   }

   public interface ConstructorInvoker {
      Object invoke(Object... var1);
   }
}