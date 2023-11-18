package com.br.teagadev.prismamc.nowly.pvp;

import com.br.teagadev.prismamc.nowly.pvp.mode.arena.ArenaMode;
import com.br.teagadev.prismamc.nowly.pvp.mode.fps.FPSMode;
import com.br.teagadev.prismamc.nowly.pvp.mode.lavachallenge.LavaChallengeMode;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.br.teagadev.prismamc.commons.CommonsGeneral;
import com.br.teagadev.prismamc.commons.bukkit.BukkitMain;
import com.br.teagadev.prismamc.commons.bukkit.api.BukkitServerAPI;
import com.br.teagadev.prismamc.commons.bukkit.commands.BukkitCommandFramework;
import com.br.teagadev.prismamc.commons.bukkit.custom.events.server.ServerLoadedEvent;
import com.br.teagadev.prismamc.commons.bukkit.utility.loader.BukkitListeners;


public class PvPMain extends JavaPlugin {
	   private static PvPMain instance;

	   public void onLoad() {
	      setInstance(this);
	      this.saveDefaultConfig();
	   }

	   @SuppressWarnings("incomplete-switch")
	public void onEnable() {
	      if (CommonsGeneral.correctlyStarted()) {
	         BukkitServerAPI.registerServer();
	         boolean findedMode = false;
	         switch(BukkitMain.getServerType()) {
	         case PVP_FPS:
	            FPSMode.init();
	            findedMode = true;
	            break;
	         case PVP_LAVACHALLENGE:
	            LavaChallengeMode.init();
	            LavaChallengeMode.updateDifficulty(this.getConfig().getInt("LavaChallenge.MinHit.Facil"), this.getConfig().getInt("LavaChallenge.MinHit.Medio"), this.getConfig().getInt("LavaChallenge.MinHit.Dificil"), this.getConfig().getInt("LavaChallenge.MinHit.Extremo"));
	            findedMode = true;
	            break;
	         case PVP_ARENA:
	            ArenaMode.init();
	            findedMode = true;
	         }

	         if (!findedMode) {
	            Bukkit.shutdown();
	            return;
	         }

	         BukkitListeners.loadListeners(getInstance(), "com.br.teagadev.prismamc.nowly.pvp.listeners");
	         BukkitCommandFramework.INSTANCE.loadCommands(this, "com.br.teagadev.prismamc.nowly.pvp.commands");
	         runLater(() -> {
	            this.getServer().getPluginManager().callEvent(new ServerLoadedEvent());
	         }, (long)BukkitMain.getServerType().getSecondsToStabilize());
	      } else {
	         Bukkit.shutdown();
	      }

	   }

	   public void onDisable() {
	   }

	   public static void console(String msg) {
	      Bukkit.getConsoleSender().sendMessage("[PvP] " + msg);
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

	   public static PvPMain getInstance() {
	      return instance;
	   }

	   public static void setInstance(PvPMain instance) {
	      PvPMain.instance = instance;
	   }
	}