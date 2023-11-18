package com.br.teagadev.prismamc.commons.bukkit.listeners;

import com.br.teagadev.prismamc.commons.bukkit.custom.events.player.PlayerChangeTagEvent;
import com.br.teagadev.prismamc.commons.bukkit.scoreboard.sidebar.SidebarManager;
import com.br.teagadev.prismamc.commons.bukkit.scoreboard.tag.TagManager;
import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ScoreboardListener implements Listener {
   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public void onJoin(PlayerJoinEvent event) {
      event.setJoinMessage((String)null);
      event.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
      SidebarManager.handleJoin(event.getPlayer());
   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public void onPlayerQuit(PlayerQuitEvent event) {
      event.setQuitMessage((String)null);
      Scoreboard board = event.getPlayer().getScoreboard();
      if (board != null) {
         Iterator var3 = board.getTeams().iterator();

         while(var3.hasNext()) {
            Team t = (Team)var3.next();
            t.unregister();
         }

         var3 = board.getObjectives().iterator();

         while(var3.hasNext()) {
            Objective ob = (Objective)var3.next();
            ob.unregister();
         }
      }

      SidebarManager.handleQuit(event.getPlayer().getUniqueId());
      event.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
      TagManager.removePlayerTag(event.getPlayer().getName());
   }

   @EventHandler
   public void onPlayerChangeTagCancelEvent(PlayerChangeTagEvent event) {
      Player player = event.getPlayer();
      if (event.isCancelled()) {
         player.sendMessage("§cVocê não pode trocar sua tag.");
      }

   }

   @EventHandler(
      priority = EventPriority.MONITOR,
      ignoreCancelled = true
   )
   public void onPlayerChangeTag(PlayerChangeTagEvent event) {
      Player player = event.getPlayer();
      if (player != null) {
         if (!event.isForced()) {
            player.sendMessage("§aVocê está utilizando a tag %tag%".replace("%tag%", event.getNewTag().getColor() + event.getNewTag().getName()));
         }

         TagManager.setTag(player, event.getNewTag());
      }
   }
}