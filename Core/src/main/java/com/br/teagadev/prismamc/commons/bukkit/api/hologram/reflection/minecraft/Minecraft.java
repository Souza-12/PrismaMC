package com.br.teagadev.prismamc.commons.bukkit.api.hologram.reflection.minecraft;

import com.br.teagadev.prismamc.commons.bukkit.api.hologram.reflection.resolver.ConstructorResolver;
import com.br.teagadev.prismamc.commons.bukkit.api.hologram.reflection.resolver.FieldResolver;
import com.br.teagadev.prismamc.commons.bukkit.api.hologram.reflection.resolver.MethodResolver;
import com.br.teagadev.prismamc.commons.bukkit.api.hologram.reflection.resolver.minecraft.NMSClassResolver;
import com.br.teagadev.prismamc.commons.bukkit.api.hologram.reflection.resolver.minecraft.OBCClassResolver;
import com.br.teagadev.prismamc.commons.bukkit.api.hologram.reflection.util.AccessUtil;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import sun.reflect.ConstructorAccessor;

public class Minecraft {
   public static final Minecraft.Version VERSION = Minecraft.Version.getVersion();
   static final Pattern NUMERIC_VERSION_PATTERN = Pattern.compile("v(\\d)_(\\d*)_R(\\d)");
   private static final NMSClassResolver nmsClassResolver = new NMSClassResolver();
   private static final OBCClassResolver obcClassResolver = new OBCClassResolver();
   private static final Class<?> NmsEntity;
   private static final Class<?> CraftEntity;

   public static String getVersion() {
      return VERSION.name() + ".";
   }

   public static Object getHandle(Object object) throws ReflectiveOperationException {
      Method method;
      try {
         method = AccessUtil.setAccessible(object.getClass().getDeclaredMethod("getHandle"));
      } catch (ReflectiveOperationException var3) {
         method = AccessUtil.setAccessible(CraftEntity.getDeclaredMethod("getHandle"));
      }

      return method.invoke(object);
   }

   public static Entity getBukkitEntity(Object object) throws ReflectiveOperationException {
      Method method;
      try {
         method = AccessUtil.setAccessible(NmsEntity.getDeclaredMethod("getBukkitEntity"));
      } catch (ReflectiveOperationException var3) {
         method = AccessUtil.setAccessible(CraftEntity.getDeclaredMethod("getHandle"));
      }

      return (Entity)method.invoke(object);
   }

   public static Object getHandleSilent(Object object) {
      try {
         return getHandle(object);
      } catch (Exception var2) {
         return null;
      }
   }

   public static Object newEnumInstance(Class clazz, Class[] types, Object[] values) throws ReflectiveOperationException {
      Constructor constructor = (new ConstructorResolver(clazz)).resolve(new Class[][]{types});
      Field accessorField = (new FieldResolver(Constructor.class)).resolve(new String[]{"constructorAccessor"});
      ConstructorAccessor constructorAccessor = (ConstructorAccessor)accessorField.get(constructor);
      if (constructorAccessor == null) {
         (new MethodResolver(Constructor.class)).resolve(new String[]{"acquireConstructorAccessor"}).invoke(constructor);
         constructorAccessor = (ConstructorAccessor)accessorField.get(constructor);
      }

      return constructorAccessor.newInstance(values);
   }

   static {
      try {
         NmsEntity = nmsClassResolver.resolve(new String[]{"Entity"});
         CraftEntity = obcClassResolver.resolve(new String[]{"entity.CraftEntity"});
      } catch (ReflectiveOperationException var1) {
         throw new RuntimeException(var1);
      }
   }

   public static enum Version {
      UNKNOWN(-1) {
         public boolean matchesPackageName(String packageName) {
            return false;
         }
      },
      v1_7_R1(10701),
      v1_7_R2(10702),
      v1_7_R3(10703),
      v1_7_R4(10704),
      v1_8_R1(10801),
      v1_8_R2(10802),
      v1_8_R3(10803),
      v1_8_R4(10804),
      v1_9_R1(10901),
      v1_9_R2(10902),
      v1_10_R1(11001),
      v1_11_R1(11101),
      v1_12_R1(11201),
      v1_13_R1(11301),
      v1_13_R2(11302),
      v1_14_R1(11401);

      private final int version;

      private Version(int version) {
         this.version = version;
      }

      public static Minecraft.Version getVersion() {
         String name = Bukkit.getServer().getClass().getPackage().getName();
         String versionPackage = name.substring(name.lastIndexOf(46) + 1) + ".";
         Minecraft.Version[] var2 = values();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Minecraft.Version version = var2[var4];
            if (version.matchesPackageName(versionPackage)) {
               return version;
            }
         }

         System.err.println("[ReflectionHelper] Failed to find version enum for '" + name + "'/'" + versionPackage + "'");
         System.out.println("[ReflectionHelper] Generating dynamic constant...");
         Matcher matcher = Minecraft.NUMERIC_VERSION_PATTERN.matcher(versionPackage);

         while(true) {
            do {
               if (!matcher.find()) {
                  return UNKNOWN;
               }
            } while(matcher.groupCount() < 3);

            String majorString = matcher.group(1);
            String minorString = matcher.group(2);
            if (minorString.length() == 1) {
               minorString = "0" + minorString;
            }

            String patchString = matcher.group(3);
            if (patchString.length() == 1) {
               patchString = "0" + patchString;
            }

            String numVersionString = majorString + minorString + patchString;
            int numVersion = Integer.parseInt(numVersionString);
            String packge = versionPackage.substring(0, versionPackage.length() - 1);

            try {
               Field valuesField = (new FieldResolver(Minecraft.Version.class)).resolve(new String[]{"$VALUES"});
               Minecraft.Version[] oldValues = (Minecraft.Version[])((Minecraft.Version[])valuesField.get((Object)null));
               Minecraft.Version[] newValues = new Minecraft.Version[oldValues.length + 1];
               System.arraycopy(oldValues, 0, newValues, 0, oldValues.length);
               Minecraft.Version dynamicVersion = (Minecraft.Version)Minecraft.newEnumInstance(Minecraft.Version.class, new Class[]{String.class, Integer.TYPE, Integer.TYPE}, new Object[]{packge, newValues.length - 1, numVersion});
               newValues[newValues.length - 1] = dynamicVersion;
               valuesField.set((Object)null, newValues);
               System.out.println("[ReflectionHelper] Injected dynamic version " + packge + " (#" + numVersion + ").");
               System.out.println("[ReflectionHelper] Please inform inventivegames about the outdated version, as this is not guaranteed to work.");
               return dynamicVersion;
            } catch (ReflectiveOperationException var13) {
               var13.printStackTrace();
            }
         }
      }

      public int version() {
         return this.version;
      }

      public boolean olderThan(Minecraft.Version version) {
         return this.version() < version.version();
      }

      public boolean newerThan(Minecraft.Version version) {
         return this.version() >= version.version();
      }

      public boolean inRange(Minecraft.Version oldVersion, Minecraft.Version newVersion) {
         return this.newerThan(oldVersion) && this.olderThan(newVersion);
      }

      public boolean matchesPackageName(String packageName) {
         return packageName.toLowerCase().contains(this.name().toLowerCase());
      }

      public String toString() {
         return this.name() + " (" + this.version() + ")";
      }

      // $FF: synthetic method
      Version(int x2, Object x3) {
         this(x2);
      }
   }
}