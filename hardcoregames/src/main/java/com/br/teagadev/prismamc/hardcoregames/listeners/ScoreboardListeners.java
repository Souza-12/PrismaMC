package com.br.teagadev.prismamc.hardcoregames.listeners;

import com.br.teagadev.prismamc.commons.bukkit.custom.events.server.ServerStopEvent;
import com.br.teagadev.prismamc.commons.common.serverinfo.enums.GameStages;
import com.br.teagadev.prismamc.hardcoregames.HardcoreGamesMain;
import com.br.teagadev.prismamc.hardcoregames.events.game.GameStageChangeEvent;
import com.br.teagadev.prismamc.hardcoregames.events.game.GameTimerEvent;
import com.br.teagadev.prismamc.hardcoregames.manager.scoreboard.HardcoreGamesScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ScoreboardListeners implements Listener {
   @EventHandler(
      priority = EventPriority.MONITOR
   )
   public void onJoin(PlayerJoinEvent event) {
      this.update(1);
   }

   @EventHandler(
      priority = EventPriority.MONITOR
   )
   public void onQuit(PlayerQuitEvent event) {
      this.update(1);
   }

   @EventHandler(
      priority = EventPriority.MONITOR
   )
   public void onTimer(GameTimerEvent event) {
      this.update(2);
   }

   @EventHandler(
      priority = EventPriority.MONITOR
   )
   public void onDeath(PlayerDeathEvent event) {
      this.update(1);
   }

   public void update(int code) {
      HardcoreGamesMain.runLater(() -> {
         if (code == 1) {
            Bukkit.getOnlinePlayers().stream().filter((check) -> {
               return check != null;
            }).filter((check) -> {
               return check.isOnline();
            }).forEach((onlines) -> {
               HardcoreGamesScoreboard.getScoreBoardCommon().updateGaming(onlines);
            });
         } else if (code == 2) {
            Bukkit.getOnlinePlayers().stream().filter((check) -> {
               return check != null;
            }).filter((check) -> {
               return check.isOnline();
            }).forEach((onlines) -> {
               HardcoreGamesScoreboard.getScoreBoardCommon().updateTime(onlines);
            });
         }

      });
   }

   @EventHandler
   public void onStop(ServerStopEvent event) {
      HandlerList.unregisterAll(this);
   }

   @EventHandler
   public void onGameEnd(GameStageChangeEvent event) {
      if (event.getNewStage() == GameStages.END) {
         HardcoreGamesMain.console("Removing listeners from ScoreboardListeners");
         HandlerList.unregisterAll(this);
      }

   }
}