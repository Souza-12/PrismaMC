package com.br.teagadev.prismamc.commons.bungee.manager.config;

import com.br.teagadev.prismamc.commons.bungee.BungeeMain;
import java.io.File;
import java.io.IOException;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class BungeeConfiguration {
   private final String configName;
   private Configuration configuration;
   private File file;

   public BungeeConfiguration(String configName) {
      this.configName = configName;
   }

   public void load() {
      this.file = new File(BungeeMain.getInstance().getDataFolder(), this.configName + ".yml");
      if (!this.file.exists()) {
         try {
            this.file.createNewFile();
         } catch (Exception var3) {
            return;
         }
      }

      try {
         this.configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(this.file);
      } catch (IOException var2) {
         var2.printStackTrace();
      }

      BungeeMain.getManager().getConfigManager().refreshConfig(this.configName);
   }

   public void save() {
      if (this.file != null) {
         try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(this.configuration, this.file);
         } catch (IOException var2) {
            var2.printStackTrace();
         }

      }
   }

   public void unload() {
      if (this.file != null) {
         this.file = null;
         this.configuration = null;
      }

   }

   public boolean isLoaded() {
      return this.file != null;
   }

   public String getConfigName() {
      return this.configName;
   }

   public Configuration getConfiguration() {
      return this.configuration;
   }

   public File getFile() {
      return this.file;
   }
}