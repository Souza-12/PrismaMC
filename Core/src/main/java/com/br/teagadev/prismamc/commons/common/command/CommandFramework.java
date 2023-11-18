package com.br.teagadev.prismamc.commons.common.command;

import com.br.teagadev.prismamc.commons.CommonsGeneral;
import com.br.teagadev.prismamc.commons.common.group.Groups;
import com.br.teagadev.prismamc.commons.common.utility.ClassGetter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Iterator;

public interface CommandFramework {
   Object getPlugin();

   void registerCommands(CommandClass var1);

   default CommandFramework loadCommands(Object plugin, String packageName) {
      Iterator var3 = ClassGetter.getClassesForPackage(plugin, packageName).iterator();

      while(var3.hasNext()) {
         Class<?> commandClass = (Class)var3.next();
         if (CommandClass.class.isAssignableFrom(commandClass)) {
            try {
               this.registerCommands((CommandClass)commandClass.newInstance());
            } catch (Exception var6) {
               CommonsGeneral.console("Error when loading command from " + commandClass.getSimpleName() + "!");
               var6.printStackTrace();
            }
         }
      }

      return this;
   }

   @Target({ElementType.METHOD})
   @Retention(RetentionPolicy.RUNTIME)
   public @interface Completer {
      String name();

      String[] aliases() default {};
   }

   @Target({ElementType.METHOD})
   @Retention(RetentionPolicy.RUNTIME)
   public @interface Command {
      String name();

      Groups[] groupsToUse() default {Groups.MEMBRO};

      String permission() default "";

      String[] aliases() default {};

      String description() default "";

      String usage() default "";

      boolean runAsync() default false;
   }
}