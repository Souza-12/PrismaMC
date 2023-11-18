package com.br.teagadev.prismamc.hardcoregames.listeners;

import com.br.teagadev.prismamc.commons.CommonsConst;
import com.br.teagadev.prismamc.commons.bukkit.queue.PlayerBukkitQueue;
import com.br.teagadev.prismamc.commons.bukkit.queue.QueueType;
import com.br.teagadev.prismamc.hardcoregames.HardcoreGamesMain;
import com.br.teagadev.prismamc.hardcoregames.events.game.GameTimerEvent;
import com.br.teagadev.prismamc.hardcoregames.utility.HardcoreGamesUtility;
import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class EndListener implements Listener {
   public static Player winner = null;

   @EventHandler
   public void onTimer(GameTimerEvent event) {
      if (event.getTime() == 0) {
         this.destroy();
      } else {
         if (event.getTime() < 0) {
            event.setTime(15);
         }

         if (winner != null && winner.isOnline()) {
            Bukkit.broadcastMessage("§a%nick% venceu a partida!".replace("%nick%", winner.getName()));
            HardcoreGamesUtility.spawnRandomFirework(((World)Bukkit.getServer().getWorlds().get(0)).getHighestBlockAt(winner.getLocation().add(0.0D, 0.0D, (double)(CommonsConst.RANDOM.nextInt(5) + 5)).add(0.0D, 5.0D, 0.0D)).getLocation());
            HardcoreGamesUtility.spawnRandomFirework(((World)Bukkit.getServer().getWorlds().get(0)).getHighestBlockAt(winner.getLocation().add((double)(CommonsConst.RANDOM.nextInt(5) + 5), 0.0D, 0.0D).add(0.0D, 5.0D, 0.0D)).getLocation());
            HardcoreGamesUtility.spawnRandomFirework(((World)Bukkit.getServer().getWorlds().get(0)).getHighestBlockAt(winner.getLocation().add((double)(CommonsConst.RANDOM.nextInt(5) + 5), 0.0D, (double)(CommonsConst.RANDOM.nextInt(5) + 5)).add(0.0D, 5.0D, 0.0D)).getLocation());
            HardcoreGamesUtility.spawnRandomFirework(((World)Bukkit.getServer().getWorlds().get(0)).getHighestBlockAt(winner.getLocation().add((double)(-CommonsConst.RANDOM.nextInt(5) - 5), 0.0D, 0.0D).add(0.0D, 5.0D, 0.0D)).getLocation());
            HardcoreGamesUtility.spawnRandomFirework(((World)Bukkit.getServer().getWorlds().get(0)).getHighestBlockAt(winner.getLocation().add(0.0D, 0.0D, (double)(-CommonsConst.RANDOM.nextInt(5) - 5)).add(0.0D, 5.0D, 0.0D)).getLocation());
            HardcoreGamesUtility.spawnRandomFirework(((World)Bukkit.getServer().getWorlds().get(0)).getHighestBlockAt(winner.getLocation().add((double)(-CommonsConst.RANDOM.nextInt(5) - 5), 0.0D, (double)(-CommonsConst.RANDOM.nextInt(5) - 5)).add(0.0D, 5.0D, 0.0D)).getLocation());
            HardcoreGamesUtility.spawnRandomFirework(((World)Bukkit.getServer().getWorlds().get(0)).getHighestBlockAt(winner.getLocation().add((double)(-CommonsConst.RANDOM.nextInt(5) - 5), 0.0D, (double)(CommonsConst.RANDOM.nextInt(5) + 5)).add(0.0D, 5.0D, 0.0D)).getLocation());
            HardcoreGamesUtility.spawnRandomFirework(((World)Bukkit.getServer().getWorlds().get(0)).getHighestBlockAt(winner.getLocation().add((double)(CommonsConst.RANDOM.nextInt(5) + 5), 0.0D, (double)(-CommonsConst.RANDOM.nextInt(5) - 5)).add(0.0D, 5.0D, 0.0D)).getLocation());
         } else {
            this.destroy();
         }

      }
   }

   @EventHandler
   public void onDrop(PlayerDropItemEvent event) {
      event.setCancelled(true);
   }

   @EventHandler
   public void onPickup(PlayerPickupItemEvent event) {
      event.setCancelled(true);
   }

   @EventHandler
   public void onPlace(BlockPlaceEvent event) {
      event.setCancelled(true);
   }

   @EventHandler
   public void onBreak(BlockBreakEvent event) {
      event.setCancelled(true);
   }

   @EventHandler
   public void onItemSpawn(ItemSpawnEvent event) {
      event.setCancelled(true);
   }

   @EventHandler
   public void onExplosion(ExplosionPrimeEvent event) {
      event.setCancelled(true);
   }

   @EventHandler
   public void onCreatureSpawn(CreatureSpawnEvent event) {
      event.setCancelled(true);
   }

   @EventHandler
   public void onWeatherChange(WeatherChangeEvent event) {
      event.setCancelled(event.toWeatherState());
   }

   @EventHandler
   public void onEntityDamage(EntityDamageEvent event) {
      event.setCancelled(true);
   }

   @EventHandler
   public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
      event.setCancelled(true);
   }

   @EventHandler
   public void onIgnite(BlockIgniteEvent event) {
      event.setCancelled(true);
   }

   @EventHandler
   public void craftItem(CraftItemEvent event) {
      event.setCancelled(true);
   }

   @EventHandler
   public void onFood(FoodLevelChangeEvent event) {
      event.setCancelled(true);
   }

   private void destroy() {
      HandlerList.unregisterAll(this);
      HardcoreGamesMain.console("Removing Listener from EndListener");
      PlayerBukkitQueue queue = new PlayerBukkitQueue(10, true, QueueType.CONNECT);
      queue.setDestroyOnFinish(true);
      queue.setStopOnFinish(true);
      Iterator var2 = Bukkit.getOnlinePlayers().iterator();

      while(var2.hasNext()) {
         Player player = (Player)var2.next();
         queue.addToQueue(player, "LobbyHardcoreGames");
      }

      queue.start();
   }
}