package com.br.teagadev.prismamc.commons.bungee.manager.config;

import com.br.teagadev.prismamc.commons.CommonsGeneral;
import com.br.teagadev.prismamc.commons.bungee.BungeeMain;
import com.br.teagadev.prismamc.commons.common.group.Groups;
import com.br.teagadev.prismamc.commons.common.utility.string.StringUtility;
import java.util.Collections;

public class BungeeConfigurationManager {
   private final BungeeConfiguration permissionsConfig = new BungeeConfiguration("bungee-permissions");
   private final BungeeConfiguration bungeeConfig = new BungeeConfiguration("bungee-config");

   public void refreshConfig(String configName) {
      boolean hasUpdate;
      int var4;
      int var5;
      if (configName.equalsIgnoreCase("bungee-permissions")) {
         hasUpdate = false;
         Groups[] var3 = Groups.values();
         var4 = var3.length;

         for(var5 = 0; var5 < var4; ++var5) {
            Groups groups = var3[var5];
            if (groups != Groups.DEVELOPER) {
               if (!this.permissionsConfig.getConfiguration().contains("permissions." + groups.getName().toLowerCase())) {
                  this.permissionsConfig.getConfiguration().set("permissions." + groups.getName().toLowerCase(), Collections.singletonList("teste.permission"));
                  hasUpdate = true;
               } else {
                  groups.setPermissions(this.permissionsConfig.getConfiguration().getStringList("permissions." + groups.getName().toLowerCase()));
               }
            }
         }

         if (hasUpdate) {
            this.getPermissionsConfig().save();
         }

         this.getPermissionsConfig().unload();
         Groups.DEVELOPER.setPermissions(Groups.DONO.getPermissions());
      } else if (configName.equalsIgnoreCase("bungee-config")) {
         hasUpdate = false;
         BungeeConfigurationManager.ValuesBungeeConfig[] var7 = BungeeConfigurationManager.ValuesBungeeConfig.values();
         var4 = var7.length;

         for(var5 = 0; var5 < var4; ++var5) {
            BungeeConfigurationManager.ValuesBungeeConfig values = var7[var5];
            if (!this.getBungeeConfig().getConfiguration().contains(values.getKey())) {
               this.getBungeeConfig().getConfiguration().set(values.getKey(), StringUtility.convertValue(values.getValue(), values.getClassExpected()));
               hasUpdate = true;
            }
         }

         if (hasUpdate) {
            this.getBungeeConfig().save();
         }

         this.apply(configName);
      }

   }

   private void apply(String configName) {
      if (configName.equalsIgnoreCase("bungee-config")) {
         CommonsGeneral.getMySQL().setHost(this.getBungeeConfig().getConfiguration().getString("MySQL.Host"));
         CommonsGeneral.getMySQL().setDatabase(this.getBungeeConfig().getConfiguration().getString("MySQL.Database"));
         CommonsGeneral.getMySQL().setUsuario(this.getBungeeConfig().getConfiguration().getString("MySQL.Usuario"));
         CommonsGeneral.getMySQL().setSenha(this.getBungeeConfig().getConfiguration().getString("MySQL.Senha"));
         CommonsGeneral.getMySQL().setPorta(this.getBungeeConfig().getConfiguration().getString("MySQL.Porta"));
         BungeeMain.getManager().setMinutos(this.getBungeeConfig().getConfiguration().getInt("AutoMSG.Minutos"));
         BungeeMain.setSocketServerHost(this.getBungeeConfig().getConfiguration().getString("Socket.Host"));
         this.getBungeeConfig().unload();
      }

   }

   public BungeeConfiguration getPermissionsConfig() {
      return this.permissionsConfig;
   }

   public BungeeConfiguration getBungeeConfig() {
      return this.bungeeConfig;
   }

   public static enum ValuesBungeeConfig {
      MYSQL_HOST("MySQL.Host", "localhost", "String"),
      MYSQL_PORTA("MySQL.Porta", "3306", "String"),
      MYSQL_DATABASE("MySQL.Database", "prismamc", "String"),
      MYSQL_USUARIO("MySQL.Usuario", "root", "String"),
      MYSQL_SENHA("MySQL.Senha", "vooiid", "String"),
      AUTO_MSG_MINUTOS("AutoMSG.Minutos", "3", "Integer"),
      SOCKET_HOST("Socket.Host", "127.0.0.1", "String");

      private final String key;
      private final String value;
      private final String classExpected;

      private ValuesBungeeConfig(String key, String value, String classExpected) {
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