package com.br.teagadev.prismamc.login;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import com.br.teagadev.prismamc.commons.CommonsGeneral;
import com.br.teagadev.prismamc.commons.bukkit.BukkitMain;
import com.br.teagadev.prismamc.commons.bukkit.commands.BukkitCommandFramework;
import com.br.teagadev.prismamc.commons.bukkit.custom.events.server.ServerLoadedEvent;
import com.br.teagadev.prismamc.commons.bukkit.manager.configuration.PluginConfiguration;
import com.br.teagadev.prismamc.commons.bukkit.utility.loader.BukkitListeners;
import com.br.teagadev.prismamc.login.manager.LoginManager;


public class LoginMain extends JavaPlugin {
	   private static LoginMain instance;
	   public static Location spawn;
	   private static LoginManager manager;

	   public void onEnable() {
	      if (CommonsGeneral.correctlyStarted()) {
	         setInstance(this);
	         setManager(new LoginManager());
	         PluginConfiguration.createLocation(getInstance(), "spawn");
	         setSpawn(PluginConfiguration.getLocation(getInstance(), "spawn"));
	         BukkitListeners.loadListeners(getInstance(), "com.br.teagadev.prismamc.login.listener");
	         BukkitCommandFramework.INSTANCE.loadCommands(this, "com.br.teagadev.prismamc.login.commands");
	         runLater(() -> {
	            this.getServer().getPluginManager().callEvent(new ServerLoadedEvent());
	         }, (long)(BukkitMain.getServerType().getSecondsToStabilize() + 40));
	      } else {
	         Bukkit.shutdown();
	      }

	   }

	   public void onDisable() {
	      getManager().removeGamers(false);
	   }

	   public static void console(String msg) {
	      Bukkit.getConsoleSender().sendMessage("[Login] " + msg);
	   }

	   public static void runAsync(Runnable runnable) {
	      Bukkit.getScheduler().runTaskAsynchronously(getInstance(), runnable);
	   }

	   public static void runLater(Runnable runnable) {
	      Bukkit.getScheduler().runTaskLater(getInstance(), runnable, 5L);
	   }

	   public static void runLater(Runnable runnable, long ticks) {
	      Bukkit.getScheduler().runTaskLater(getInstance(), runnable, ticks);
	   }

	   public static LoginMain getInstance() {
	      return instance;
	   }

	   public static void setInstance(LoginMain instance) {
	      LoginMain.instance = instance;
	   }

	   public static Location getSpawn() {
	      return spawn;
	   }

	   public static void setSpawn(Location spawn) {
	      LoginMain.spawn = spawn;
	   }

	   public static LoginManager getManager() {
	      return manager;
	   }

	   public static void setManager(LoginManager manager) {
	      LoginMain.manager = manager;
	   }
	}