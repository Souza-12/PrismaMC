package com.br.teagadev.prismamc.commons.bukkit.manager.configuration.impl;

import com.br.teagadev.prismamc.commons.bukkit.BukkitMain;
import com.br.teagadev.prismamc.commons.common.utility.system.Machine;
import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.file.FileConfiguration;

public class BukkitConfiguration {
   private String configName;
   private File file;
   private File dir;
   private FileConfiguration configuration;

   public BukkitConfiguration(String configName, File dir, boolean createDefaultDir) {
      this.configName = configName;
      if (createDefaultDir) {
         this.createDir();
      }

      if (dir != null) {
         this.dir = dir;
      }

   }

   public BukkitConfiguration(String configName, boolean createDefaultDir) {
      this(configName, (File)null, createDefaultDir);
   }

   public void load() {
      if (this.file != null) {
         this.console("§c" + this.configName + " has already loaded!");
      } else {
         this.file = this.dir == null ? new File(Machine.getDiretorio(), this.configName + ".yml") : new File(this.dir, this.configName + ".yml");
         if (!this.file.exists()) {
            try {
               this.file.createNewFile();
            } catch (IOException var2) {
               var2.printStackTrace();
               this.console("§cAn error ocurred on create Configuration ''" + this.configName + "' > " + var2.getLocalizedMessage());
            }
         }

         this.configuration = Utf8YamlConfiguration.loadConfiguration(this.file);
         BukkitMain.getManager().getConfigurationManager().refreshConfig(this.configName);
      }
   }

   public void save() {
      if (this.file == null) {
         this.console("§c" + this.configName + " has not loaded!");
      } else {
         try {
            this.configuration.save(this.file);
         } catch (IOException var2) {
            var2.printStackTrace();
         }

      }
   }

   private void createDir() {
      File dir = new File(Machine.getDiretorio());
      if (!dir.exists()) {
         dir.mkdir();
      }

      dir = null;
   }

   public void unload() {
      if (this.file != null) {
         this.file = null;
         this.configuration = null;
      }

   }

   private void console(String message) {
      BukkitMain.console("[BukkitConfiguration] " + message);
   }

   public String getConfigName() {
      return this.configName;
   }

   public File getFile() {
      return this.file;
   }

   public File getDir() {
      return this.dir;
   }

   public FileConfiguration getConfiguration() {
      return this.configuration;
   }

   public void setConfigName(String configName) {
      this.configName = configName;
   }

   public void setFile(File file) {
      this.file = file;
   }

   public void setDir(File dir) {
      this.dir = dir;
   }

   public void setConfiguration(FileConfiguration configuration) {
      this.configuration = configuration;
   }
}