package com.br.teagadev.prismamc.nowly.pvp.manager.kit;

import java.util.Arrays;
import java.util.Iterator;

import com.br.teagadev.prismamc.nowly.pvp.PvPMain;
import com.br.teagadev.prismamc.nowly.pvp.ability.Kit;
import org.bukkit.Material;

import com.br.teagadev.prismamc.commons.bukkit.manager.configuration.impl.BukkitConfiguration;
import com.br.teagadev.prismamc.commons.common.utility.ClassGetter;
import com.br.teagadev.prismamc.commons.common.utility.string.StringUtility;

import lombok.Getter;

public class KitLoader {
	   private static BukkitConfiguration kitsConfig = new BukkitConfiguration("kits", PvPMain.getInstance().getDataFolder(), false);

	   public static void load() {
	      kitsConfig.load();
	      boolean saveConfig = false;
	      Iterator var1 = ClassGetter.getClassesForPackage(PvPMain.getInstance(), "com.br.teagadev.prismamc.nowly.pvp.ability.register").iterator();

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
	                  kitsConfig.getConfiguration().set("kits." + className + ".icon.description", Arrays.asList("&7Edite a desc na", "&7Config"));
	                  saveConfig = true;
	               }
	            }
	         } catch (Exception var14) {
	            add = false;
	            PvPMain.console("Ocorreu um erro ao tentar carregar um kit '" + className + "' > " + var14.getLocalizedMessage());
	         }

	         if (add) {
	            Material material = null;

	            try {
	               if (StringUtility.isInteger(kitsConfig.getConfiguration().getString("kits." + className + ".icon.material"))) {
	                  material = Material.getMaterial(Integer.valueOf(kitsConfig.getConfiguration().getString("kits." + className + ".icon.material")));
	               } else {
	                  material = Material.getMaterial(kitsConfig.getConfiguration().getString("kits." + className + ".icon.material"));
	               }
	            } catch (NullPointerException var13) {
	               PvPMain.console("Material do Kit '" + className + "' está incorreto.");
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
	                  PvPMain.console("Ocorreu um erro ao tentar adicionar um Kit. ->" + var12.getLocalizedMessage());
	               }
	            } else {
	               PvPMain.console("não foi possivel adicionar o Kit -> " + className);
	            }
	         }
	      }

	      if (saveConfig) {
	         kitsConfig.save();
	      }

	      kitsConfig.unload();
	      kitsConfig = null;
	   }

	   public static BukkitConfiguration getKitsConfig() {
	      return kitsConfig;
	   }
	}