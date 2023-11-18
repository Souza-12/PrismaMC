package com.br.teagadev.prismamc.hardcoregames.listeners;

import com.br.teagadev.prismamc.commons.bukkit.BukkitMain;
import com.br.teagadev.prismamc.commons.common.serverinfo.ServerType;
import com.br.teagadev.prismamc.commons.common.serverinfo.enums.GameStages;
import com.br.teagadev.prismamc.hardcoregames.HardcoreGamesMain;
import com.br.teagadev.prismamc.hardcoregames.events.game.GameInvincibilityEndEvent;
import com.br.teagadev.prismamc.hardcoregames.events.game.GameStageChangeEvent;
import com.br.teagadev.prismamc.hardcoregames.events.game.GameTimerEvent;
import com.br.teagadev.prismamc.hardcoregames.manager.gamer.Gamer;
import com.br.teagadev.prismamc.hardcoregames.manager.gamer.GamerManager;
import com.br.teagadev.prismamc.hardcoregames.manager.scoreboard.HardcoreGamesScoreboard;
import com.br.teagadev.prismamc.hardcoregames.manager.timer.TimerType;
import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.plugin.PluginManager;

public class InvincibilityListener implements Listener {
   @EventHandler
   public void onTimer(GameTimerEvent event) {
      if (event.getTime() == 0) {
         PluginManager pluginManager = Bukkit.getServer().getPluginManager();
         pluginManager.registerEvents(new GameListener(), HardcoreGamesMain.getInstance());
         pluginManager.callEvent(new GameInvincibilityEndEvent());
         pluginManager.callEvent(new GameStageChangeEvent(GameStages.INVINCIBILITY, GameStages.PLAYING));
      } else {
         event.checkMessage();
      }

   }

   @EventHandler
   public void onInvincibilityEnd(GameInvincibilityEndEvent event) {
      int time = 120;
      if (BukkitMain.getServerType() == ServerType.MINIPRISMA) {
         time = 60;
      }

      HardcoreGamesMain.getGameManager().setStage(GameStages.PLAYING);
      HardcoreGamesMain.getTimerManager().updateTime(time);
      HardcoreGamesMain.getTimerManager().setTimerType(TimerType.COUNT_UP);
      Iterator var3 = GamerManager.getGamers().iterator();

      while(var3.hasNext()) {
         Gamer gamer = (Gamer)var3.next();
         if (gamer != null && gamer.isOnline()) {
            HardcoreGamesScoreboard.createScoreboard(gamer);
            gamer.getPlayer().playSound(gamer.getPlayer().getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
         }
      }

      Bukkit.broadcastMessage("§aA invencibilidade acabou. Agora é a hora que a verdadeira batalha começa!");
      HardcoreGamesMain.getGameManager().getGameType().checkWin();
   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public void onEntityDamage(EntityDamageEvent event) {
      event.setCancelled(true);
   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
      event.setCancelled(true);
   }

   @EventHandler
   public void onWeatherChange(WeatherChangeEvent event) {
      event.setCancelled(event.toWeatherState());
   }

   @EventHandler
   public void onCreatureSpawn(CreatureSpawnEvent event) {
      event.setCancelled(true);
   }

   @EventHandler
   public void onFood(FoodLevelChangeEvent event) {
      event.setCancelled(true);
   }

   @EventHandler
   public void onInvencibilityEnd(GameStageChangeEvent event) {
      if (event.getLastStage() == GameStages.INVINCIBILITY && event.getNewStage() == GameStages.PLAYING) {
         HardcoreGamesMain.console("Removing listeners from InvincibilityListener");
         HandlerList.unregisterAll(this);
      }

   }
}