package com.br.teagadev.prismamc.commons.bukkit.api.npc.listener;

import com.br.teagadev.prismamc.commons.bukkit.BukkitMain;
import com.br.teagadev.prismamc.commons.bukkit.api.npc.NPCManager;
import com.br.teagadev.prismamc.commons.bukkit.api.npc.api.NPC;
import com.br.teagadev.prismamc.commons.bukkit.utility.LocationUtil;
import java.util.Iterator;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

public class NPCListener implements Listener {
   private static final String LOCKED_TAG = "LOCKED.NPCS.TIME";

   public static void lock(Player player) {
      lock(player, 2000L);
   }

   public static void lock(Player player, Long time) {
      player.setMetadata("LOCKED.NPCS.TIME", new FixedMetadataValue(BukkitMain.getInstance(), System.currentTimeMillis() + time));
   }

   public static void removeLock(Player player) {
      player.removeMetadata("LOCKED.NPCS.TIME", BukkitMain.getInstance());
   }

   @EventHandler(
      priority = EventPriority.MONITOR
   )
   public void onJoin(PlayerJoinEvent event) {
      this.handleNPC(event.getPlayer(), event.getPlayer().getLocation());
   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public void onPlayerQuit(PlayerQuitEvent event) {
      Player player = event.getPlayer();
      Iterator var3 = NPCManager.getAllNPCs().iterator();

      while(var3.hasNext()) {
         NPC npc = (NPC)var3.next();
         npc.getShown().remove(player.getUniqueId());
      }

   }

   @EventHandler
   public void onRealMovement(PlayerMoveEvent event) {
      if (LocationUtil.isRealMovement(event.getFrom(), event.getTo())) {
         Player player = event.getPlayer();
         if (!this.isLocked(player)) {
            this.handleNPC(player, event.getTo());
         }
      }
   }

   @EventHandler
   public void onTeleport(PlayerTeleportEvent e) {
      Player player = e.getPlayer();
      if (!this.isLocked(player)) {
         this.handleNPC(player, e.getTo());
      }
   }

   public void handleNPC(Player player, Location to) {
      lock(player);
      Iterator var3 = NPCManager.getAllNPCs().iterator();

      while(var3.hasNext()) {
         NPC npc = (NPC)var3.next();
         Location location = npc.getLocation();
         if (location.getWorld() == to.getWorld()) {
            double distancia = location.distance(to);
            if (distancia <= 80.0D) {
               if (!npc.getShown().contains(player.getUniqueId())) {
                  npc.show(player);
                  npc.getShown().add(player.getUniqueId());
               }
            } else if (npc.getShown().contains(player.getUniqueId())) {
               npc.hide(player);
               npc.getShown().remove(player.getUniqueId());
            }
         }
      }

      removeLock(player);
   }

   private boolean isLocked(Player player) {
      if (!player.hasMetadata("LOCKED.NPCS.TIME")) {
         return false;
      } else {
         return ((MetadataValue)player.getMetadata("LOCKED.NPCS.TIME").get(0)).asLong() > System.currentTimeMillis();
      }
   }
}