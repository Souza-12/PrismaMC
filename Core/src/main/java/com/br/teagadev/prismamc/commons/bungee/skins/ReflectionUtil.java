package com.br.teagadev.prismamc.commons.bungee.skins;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionUtil {
   public static Class<?> getBungeeClass(String path, String clazz) throws Exception {
      return Class.forName("net.md_5.bungee." + path + "." + clazz);
   }

   public static Constructor<?> getConstructor(Class<?> clazz, Class<?>... args) throws Exception {
      Constructor<?> c = clazz.getConstructor(args);
      c.setAccessible(true);
      return c;
   }

   public static Enum<?> getEnum(Class<?> clazz, String constant) throws Exception {
      Class<?> c = Class.forName(clazz.getName());
      Enum<?>[] econstants = (Enum[])((Enum[])c.getEnumConstants());
      Enum[] var4 = econstants;
      int var5 = econstants.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Enum<?> e = var4[var6];
         if (e.name().equalsIgnoreCase(constant)) {
            return e;
         }
      }

      throw new Exception("Enum constant not found " + constant);
   }

   public static Enum<?> getEnum(Class<?> clazz, String enumname, String constant) throws Exception {
      Class<?> c = Class.forName(clazz.getName() + "$" + enumname);
      Enum<?>[] econstants = (Enum[])((Enum[])c.getEnumConstants());
      Enum[] var5 = econstants;
      int var6 = econstants.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         Enum<?> e = var5[var7];
         if (e.name().equalsIgnoreCase(constant)) {
            return e;
         }
      }

      throw new Exception("Enum constant not found " + constant);
   }

   public static Field getField(Class<?> clazz, String fname) throws Exception {
      Field f = null;

      try {
         f = clazz.getDeclaredField(fname);
      } catch (Exception var4) {
         f = clazz.getField(fname);
      }

      setFieldAccessible(f);
      return f;
   }

   public static Object getFirstObject(Class<?> clazz, Class<?> objclass, Object instance) throws Exception {
      Field f = null;
      Field[] var4 = clazz.getDeclaredFields();
      int var5 = var4.length;

      int var6;
      Field fi;
      for(var6 = 0; var6 < var5; ++var6) {
         fi = var4[var6];
         if (fi.getType().equals(objclass)) {
            f = fi;
            break;
         }
      }

      if (f == null) {
         var4 = clazz.getFields();
         var5 = var4.length;

         for(var6 = 0; var6 < var5; ++var6) {
            fi = var4[var6];
            if (fi.getType().equals(objclass)) {
               f = fi;
               break;
            }
         }
      }

      setFieldAccessible(f);
      return f.get(instance);
   }

   public static Method getMethod(Class<?> clazz, String mname) throws Exception {
      Method m = null;

      try {
         m = clazz.getDeclaredMethod(mname);
      } catch (Exception var6) {
         try {
            m = clazz.getMethod(mname);
         } catch (Exception var5) {
            return m;
         }
      }

      m.setAccessible(true);
      return m;
   }

   public static <T> Field getField(Class<?> target, String name, Class<T> fieldType, int index) {
      Field[] var4 = target.getDeclaredFields();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Field field = var4[var6];
         if ((name == null || field.getName().equals(name)) && fieldType.isAssignableFrom(field.getType()) && index-- <= 0) {
            field.setAccessible(true);
            return field;
         }
      }

      if (target.getSuperclass() != null) {
         return getField(target.getSuperclass(), name, fieldType, index);
      } else {
         throw new IllegalArgumentException("Cannot find field with type " + fieldType);
      }
   }

   public static Method getMethod(Class<?> clazz, String mname, Class<?>... args) throws Exception {
      Method m = null;

      try {
         m = clazz.getDeclaredMethod(mname, args);
      } catch (Exception var7) {
         try {
            m = clazz.getMethod(mname, args);
         } catch (Exception var6) {
            return m;
         }
      }

      m.setAccessible(true);
      return m;
   }

   public static Object getObject(Class<?> clazz, Object obj, String fname) throws Exception {
      return getField(clazz, fname).get(obj);
   }

   public static Object getObject(Object obj, String fname) throws Exception {
      return getField(obj.getClass(), fname).get(obj);
   }

   public static Object invokeConstructor(Class<?> clazz, Class<?>[] args, Object... initargs) throws Exception {
      return getConstructor(clazz, args).newInstance(initargs);
   }

   public static Object invokeMethod(Class<?> clazz, Object obj, String method) throws Exception {
      return getMethod(clazz, method).invoke(obj);
   }

   public static Object invokeMethod(Class<?> clazz, Object obj, String method, Class<?>[] args, Object... initargs) throws Exception {
      return getMethod(clazz, method, args).invoke(obj, initargs);
   }

   public static Object invokeMethod(Class<?> clazz, Object obj, String method, Object... initargs) throws Exception {
      return getMethod(clazz, method).invoke(obj, initargs);
   }

   public static Object invokeMethod(Object obj, String method) throws Exception {
      return getMethod(obj.getClass(), method).invoke(obj);
   }

   public static Object invokeMethod(Object obj, String method, Object[] initargs) throws Exception {
      return getMethod(obj.getClass(), method).invoke(obj, initargs);
   }

   public static void setFieldAccessible(Field f) throws Exception {
      f.setAccessible(true);
      Field modifiers = Field.class.getDeclaredField("modifiers");
      modifiers.setAccessible(true);
      modifiers.setInt(f, f.getModifiers() & -17);
   }

   public static void setObject(Class<?> clazz, Object obj, String fname, Object value) throws Exception {
      getField(clazz, fname).set(obj, value);
   }

   public static void setObject(Object obj, String fname, Object value) throws Exception {
      getField(obj.getClass(), fname).set(obj, value);
   }
}