package com.br.teagadev.prismamc.commons.bukkit.manager.configuration;

import com.br.teagadev.prismamc.commons.CommonsGeneral;
import com.br.teagadev.prismamc.commons.bukkit.listeners.DamageListener;
import com.br.teagadev.prismamc.commons.bukkit.manager.configuration.impl.BukkitConfiguration;
import com.br.teagadev.prismamc.commons.common.group.Groups;
import com.br.teagadev.prismamc.commons.common.utility.string.StringUtility;
import java.util.Arrays;
import java.util.Collections;
import org.bukkit.Material;

public class BukkitConfigurationManager {
   private final BukkitConfiguration permissionsConfiguration = new BukkitConfiguration("bukkit-permissions", true);
   private final BukkitConfiguration globalConfiguration = new BukkitConfiguration("global-config", true);
   private final BukkitConfiguration damageConfiguration = new BukkitConfiguration("damages", true);

   public void init() {
      this.getPermissionsConfiguration().load();
      this.getGlobalConfiguration().load();
      this.getDamageConfiguration().load();
   }

   public void refreshConfig(String configName) {
      boolean hasUpdate = false;
      int var4;
      int var5;
      if (configName.equalsIgnoreCase("bukkit-permissions")) {
         Groups[] var3 = Groups.values();
         var4 = var3.length;

         for(var5 = 0; var5 < var4; ++var5) {
            Groups groups = var3[var5];
            if (groups != Groups.DEVELOPER) {
               if (!this.permissionsConfiguration.getConfiguration().contains("permissions." + groups.getName().toLowerCase())) {
                  this.permissionsConfiguration.getConfiguration().set("permissions." + groups.getName().toLowerCase(), groups == Groups.MEMBRO ? Collections.singletonList("tag.membro") : Arrays.asList("tag.membro", "tag." + groups.getName().toLowerCase()));
                  hasUpdate = true;
               } else {
                  groups.setPermissions(this.permissionsConfiguration.getConfiguration().getStringList("permissions." + groups.getName().toLowerCase()));
               }
            }
         }

         if (hasUpdate) {
            this.getPermissionsConfiguration().save();
         }

         this.getPermissionsConfiguration().unload();
         Groups.DEVELOPER.setPermissions(Groups.DONO.getPermissions());
      } else if (configName.equalsIgnoreCase("global-config")) {
         BukkitConfigurationManager.ValuesGlobalConfig[] var8 = BukkitConfigurationManager.ValuesGlobalConfig.values();
         var4 = var8.length;

         for(var5 = 0; var5 < var4; ++var5) {
            BukkitConfigurationManager.ValuesGlobalConfig values = var8[var5];
            if (!this.getGlobalConfiguration().getConfiguration().contains(values.getKey())) {
               this.getGlobalConfiguration().getConfiguration().set(values.getKey(), StringUtility.convertValue(values.getValue(), values.getClassExpected()));
               hasUpdate = true;
            }
         }

         if (hasUpdate) {
            this.getGlobalConfiguration().save();
         }
      } else if (configName.equalsIgnoreCase("damages")) {
         BukkitConfigurationManager.ValuesDano[] var9 = BukkitConfigurationManager.ValuesDano.values();
         var4 = var9.length;

         for(var5 = 0; var5 < var4; ++var5) {
            BukkitConfigurationManager.ValuesDano values = var9[var5];
            if (!this.getDamageConfiguration().getConfiguration().contains(values.getKey())) {
               this.getDamageConfiguration().getConfiguration().set(values.getKey(), StringUtility.convertValue(values.getValue(), values.getClassExpected()));
               hasUpdate = true;
            }
         }

         Material[] var10 = Material.values();
         var4 = var10.length;

         for(var5 = 0; var5 < var4; ++var5) {
            Material materiais = var10[var5];
            String name = materiais.name().toLowerCase();
            if ((name.contains("sword") || name.contains("pickaxe") || name.contains("spade") || name.contains("axe")) && !this.getDamageConfiguration().getConfiguration().contains("dano.materiais." + name)) {
               hasUpdate = true;
               this.getDamageConfiguration().getConfiguration().set("dano.materiais." + name, 1.0D);
            }
         }

         if (hasUpdate) {
            this.getDamageConfiguration().save();
         }
      }

      this.apply(configName);
   }

   private void apply(String configName) {
      if (configName.equalsIgnoreCase("global-config")) {
         CommonsGeneral.getMySQL().setHost(this.getStringByGlobalConfig("MySQL.Host"));
         CommonsGeneral.getMySQL().setDatabase(this.getStringByGlobalConfig("MySQL.Database"));
         CommonsGeneral.getMySQL().setUsuario(this.getStringByGlobalConfig("MySQL.Usuario"));
         CommonsGeneral.getMySQL().setSenha(this.getStringByGlobalConfig("MySQL.Senha"));
         CommonsGeneral.getMySQL().setPorta(this.getStringByGlobalConfig("MySQL.Porta"));
      } else if (configName.equalsIgnoreCase("damages")) {
         DamageListener.CRITICAL = this.getDamageConfiguration().getConfiguration().getBoolean("dano.critical");
         DamageListener.CHANCE_DE_CRITICAL = this.getDamageConfiguration().getConfiguration().getInt("dano.critical_chance");
         Material[] var2 = Material.values();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Material materiais = var2[var4];
            String name = materiais.name().toLowerCase();
            if (!name.contains("sword") && !name.contains("pickaxe") && !name.contains("spade") && !name.contains("axe")) {
               DamageListener.damageMaterial.put(materiais, 1.0D);
            } else {
               DamageListener.damageMaterial.put(materiais, this.getDamageConfiguration().getConfiguration().getDouble("dano.materiais." + name));
            }
         }
      }

   }

   private String getStringByGlobalConfig(String type) {
      return this.getGlobalConfiguration().getConfiguration().getString(type);
   }

   public BukkitConfiguration getPermissionsConfiguration() {
      return this.permissionsConfiguration;
   }

   public BukkitConfiguration getGlobalConfiguration() {
      return this.globalConfiguration;
   }

   public BukkitConfiguration getDamageConfiguration() {
      return this.damageConfiguration;
   }

   public static enum ValuesDano {
      CRITICAL("dano.critical", "true", "Boolean"),
      CRITICAL_CHANCE("dano.critical_chance", "35", "Integer");

      private final String key;
      private final String value;
      private final String classExpected;

      private ValuesDano(String key, String value, String classExpected) {
         this.key = key;
         this.value = value;
         this.classExpected = classExpected;
      }

      public String getKey() {
         return this.key;
      }

      public String getValue() {
         return this.value;
      }

      public String getClassExpected() {
         return this.classExpected;
      }
   }

   public static enum ValuesGlobalConfig {
      MYSQL_HOST("MySQL.Host", "localhost", "String"),
      MYSQL_PORTA("MySQL.Porta", "3306", "String"),
      MYSQL_DATABASE("MySQL.Database", "prismamc", "String"),
      MYSQL_USUARIO("MySQL.Usuario", "root", "String"),
      MYSQL_SENHA("MySQL.Senha", "", "String");

      private final String key;
      private final String value;
      private final String classExpected;

      private ValuesGlobalConfig(String key, String value, String classExpected) {
         this.key = key;
         this.value = value;
         this.classExpected = classExpected;
      }

      public String getKey() {
         return this.key;
      }

      public String getValue() {
         return this.value;
      }

      public String getClassExpected() {
         return this.classExpected;
      }
   }
}