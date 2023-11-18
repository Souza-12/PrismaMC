package com.br.teagadev.prismamc.hardcoregames.manager.kit;

import com.br.teagadev.prismamc.commons.CommonsGeneral;
import com.br.teagadev.prismamc.commons.bukkit.manager.configuration.impl.BukkitConfiguration;
import com.br.teagadev.prismamc.commons.common.utility.ClassGetter;
import com.br.teagadev.prismamc.commons.common.utility.system.Machine;
import com.br.teagadev.prismamc.hardcoregames.HardcoreGamesMain;
import com.br.teagadev.prismamc.hardcoregames.ability.Kit;
import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import org.bukkit.Material;

public class KitLoader {
   private static BukkitConfiguration kitsConfig = new BukkitConfiguration("kits-hg", getDirectorty(), false);

   public static void load() {
      kitsConfig.load();
      boolean saveConfig = false;
      Iterator var1 = ClassGetter.getClassesForPackage(HardcoreGamesMain.getInstance(), "com.br.teagadev.prismamc.hardcoregames.ability.register").iterator();

      while(var1.hasNext()) {
         Class<?> classes = (Class)var1.next();
         String className = classes.getSimpleName();
         boolean add = false;

         try {
            if (Kit.class.isAssignableFrom(classes) && classes != Kit.class) {
               add = true;
               if (!kitsConfig.getConfiguration().contains("kits." + className + ".price")) {
                  kitsConfig.getConfiguration().set("kits." + className + ".price", 1000);
                  kitsConfig.getConfiguration().set("kits." + className + ".cooldown", 20);
                  kitsConfig.getConfiguration().set("kits." + className + ".icon.material", "CHEST");
                  kitsConfig.getConfiguration().set("kits." + className + ".icon.durability", 0);
                  kitsConfig.getConfiguration().set("kits." + className + ".icon.amount", 1);
                  kitsConfig.getConfiguration().set("kits." + className + ".icon.description", Arrays.asList("&bEdite a desc na", "&cConfig"));
                  saveConfig = true;
               }
            }
         } catch (Exception var14) {
            add = false;
            CommonsGeneral.error("Ocorreu um erro ao tentar carregar um kit '" + className + "' > " + var14.getLocalizedMessage());
         }

         if (add) {
            Material material = null;

            try {
               material = Material.getMaterial(kitsConfig.getConfiguration().getString("kits." + className + ".icon.material"));
            } catch (NullPointerException var13) {
               HardcoreGamesMain.console("Material do Kit '" + className + "' está incorreto.");
               add = false;
            } finally {
               if (material == null) {
                  add = false;
               }

            }

            if (add) {
               try {
                  KitManager.getKits().put(className.toLowerCase(), (Kit)classes.newInstance());
               } catch (Exception var12) {
                  HardcoreGamesMain.console("Ocorreu um erro ao tentar adicionar um Kit. ->" + var12.getLocalizedMessage());
               }
            } else {
               HardcoreGamesMain.console("Não foi possivel adicionar o Kit -> " + className);
            }
         }
      }

      if (saveConfig) {
         kitsConfig.save();
      }

      kitsConfig.unload();
      kitsConfig = null;
   }

   private static File getDirectorty() {
      File dir = new File(Machine.getDiretorio());
      if (!dir.exists()) {
         dir.mkdir();
      }

      return dir;
   }

   public static BukkitConfiguration getKitsConfig() {
      return kitsConfig;
   }
}