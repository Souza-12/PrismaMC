package com.br.teagadev.prismamc.hardcoregames;

import com.br.teagadev.prismamc.commons.CommonsGeneral;
import com.br.teagadev.prismamc.commons.bukkit.BukkitMain;
import com.br.teagadev.prismamc.commons.bukkit.api.BukkitServerAPI;
import com.br.teagadev.prismamc.commons.bukkit.api.menu.MenuListener;
import com.br.teagadev.prismamc.commons.bukkit.commands.BukkitCommandFramework;
import com.br.teagadev.prismamc.commons.bukkit.custom.events.server.ServerLoadedEvent;
import com.br.teagadev.prismamc.commons.common.serverinfo.ServerType;
import com.br.teagadev.prismamc.commons.common.serverinfo.enums.GameStages;
import com.br.teagadev.prismamc.commons.common.utility.system.DateUtils;
import com.br.teagadev.prismamc.hardcoregames.base.GameType;
import com.br.teagadev.prismamc.hardcoregames.game.GameManager;
import com.br.teagadev.prismamc.hardcoregames.game.types.AutomaticEventType;
import com.br.teagadev.prismamc.hardcoregames.game.types.NormalType;
import com.br.teagadev.prismamc.hardcoregames.listeners.BorderListener;
import com.br.teagadev.prismamc.hardcoregames.listeners.GeneralListeners;
import com.br.teagadev.prismamc.hardcoregames.listeners.PreGameListeners;
import com.br.teagadev.prismamc.hardcoregames.listeners.ScoreboardListeners;
import com.br.teagadev.prismamc.hardcoregames.manager.kit.KitLoader;
import com.br.teagadev.prismamc.hardcoregames.manager.scoreboard.HardcoreGamesScoreboard;
import com.br.teagadev.prismamc.hardcoregames.manager.structures.StructuresManager;
import com.br.teagadev.prismamc.hardcoregames.manager.timer.TimerManager;
import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class HardcoreGamesMain extends JavaPlugin {
   private static HardcoreGamesMain instance;
   private static final TimerManager timerManager = new TimerManager();
   private static final GameManager gameManager = new GameManager();

   public void onLoad() {
      instance = this;
      this.saveDefaultConfig();
      Bukkit.getServer().unloadWorld("world", false);
      this.deleteDir(new File("world"));
   }

   public void onEnable() {
      if (CommonsGeneral.correctlyStarted()) {
         BukkitServerAPI.registerServer();
         Bukkit.setDefaultGameMode(GameMode.ADVENTURE);
         getGameManager().setGameType((GameType)(BukkitMain.getServerType() == ServerType.MINIPRISMA ? new AutomaticEventType() : new NormalType()));
         KitLoader.load();
         BukkitCommandFramework.INSTANCE.loadCommands(this, "com.br.teagadev.prismamc.hardcoregames.commands");
         getGameManager().setStage(GameStages.WAITING);
         getGameManager().getGameType().initialize();
         PluginManager pluginManager = this.getServer().getPluginManager();
         pluginManager.registerEvents(new BorderListener(), this);
         pluginManager.registerEvents(new GeneralListeners(), this);
         pluginManager.registerEvents(new PreGameListeners(), this);
         pluginManager.registerEvents(new ScoreboardListeners(), this);
         StructuresManager.loadItens();
         HardcoreGamesScoreboard.init();
         MenuListener.registerListeners();
         this.cleanWorld();
      } else {
         Bukkit.shutdown();
      }

   }

   public void onDisable() {
   }

   public static void console(String msg) {
      Bukkit.getConsoleSender().sendMessage("[HardcoreGames] " + msg);
   }

   public static void runAsync(Runnable runnable) {
      Bukkit.getScheduler().runTaskAsynchronously(getInstance(), runnable);
   }

   public static void runLater(Runnable runnable) {
      runLater(runnable, 5L);
   }

   public static void runLater(Runnable runnable, long ticks) {
      Bukkit.getScheduler().runTaskLater(getInstance(), runnable, ticks);
   }

   private void cleanWorld() {
      Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(getInstance(), () -> {
         World world = this.getServer().getWorld("world");
         world.setDifficulty(Difficulty.NORMAL);
         world.setAutoSave(false);
         WorldBorder border = world.getWorldBorder();
         border.setCenter(0.0D, 0.0D);
         border.setSize(806.0D);
         world.setSpawnLocation(0, ((World)this.getServer().getWorlds().get(0)).getHighestBlockYAt(0, 0) + 5, 0);
         world.setAutoSave(false);
         ((CraftWorld)world).getHandle().savingDisabled = true;
         long time = System.currentTimeMillis();

         int z;
         int x;
         try {
            for(z = 0; z <= 28; ++z) {
               for(x = 0; x <= 28; ++x) {
                  world.getSpawnLocation().clone().add((double)(z * 16), 0.0D, (double)(x * 16)).getChunk().load(true);
                  world.getSpawnLocation().clone().add((double)(z * -16), 0.0D, (double)(x * -16)).getChunk().load(true);
                  world.getSpawnLocation().clone().add((double)(z * 16), 0.0D, (double)(x * -16)).getChunk().load(true);
                  world.getSpawnLocation().clone().add((double)(z * -16), 0.0D, (double)(x * 16)).getChunk().load(true);
               }

               if (z % 2 == 0) {
                  console("[World] Loading chunks! " + DateUtils.formatSeconds((int)((System.currentTimeMillis() - time) / 1000L)) + " have passed! - used mem: " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 2L / 1048576L);
               }
            }
         } catch (OutOfMemoryError var8) {
            var8.printStackTrace();
         }

         world.setDifficulty(Difficulty.NORMAL);
         if (world.hasStorm()) {
            world.setStorm(false);
         }

         world.setTime(0L);
         world.setWeatherDuration(999999999);
         world.setGameRuleValue("doDaylightCycle", "false");
         world.setGameRuleValue("announceAdvancements", "false");
         console("Criando bordas...");
         time = System.currentTimeMillis();

         int y;
         for(z = -401; z <= 401; ++z) {
            if (z == -401 || z == 401) {
               for(x = -401; x <= 401; ++x) {
                  for(y = 0; y <= 150; ++y) {
                     world.getBlockAt(z, y, x).setType(Material.BEDROCK);
                  }
               }
            }
         }

         for(z = -401; z <= 401; ++z) {
            if (z == -401 || z == 401) {
               for(x = -401; x <= 401; ++x) {
                  for(y = 0; y <= 150; ++y) {
                     world.getBlockAt(x, y, z).setType(Material.BEDROCK);
                  }
               }
            }
         }

         console("Bordas criadas " + DateUtils.formatSeconds((int)((System.currentTimeMillis() - time) / 1000L)));
         world.getEntities().forEach(Entity::remove);
         this.getServer().getPluginManager().callEvent(new ServerLoadedEvent());
      });
   }

   public void deleteDir(File dir) {
      if (dir.isDirectory()) {
         String[] children = dir.list();
         String[] var3 = children;
         int var4 = children.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            String child = var3[var5];
            this.deleteDir(new File(dir, child));
         }
      }

      dir.delete();
   }

   public static HardcoreGamesMain getInstance() {
      return instance;
   }

   public static TimerManager getTimerManager() {
      return timerManager;
   }

   public static GameManager getGameManager() {
      return gameManager;
   }
}