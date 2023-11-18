package com.br.teagadev.prismamc.hardcoregames.listeners;

import com.br.teagadev.prismamc.commons.bukkit.api.BukkitServerAPI;
import com.br.teagadev.prismamc.commons.bukkit.custom.events.BukkitUpdateEvent;
import com.br.teagadev.prismamc.commons.bukkit.custom.events.BukkitUpdateEvent.UpdateType;
import com.br.teagadev.prismamc.commons.common.serverinfo.enums.GameStages;
import com.br.teagadev.prismamc.hardcoregames.HardcoreGamesMain;
import com.br.teagadev.prismamc.hardcoregames.manager.gamer.Gamer;
import com.br.teagadev.prismamc.hardcoregames.manager.gamer.GamerManager;
import com.br.teagadev.prismamc.hardcoregames.utility.HardcoreGamesUtility;
import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BorderListener implements Listener {
   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onUpdate(BukkitUpdateEvent event) {
      if (event.getType() == UpdateType.SEGUNDO) {
         GameStages stage = HardcoreGamesMain.getGameManager().getStage();
         Iterator var3 = Bukkit.getOnlinePlayers().iterator();

         while(var3.hasNext()) {
            Player onlines = (Player)var3.next();
            if (stage == GameStages.WAITING) {
               if (this.isNotInBoard(onlines.getLocation(), 120)) {
                  onlines.teleport(HardcoreGamesUtility.getRandomLocation(40));
               }
            } else if (stage == GameStages.INVINCIBILITY) {
               this.handleBoardInvincibility(onlines);
            } else {
               this.handleBoard(onlines);
            }
         }

         stage = null;
      }
   }

   private void handleBoardInvincibility(Player player) {
      if (this.isNotInBoard(player.getLocation(), 401)) {
         Gamer gamer = GamerManager.getGamer(player.getUniqueId());
         if (gamer != null) {
            if (gamer.isPlaying()) {
               player.sendMessage("§aVocê passou da borda e automaticamente foi desclassificado da partida!");
               player.teleport(HardcoreGamesUtility.getRandomLocation(160));
               HardcoreGamesMain.getGameManager().getGameType().setEspectador(player);
               if (!HardcoreGamesUtility.availableToSpec(player)) {
                  BukkitServerAPI.redirectPlayer(player, "LobbyHG", true);
               }
            } else {
               player.teleport(HardcoreGamesUtility.getRandomLocation(160));
            }

            gamer = null;
         } else {
            player.kickPlayer("WTF? #1");
         }
      }

   }

   private void handleBoard(Player player) {
      if (this.isNotInBoard(player.getLocation(), 400)) {
         if (this.inLauncher(player)) {
            return;
         }

         Gamer gamer = GamerManager.getGamer(player.getUniqueId());
         if (gamer != null) {
            if (gamer.isPlaying()) {
               player.setFireTicks(100);
               player.damage(3.0D);
               player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20, 2));
            } else {
               player.teleport(HardcoreGamesUtility.getRandomLocation(160));
            }

            gamer = null;
         } else {
            player.kickPlayer("WTF? #2");
         }
      }

   }

   private boolean inLauncher(Player player) {
      if (!player.hasMetadata("nofall")) {
         return false;
      } else {
         Long time = ((MetadataValue)player.getMetadata("nofall.time").get(0)).asLong();
         return time + 6200L > System.currentTimeMillis();
      }
   }

   private boolean isNotInBoard(Location loc, int size) {
      return loc.getBlockX() > size || loc.getBlockX() < -size || loc.getBlockZ() > size || loc.getBlockY() > 128 || loc.getBlockZ() < -size;
   }
}